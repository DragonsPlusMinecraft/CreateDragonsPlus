package plus.dragons.createdragonsplus.common.fluids.tank;

import com.google.common.util.concurrent.Runnables;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.function.Consumer;

public class FluidTankBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<FluidTankBehaviour> TYPE = new BehaviourType<>();
    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;
    protected FluidTank[] handlers;
    protected TankSegment[] tanks;
    protected IFluidHandler capability;
    protected Runnable fluidUpdateCallback;

    public FluidTankBehaviour(SmartBlockEntity blockEntity, FluidTank[] handlers, boolean enforceVariety) {
        super(blockEntity);
        this.handlers = handlers;
        this.tanks = new TankSegment[handlers.length];
        for (int i = 0; i < handlers.length; i++) {
            TankSegment tankSegment = new TankSegment(handlers[i]);
            this.tanks[i] = tankSegment;
            handlers[i] = tankSegment.tank;
        }
        capability = Util.make(new CombinedTankWrapper(handlers), tank -> {
            if (enforceVariety)
                tank.enforceVariety();
        });
        fluidUpdateCallback = Runnables.doNothing();
    }

    public FluidTankBehaviour(SmartBlockEntity blockEntity, FluidTank tank) {
        super(blockEntity);
        this.handlers = new FluidTank[]{tank};
        this.tanks = new TankSegment[]{new TankSegment(tank)};
        capability = tank;
        fluidUpdateCallback = Runnables.doNothing();
    }

    public FluidTankBehaviour whenFluidUpdates(Runnable fluidUpdateCallback) {
        this.fluidUpdateCallback = fluidUpdateCallback;
        return this;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Override
    public void initialize() {
        super.initialize();
        if (getWorld().isClientSide)
            return;
        forEach(segment -> {
            segment.fluidLevel.forceNextSync();
            segment.onFluidStackChanged();
        });
    }

    @Override
    public void tick() {
        super.tick();

        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync)
                updateFluids();
        }

        forEach(segment -> segment.getFluidLevel().tickChaser());
    }

    public void sendDataImmediately() {
        syncCooldown = 0;
        queuedSync = false;
        updateFluids();
    }

    public void sendDataLazily() {
        if (syncCooldown > 0) {
            queuedSync = true;
            return;
        }
        updateFluids();
        queuedSync = false;
        syncCooldown = SYNC_RATE;
    }

    protected void updateFluids() {
        fluidUpdateCallback.run();
        blockEntity.sendData();
        blockEntity.setChanged();
    }

    @Override
    public void unload() {
        super.unload();
        var level = blockEntity.getLevel();
        assert level != null;
        level.invalidateCapabilities(getPos());
    }

    public FluidTank getPrimaryHandler() {
        return handlers[0];
    }

    public TankSegment getPrimaryTank() {
        return tanks[0];
    }

    public FluidTank[] getHandlers() {
        return handlers;
    }

    public TankSegment[] getTanks() {
        return tanks;
    }

    public boolean isEmpty() {
        for (TankSegment tankSegment : tanks)
            if (!tankSegment.tank.isEmpty())
                return false;
        return true;
    }

    public void forEach(Consumer<TankSegment> action) {
        for (TankSegment tankSegment : tanks)
            action.accept(tankSegment);
    }

    public IFluidHandler getCapability() {
        return capability;
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        ListTag tanksNBT = new ListTag();
        forEach(segment -> tanksNBT.add(segment.writeNBT(registries)));
        nbt.put(getType().getName() + "Tanks", tanksNBT);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        MutableInt index = new MutableInt(0);
        NBTHelper.iterateCompoundList(nbt.getList(getType().getName() + "Tanks", Tag.TAG_COMPOUND), tank -> {
            if (index.intValue() >= tanks.length)
                return;
            tanks[index.intValue()].readNBT(tank, registries, clientPacket);
            index.increment();
        });
    }

    public class TankSegment {
        public final FluidTank tank;
        protected LerpedFloat fluidLevel;
        protected FluidStack renderedFluid;

        public TankSegment(FluidTank tank) {
            this.tank = tank;
            fluidLevel = LerpedFloat.linear()
                    .startWithValue(0)
                    .chase(0, .25, Chaser.EXP);
            renderedFluid = FluidStack.EMPTY;
        }

        public void onFluidStackChanged() {
            if (!blockEntity.hasLevel())
                return;
            fluidLevel.chase(tank.getFluidAmount() / (float) tank.getCapacity(), .25, Chaser.EXP);
            if (!getWorld().isClientSide)
                sendDataLazily();
            if (blockEntity.isVirtual() && !tank.getFluid().isEmpty())
                renderedFluid = tank.getFluid();
        }

        public FluidStack getRenderedFluid() {
            return renderedFluid;
        }

        public LerpedFloat getFluidLevel() {
            return fluidLevel;
        }

        public float getTotalUnits(float partialTicks) {
            return fluidLevel.getValue(partialTicks) * tank.getCapacity();
        }

        public CompoundTag writeNBT(HolderLookup.Provider registries) {
            CompoundTag compound = new CompoundTag();
            compound.put("TankContent", tank.writeToNBT(registries, new CompoundTag()));
            compound.put("Level", fluidLevel.writeNBT());
            return compound;
        }

        public void readNBT(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
            tank.readFromNBT(registries, compound.getCompound("TankContent"));
            fluidLevel.readNBT(compound.getCompound("Level"), clientPacket);
            if (!tank.getFluid().isEmpty())
                renderedFluid = tank.getFluid();
        }

        public boolean isEmpty(float partialTicks) {
            FluidStack renderedFluid = getRenderedFluid();
            if (renderedFluid.isEmpty())
                return true;
            float units = getTotalUnits(partialTicks);
            return units < 1;
        }
    }
}
