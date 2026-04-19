package plus.dragons.createdragonsplus.mixin.simulated.create;

import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;
import org.spongepowered.asm.mixin.Mixin;
import plus.dragons.createdragonsplus.common.kinetics.fan.ending.EndingFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.sanding.SandingFanProcessingType;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.integration.simulated.common.kinetics.fan.IFanProcessingTypeSimulatedExtension;
import plus.dragons.createdragonsplus.integration.simulated.common.registry.CDPSEDataMaps;
import plus.dragons.createdragonsplus.integration.simulated.config.CDPSEConfig;

public class ProcessingTypeMixins {
    @Restriction(require = @Condition(ModIntegration.Constants.AERONAUTICS))
    @Mixin(AllFanProcessingTypes.BlastingType.class)
    public static class BlastingTypeMixin implements IFanProcessingTypeSimulatedExtension {

        @Override
        public boolean active() {
            return CDPSEConfig.server().airCurrentBlockInteraction.enableBulkBlastingBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            if(blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_BLASTING)!=null)
                return true;
            else if(CDPSEConfig.server().airCurrentBlockInteraction.bulkBlastingIgniteBlock.get()){
                return blockState.ignitedByLava(level, pos, Direction.getRandom(level.random));
            } return false;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_BLASTING);
            if(result!=null){
                level.setBlockAndUpdate(pos, result.defaultBlockState());
            } else {
                pos = pos.relative(Direction.getRandom(level.random));
                if(level.getBlockState(pos).isAir())
                    level.setBlockAndUpdate(pos, EventHooks.fireFluidPlaceBlockEvent(level, pos, pos, BaseFireBlock.getState(level, pos)));
            }
        }
    }

    @Restriction(require = @Condition(ModIntegration.Constants.AERONAUTICS))
    @Mixin(AllFanProcessingTypes.SmokingType.class)
    public static class SmokingTypeMixin implements IFanProcessingTypeSimulatedExtension {

        @Override
        public boolean active() {
            return CDPSEConfig.server().airCurrentBlockInteraction.enableBulkSmokingBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
           return blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_SMOKING)!=null;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_SMOKING);
            if(result!=null)
                level.setBlockAndUpdate(pos, result.defaultBlockState());
        }
    }

    @Restriction(require = @Condition(ModIntegration.Constants.AERONAUTICS))
    @Mixin(AllFanProcessingTypes.SplashingType.class)
    public static class SplashingTypeMixin implements IFanProcessingTypeSimulatedExtension {

        @Override
        public boolean active() {
            return CDPSEConfig.server().airCurrentBlockInteraction.enableBulkSplashingBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            return blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_SPLASHING)!=null;
            // TODO
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_SPLASHING);
            if(result!=null)
                level.setBlockAndUpdate(pos, result.defaultBlockState());
            // TODO
        }
    }

    @Restriction(require = @Condition(ModIntegration.Constants.AERONAUTICS))
    @Mixin(AllFanProcessingTypes.HauntingType.class)
    public static class HauntingTypeMixin implements IFanProcessingTypeSimulatedExtension {

        @Override
        public boolean active() {
            return CDPSEConfig.server().airCurrentBlockInteraction.enableBulkHauntingBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            return blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_HAUNTING)!=null;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_HAUNTING);
            if(result!=null)
                level.setBlockAndUpdate(pos, result.defaultBlockState());
        }
    }

    @Restriction(require = @Condition(ModIntegration.Constants.AERONAUTICS))
    @Mixin(FreezingFanProcessingType.class)
    public static class FreezingTypeMixin implements IFanProcessingTypeSimulatedExtension {

        @Override
        public boolean active() {
            return CDPSEConfig.server().airCurrentBlockInteraction.enableBulkFreezingBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            return blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_FREEZING)!=null;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_FREEZING);
            if(result!=null)
                level.setBlockAndUpdate(pos, result.defaultBlockState());
        }
    }

    @Restriction(require = @Condition(ModIntegration.Constants.AERONAUTICS))
    @Mixin(EndingFanProcessingType.class)
    public static class EndingTypeMixin implements IFanProcessingTypeSimulatedExtension {

        @Override
        public boolean active() {
            return CDPSEConfig.server().airCurrentBlockInteraction.enableBulkEndingBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            return blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_ENDING)!=null;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_ENDING);
            if(result!=null)
                level.setBlockAndUpdate(pos, result.defaultBlockState());
        }
    }

    @Restriction(require = @Condition(ModIntegration.Constants.AERONAUTICS))
    @Mixin(SandingFanProcessingType.class)
    public static class SandingTypeMixin implements IFanProcessingTypeSimulatedExtension {

        @Override
        public boolean active() {
            return CDPSEConfig.server().airCurrentBlockInteraction.enableBulkSandingBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            return blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_SANDING)!=null;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_SANDING);
            if(result!=null)
                level.setBlockAndUpdate(pos, result.defaultBlockState());
        }
    }

    @Restriction(require = @Condition(ModIntegration.Constants.AERONAUTICS))
    @Mixin(SandingFanProcessingType.class)
    public static class ColoringTypeMixin implements IFanProcessingTypeSimulatedExtension {

        @Override
        public boolean active() {
            return CDPSEConfig.server().airCurrentBlockInteraction.enableBulkColoringBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            // TODO gen from recipe and cache them
            return false;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            // TODO
        }
    }
}
