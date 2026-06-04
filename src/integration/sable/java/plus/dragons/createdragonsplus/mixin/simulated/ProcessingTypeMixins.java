/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package plus.dragons.createdragonsplus.mixin.simulated;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import java.util.HashMap;
import java.util.List;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import plus.dragons.createdragonsplus.integration.simulated.api.kinetics.fan.FanProcessingTypeSimulatedExtension;
import plus.dragons.createdragonsplus.integration.simulated.common.registry.CDPSEDataMaps;
import plus.dragons.createdragonsplus.integration.simulated.config.CDPSEConfig;

public class ProcessingTypeMixins {
    @Restriction(require = @Condition(ModIntegration.Constants.SABLE))
    @Mixin(AllFanProcessingTypes.BlastingType.class)
    public static class BlastingTypeMixin implements FanProcessingTypeSimulatedExtension {
        @Override
        public boolean active() {
            return CDPSEConfig.airCurrentBlockInteraction().enableBulkBlastingBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            if (blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_BLASTING) != null)
                return true;
            if (CDPSEConfig.airCurrentBlockInteraction().bulkBlastingIgniteBlock.get()) {
                if (blockState.is(Blocks.CAMPFIRE) || blockState.is(Blocks.SOUL_CAMPFIRE)) {
                    return !blockState.getValue(CampfireBlock.LIT) && level.getFluidState(pos).isEmpty();
                } else if (blockState.is(AllBlocks.BLAZE_BURNER)) {
                    return blockState.getValue(BlazeBurnerBlock.HEAT_LEVEL) == BlazeBurnerBlock.HeatLevel.NONE;
                }
            }
            if (CDPSEConfig.airCurrentBlockInteraction().bulkBlastingSpreadFire.get())
                return blockState.ignitedByLava(level, pos, Direction.getRandom(level.random));
            return false;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_BLASTING);
            if (result != null) {
                level.setBlockAndUpdate(pos, result.defaultBlockState());
                return;
            }

            if (CDPSEConfig.airCurrentBlockInteraction().bulkBlastingIgniteBlock.get()) {
                if (blockState.is(Blocks.CAMPFIRE) || blockState.is(Blocks.SOUL_CAMPFIRE)) {
                    level.setBlockAndUpdate(pos, blockState.setValue(CampfireBlock.LIT, true));
                    return;
                } else if (blockState.is(AllBlocks.BLAZE_BURNER)) {
                    level.setBlockAndUpdate(pos, AllBlocks.LIT_BLAZE_BURNER.getDefaultState());
                    return;
                }
            }

