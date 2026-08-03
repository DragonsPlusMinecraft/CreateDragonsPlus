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

import com.simibubi.create.content.fluids.pipes.VanillaFluidTargets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonsplus.common.fluids.dragonBreath.DragonBreathCauldronBlock;
import plus.dragons.createdragonsplus.common.registry.CDPCauldrons;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;

@Mixin(value = VanillaFluidTargets.class, remap = false)
public class VanillaFluidTargetsMixin {
    @Inject(method = "canProvideFluidWithoutCapability", at = @At("HEAD"), cancellable = true)
    private static void canProvideFluidWithoutCapability$dragonBreathCauldron(BlockState state,
            CallbackInfoReturnable<Boolean> cir) {
        if (state.is(CDPCauldrons.DRAGON_BREATH_CAULDRON.get()))
            cir.setReturnValue(true);
    }

    @Inject(method = "drainBlock", at = @At("HEAD"), cancellable = true)
    private static void drainBlock$dragonBreathCauldron(Level level, BlockPos pos, BlockState state,
            boolean simulate, CallbackInfoReturnable<FluidStack> cir) {
        if (!state.is(CDPCauldrons.DRAGON_BREATH_CAULDRON.get()))
            return;
        if (state.getValue(DragonBreathCauldronBlock.LEVEL) != DragonBreathCauldronBlock.MAX_LEVEL) {
            cir.setReturnValue(FluidStack.EMPTY);
            return;
        }
        if (!simulate)
            level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
        cir.setReturnValue(new FluidStack((Fluid) CDPFluids.DRAGON_BREATH.getSource(), FluidType.BUCKET_VOLUME));
    }
}
