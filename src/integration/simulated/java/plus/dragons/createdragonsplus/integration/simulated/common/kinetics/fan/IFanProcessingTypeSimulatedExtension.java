package plus.dragons.createdragonsplus.integration.simulated.common.kinetics.fan;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IFanProcessingTypeSimulatedExtension {

    boolean active();

    boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState);

    void affectBlock(Level level, BlockPos pos, BlockState blockState);
}
