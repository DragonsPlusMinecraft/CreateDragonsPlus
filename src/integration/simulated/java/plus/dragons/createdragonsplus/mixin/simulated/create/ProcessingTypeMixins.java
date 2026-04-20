package plus.dragons.createdragonsplus.mixin.simulated.create;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.ColoringFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.ending.EndingFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.sanding.SandingFanProcessingType;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.integration.simulated.common.kinetics.fan.IFanProcessingTypeSimulatedExtension;
import plus.dragons.createdragonsplus.integration.simulated.common.registry.CDPSEDataMaps;
import plus.dragons.createdragonsplus.integration.simulated.config.CDPSEConfig;

import java.util.HashMap;
import java.util.List;

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
            if (CDPSEConfig.server().airCurrentBlockInteraction.bulkBlastingIgniteBlock.get()){
                if (blockState.is(Blocks.CAMPFIRE) || blockState.is(Blocks.SOUL_CAMPFIRE)) {
                    return !blockState.getValue(CampfireBlock.LIT) && level.getFluidState(pos).isEmpty();
                } else if (blockState.is(AllBlocks.BLAZE_BURNER)) {
                    return blockState.getValue(BlazeBurnerBlock.HEAT_LEVEL) == BlazeBurnerBlock.HeatLevel.NONE;
                }
            }
            if(CDPSEConfig.server().airCurrentBlockInteraction.bulkBlastingSpreadFire.get())
                return blockState.ignitedByLava(level, pos, Direction.getRandom(level.random));
            return false;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_BLASTING);
            if(result!=null){
                level.setBlockAndUpdate(pos, result.defaultBlockState());
                return;
            }

            if (CDPSEConfig.server().airCurrentBlockInteraction.bulkBlastingIgniteBlock.get()){
                if (blockState.is(Blocks.CAMPFIRE) || blockState.is(Blocks.SOUL_CAMPFIRE)) {
                    level.setBlockAndUpdate(pos, blockState.setValue(CampfireBlock.LIT,true));
                    return;
                } else if (blockState.is(AllBlocks.BLAZE_BURNER)) {
                    level.setBlockAndUpdate(pos, AllBlocks.LIT_BLAZE_BURNER.getDefaultState());
                    return;
                }
            }

            if(CDPSEConfig.server().airCurrentBlockInteraction.bulkBlastingSpreadFire.get()) {
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
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_SPLASHING);
            if(result!=null) return true;

            if (CDPSEConfig.server().airCurrentBlockInteraction.bulkSplashingExtinguishFire.get()){
                if (blockState.is(Blocks.CAMPFIRE) || blockState.is(Blocks.SOUL_CAMPFIRE)) {
                    return blockState.getValue(CampfireBlock.LIT);
                } else return blockState.is(AllBlocks.LIT_BLAZE_BURNER);
            }

            return false;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_SPLASHING);
            if(result!=null)
                level.setBlockAndUpdate(pos, result.defaultBlockState());

            if (CDPSEConfig.server().airCurrentBlockInteraction.bulkSplashingExtinguishFire.get()){
                if (blockState.is(Blocks.CAMPFIRE) || blockState.is(Blocks.SOUL_CAMPFIRE)) {
                    level.setBlockAndUpdate(pos, blockState.setValue(CampfireBlock.LIT,false));
                } else if (blockState.is(AllBlocks.LIT_BLAZE_BURNER)) {
                    level.setBlockAndUpdate(pos, AllBlocks.BLAZE_BURNER.getDefaultState());
                }
            }
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
    @Mixin(ColoringFanProcessingType.class)
    public static abstract class ColoringTypeMixin implements IFanProcessingTypeSimulatedExtension {

        @Shadow
        @Nullable
        public abstract List<ItemStack> process(ItemStack stack, Level level);

        @Unique
        private final HashMap<Block, Block> transformingResultCache = new HashMap<>();

        @Inject(method = "recreateCache", at = @At(value = "RETURN"), remap = false)
        private void recreateCache$thisCache(CallbackInfo ci) {
            transformingResultCache.clear();
        }

        @Override
        public boolean active() {
            return CDPSEConfig.server().airCurrentBlockInteraction.enableBulkColoringBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            if(!blockState.getBlock().asItem().equals(Items.AIR)){
                if(transformingResultCache.containsKey(blockState.getBlock()))
                    return !transformingResultCache.get(blockState.getBlock()).equals(Blocks.AIR);
                else{
                    var result = process(new ItemStack(blockState.getBlock()), level);
                    if(result==null || result.size()!=1){
                        transformingResultCache.put(blockState.getBlock(), Blocks.AIR);
                        return false;
                    }
                    else {
                        transformingResultCache.put(blockState.getBlock(),Block.byItem(result.get(0).getItem()));
                        return !Block.byItem(result.get(0).getItem()).equals(Items.AIR);
                    }
                }
            }
            return false;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            if(transformingResultCache.containsKey(blockState.getBlock())){
                level.setBlockAndUpdate(pos, transformingResultCache.get(blockState.getBlock()).defaultBlockState());
            }
        }
    }
}
