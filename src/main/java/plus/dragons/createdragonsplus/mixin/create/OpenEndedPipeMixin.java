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
import com.simibubi.create.content.fluids.OpenEndedPipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;

@Mixin(value = OpenEndedPipe.class, remap = false)
public class OpenEndedPipeMixin {
    @Shadow
    private Level world;

    @Shadow
    private BlockPos outputPos;

    @Inject(method = "provideFluidToSpace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;ultraWarm()Z", remap = true), cancellable = true)
    private void provideFluidToSpace$checkVaporize(FluidStack fluid, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        var type = fluid.getFluid().getFluidType();
        if (world.dimensionType().ultraWarm() && type.isVaporizedOnPlacement(world, outputPos, fluid)) {
            type.onVaporize(null, world, outputPos, fluid);
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "provideFluidToSpace", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/fluids/FluidReactions;handlePipeSpillCollision(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/world/level/material/FluidState;)V"), cancellable = true)
    private void provideFluidToSpace$handleDyeLavaCollision(FluidStack fluid, boolean simulate, CallbackInfoReturnable<Boolean> cir, @Local FluidState fluidState) {
        BlockState result = null;
        var pipeType = fluid.getFluid().getFluidType();
        var worldType = fluidState.getFluidType();
        if (pipeType == ForgeMod.LAVA_TYPE.get()) {
            result = CDPFluids.Reactions.getDyeLavaInteraction(worldType);
        } else if (worldType == ForgeMod.LAVA_TYPE.get()) {
            result = CDPFluids.Reactions.getDyeLavaInteraction(pipeType);
        }
        if (result == null)
            return;
        if (!simulate) {
            var placed = ForgeEventFactory.fireFluidPlaceBlockEvent(world, outputPos, outputPos, result);
            world.setBlockAndUpdate(outputPos, placed);
            world.levelEvent(1501, outputPos, 0);
        }
        cir.setReturnValue(true);
    }
}
