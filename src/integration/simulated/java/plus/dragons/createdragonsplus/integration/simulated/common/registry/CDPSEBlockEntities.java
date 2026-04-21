package plus.dragons.createdragonsplus.integration.simulated.common.registry;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank.FragileFluidTankBlockEntity;
import plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank.FragileFluidTankRenderer;

import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;

public class CDPSEBlockEntities {
    public static final BlockEntityEntry<FragileFluidTankBlockEntity> FRAGILE_FLUID_TANK = REGISTRATE
            .blockEntity("fragile_fluid_tank", FragileFluidTankBlockEntity::new)
            .renderer(() -> FragileFluidTankRenderer::new)
            .validBlocks(CDPSEBlocks.FRAGILE_FLUID_TANK, CDPSEBlocks.LEVITITE_FRAGILE_FLUID_TANK)
            .register();

    public static void register(IEventBus modBus) {
        modBus.register(CDPSEBlockEntities.class);
    }

    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                FRAGILE_FLUID_TANK.get(), FragileFluidTankBlockEntity::getFluidHandler);
    }
}
