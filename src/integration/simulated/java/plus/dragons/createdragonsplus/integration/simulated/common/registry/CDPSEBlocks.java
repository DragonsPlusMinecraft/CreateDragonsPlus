package plus.dragons.createdragonsplus.integration.simulated.common.registry;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank.FragileFluidTankBlock;


import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;

public class CDPSEBlocks {
    public static final BlockEntry<FragileFluidTankBlock> FRAGILE_FLUID_TANK = REGISTRATE
            .block("fragile_fluid_tank", FragileFluidTankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .asOptional()
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.simpleBlock(c.get(), AssetLookup.standardModel(c, p)))
            .simpleItem()
            .register();

    public static final BlockEntry<FragileFluidTankBlock> LEVITITE_FRAGILE_FLUID_TANK = REGISTRATE
            .block("levitite_fragile_fluid_tank", FragileFluidTankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .asOptional()
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.simpleBlock(c.get(), AssetLookup.standardModel(c, p)))
            .simpleItem()
            .register();

    public static void register(IEventBus modBus) {}
}