            if (CDPSEConfig.airCurrentBlockInteraction().bulkBlastingSpreadFire.get()) {
                pos = pos.relative(Direction.getRandom(level.random));
                if (level.getBlockState(pos).isAir())
                    level.setBlockAndUpdate(pos, EventHooks.fireFluidPlaceBlockEvent(level, pos, pos, BaseFireBlock.getState(level, pos)));
            }
        }
    }

    @Restriction(require = @Condition(ModIntegration.Constants.SABLE))
    @Mixin(AllFanProcessingTypes.SmokingType.class)
    public static class SmokingTypeMixin implements FanProcessingTypeSimulatedExtension {
        @Override
        public boolean active() {
            return CDPSEConfig.airCurrentBlockInteraction().enableBulkSmokingBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            return blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_SMOKING) != null;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_SMOKING);
            if (result != null)
                level.setBlockAndUpdate(pos, result.defaultBlockState());
        }
    }

    @Restriction(require = @Condition(ModIntegration.Constants.SABLE))
    @Mixin(AllFanProcessingTypes.SplashingType.class)
    public static class SplashingTypeMixin implements FanProcessingTypeSimulatedExtension {
        @Override
        public boolean active() {
            return CDPSEConfig.airCurrentBlockInteraction().enableBulkSplashingBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_SPLASHING);
            if (result != null) return true;

            if (blockState.is(Blocks.CAMPFIRE) || blockState.is(Blocks.SOUL_CAMPFIRE)) {
                return blockState.getValue(CampfireBlock.LIT);
            } else return blockState.is(AllBlocks.LIT_BLAZE_BURNER);
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_SPLASHING);
            if (result != null)
                level.setBlockAndUpdate(pos, result.defaultBlockState());

            if (blockState.is(Blocks.CAMPFIRE) || blockState.is(Blocks.SOUL_CAMPFIRE)) {
                level.setBlockAndUpdate(pos, blockState.setValue(CampfireBlock.LIT, false));
            } else if (blockState.is(AllBlocks.LIT_BLAZE_BURNER)) {
                level.setBlockAndUpdate(pos, AllBlocks.BLAZE_BURNER.getDefaultState());
            }
        }
    }

    @Restriction(require = @Condition(ModIntegration.Constants.SABLE))
    @Mixin(AllFanProcessingTypes.HauntingType.class)
    public static class HauntingTypeMixin implements FanProcessingTypeSimulatedExtension {
        @Override
        public boolean active() {
            return CDPSEConfig.airCurrentBlockInteraction().enableBulkHauntingBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            return blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_HAUNTING) != null;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_HAUNTING);
            if (result != null)
                level.setBlockAndUpdate(pos, result.defaultBlockState());
        }
    }

    @Restriction(require = @Condition(ModIntegration.Constants.SABLE))
    @Mixin(FreezingFanProcessingType.class)
    public static class FreezingTypeMixin implements FanProcessingTypeSimulatedExtension {
        @Override
        public boolean active() {
            return CDPSEConfig.airCurrentBlockInteraction().enableBulkFreezingBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            return blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_FREEZING) != null;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_FREEZING);
            if (result != null)
                level.setBlockAndUpdate(pos, result.defaultBlockState());
        }
    }

    @Restriction(require = @Condition(ModIntegration.Constants.SABLE))
    @Mixin(EndingFanProcessingType.class)
    public static class EndingTypeMixin implements FanProcessingTypeSimulatedExtension {
        @Override
        public boolean active() {
            return CDPSEConfig.airCurrentBlockInteraction().enableBulkEndingBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            return blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_ENDING) != null;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_ENDING);
            if (result != null)
                level.setBlockAndUpdate(pos, result.defaultBlockState());
        }
    }

    @Restriction(require = @Condition(ModIntegration.Constants.SABLE))
    @Mixin(SandingFanProcessingType.class)
    public static class SandingTypeMixin implements FanProcessingTypeSimulatedExtension {
        @Override
        public boolean active() {
            return CDPSEConfig.airCurrentBlockInteraction().enableBulkSandingBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            return blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_SANDING) != null;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            var result = blockState.getBlockHolder().getData(CDPSEDataMaps.BLOCK_INTERACTION_SANDING);
            if (result != null)
                level.setBlockAndUpdate(pos, result.defaultBlockState());
        }
    }

    @Restriction(require = @Condition(ModIntegration.Constants.SABLE))
    @Mixin(ColoringFanProcessingType.class)
    public static abstract class ColoringTypeMixin implements FanProcessingTypeSimulatedExtension {
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
            return CDPSEConfig.airCurrentBlockInteraction().enableBulkColoringBlockInteraction.get();
        }

        @Override
        public boolean canAffectBlock(Level level, BlockPos pos, BlockState blockState) {
            if (!blockState.getBlock().asItem().equals(Items.AIR)) {
                if (transformingResultCache.containsKey(blockState.getBlock()))
                    return !transformingResultCache.get(blockState.getBlock()).equals(Blocks.AIR);
                else {
                    var result = process(new ItemStack(blockState.getBlock()), level);
                    if (result == null || result.size() != 1) {
                        transformingResultCache.put(blockState.getBlock(), Blocks.AIR);
                        return false;
                    } else {
                        transformingResultCache.put(blockState.getBlock(), Block.byItem(result.get(0).getItem()));
                        return !Block.byItem(result.get(0).getItem()).equals(Items.AIR);
                    }
                }
            }
            return false;
        }

        @Override
        public void affectBlock(Level level, BlockPos pos, BlockState blockState) {
            if (transformingResultCache.containsKey(blockState.getBlock())) {
                level.setBlockAndUpdate(pos, transformingResultCache.get(blockState.getBlock()).defaultBlockState());
            }
        }
    }
}
