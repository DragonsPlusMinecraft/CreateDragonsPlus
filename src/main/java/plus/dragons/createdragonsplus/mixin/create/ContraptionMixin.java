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

package plus.dragons.createdragonsplus.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import plus.dragons.createdragonsplus.common.fluids.WaterAndLavaLoggedBlock;

import static plus.dragons.createdragonsplus.common.fluids.WaterAndLavaLoggedBlock.FLUID;

@Mixin(Contraption.class)
public abstract class ContraptionMixin {
    @Inject(method = "removeBlocksFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", shift = At.Shift.AFTER))
    private void createintegratedfarming$fixRemoveBlockLeaveNoFluid(Level world, BlockPos offset, CallbackInfo ci, @Local Block blockIn, @Local BlockState oldState, @Local(ordinal = 1) BlockPos add) {
        if (blockIn instanceof WaterAndLavaLoggedBlock && oldState.hasProperty(FLUID)
                && oldState.getValue(FLUID) != WaterAndLavaLoggedBlock.ContainedFluid.EMPTY) {
            var f = oldState.getValue(FLUID);
            int flags = Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_KNOWN_SHAPE
                    | Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE;
            if (f == WaterAndLavaLoggedBlock.ContainedFluid.WATER)
                world.setBlock(add, Blocks.WATER.defaultBlockState(), flags);
            else world.setBlock(add, Blocks.LAVA.defaultBlockState(), flags);
        }
    }

    @ModifyArg(method = "addBlocksToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), index = 1)
    private BlockState createintegratedfarming$fixAddBlocksToWorldIgnoreFluid(BlockState state, @Local(ordinal = 0) BlockPos targetPos, @Local(ordinal = 0, argsOnly = true) Level world) {
        var result = state;
        if (state.getBlock() instanceof WaterAndLavaLoggedBlock
                && state.hasProperty(FLUID)) {
            FluidState fluidState = world.getFluidState(targetPos);
            result = state.setValue(FLUID, fluidState.getType() == Fluids.WATER ? WaterAndLavaLoggedBlock.ContainedFluid.WATER : fluidState.getType() == Fluids.LAVA ? WaterAndLavaLoggedBlock.ContainedFluid.LAVA : WaterAndLavaLoggedBlock.ContainedFluid.EMPTY);
        }
        return result;
    }
}
