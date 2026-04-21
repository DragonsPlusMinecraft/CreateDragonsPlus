package plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.integration.simulated.config.CDPSEConfig;

import java.util.List;

public class FragileFluidTankBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation  {
    protected SmartFluidTankBehaviour tank;

    public FragileFluidTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, CDPSEConfig.fluid().fragileFluidTankCapacity.get());
        behaviours.add(tank);
    }

    public @Nullable IFluidHandler getFluidHandler(@Nullable Direction side) {
        return tank.getCapability();
    }

    public FluidStack getFluidInTank() {
        return tank.getPrimaryHandler().getFluid();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return containedFluidTooltip(tooltip, isPlayerSneaking, tank.getPrimaryHandler());
    }
}
