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

package plus.dragons.createdragonsplus.mixin.minecraft;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;

@Mixin(ConcretePowderBlock.class)
public abstract class ConcretePowderBlockMixin extends FallingBlock {
    public ConcretePowderBlockMixin(Properties properties) {
        super(properties);
    }

    @Shadow
    @Final
    private BlockState concrete;

    @Inject(method = "updateShape", at = @At("HEAD"), cancellable = true)
    private void updateShape$handleDyeLiquidInteraction(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos, CallbackInfoReturnable<BlockState> cir) {
        var result = createDragonsPlus$getProperSolidified(level, pos, concrete);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }

    @Inject(method = "onLand", at = @At("HEAD"), cancellable = true)
    private void onLand$handleDyeLiquidInteraction(Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock, CallbackInfo ci) {
        var result = createDragonsPlus$getProperSolidified(level, pos, concrete);
        if (result != null) {
            level.setBlock(pos, result, 3);
            ci.cancel();
        }
    }

    @Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true)
    private void getStateForPlacement$handleDyeLiquidInteraction(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        var result = createDragonsPlus$getProperSolidified(context.getLevel(), context.getClickedPos(), concrete);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }

    @Unique
    @Nullable
    private static BlockState createDragonsPlus$getProperSolidified(LevelAccessor level, BlockPos pos, BlockState concrete) {
        BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();
        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN) continue;
            mutableBlockPos.setWithOffset(pos, direction);
            var fluid = level.getBlockState(mutableBlockPos).getFluidState();
            if (fluid.is(CDPFluids.COMMON_TAGS.dyes)) {
                for (var entry : CDPFluids.DYES_BY_VARIANT.entrySet()) {
                    var dye = entry.getValue();
                    if (fluid.getType() != dye.getSource() && fluid.getType() != dye.get())
                        continue;
                    return plus.dragons.createdragonsplus.common.fluids.dye.DyeVariantRegistry.get(entry.getKey())
                            .flatMap(variant -> BuiltInRegistries.BLOCK.getOptional(variant.concreteBlockId()))
                            .map(Block::defaultBlockState)
                            .orElse(concrete);
                }
                return concrete;
            }
        }
        return null;
    }

    @Override
    public boolean canBeHydrated(BlockState state, BlockGetter getter, BlockPos pos, FluidState fluid, BlockPos fluidPos) {
        if (fluid.is(CDPFluids.COMMON_TAGS.dyes)) return true;
        return fluid.canHydrate(getter, fluidPos, state, pos);
    }
}
