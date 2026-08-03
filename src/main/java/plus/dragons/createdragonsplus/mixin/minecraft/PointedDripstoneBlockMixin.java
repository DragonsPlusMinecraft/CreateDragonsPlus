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

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.config.CDPConfig;

@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlockMixin {
    private static final float DRAGON_BREATH_CAULDRON_FILL_CHANCE = 0.05859375F;

    @Shadow
    private static boolean isStalactiteStartPos(BlockState state, LevelReader level, BlockPos pos) {
        throw new AssertionError();
    }

    @Shadow
    private static @Nullable BlockPos findTip(BlockState state, net.minecraft.world.level.LevelAccessor level, BlockPos pos, int maxIterations, boolean isTipMerge) {
        throw new AssertionError();
    }

    @Shadow
    private static @Nullable BlockPos findFillableCauldronBelowStalactiteTip(Level level, BlockPos pos, Fluid fluid) {
        throw new AssertionError();
    }

    @Inject(method = "maybeTransferFluid", at = @At("HEAD"), cancellable = true)
    private static void createDragonsPlus$transferDragonBreath(BlockState state, ServerLevel level, BlockPos pos, float randomChance, CallbackInfo ci) {
        if (!CDPConfig.features().dragonBreathFluid.get()
                || !CDPConfig.features().dragonBreathFluidDripstoneDuplication.get()
                || !isStalactiteStartPos(state, level, pos))
            return;

        Fluid dragonBreath = CDPFluids.DRAGON_BREATH.getSource();
        if (!level.getFluidState(pos.above(2)).getType().isSame(dragonBreath))
            return;

        ci.cancel();
        if (randomChance >= DRAGON_BREATH_CAULDRON_FILL_CHANCE)
            return;

        BlockPos tip = findTip(state, level, pos, 11, false);
        if (tip == null)
            return;

        BlockPos cauldronPos = findFillableCauldronBelowStalactiteTip(level, tip, dragonBreath);
        if (cauldronPos == null)
            return;

        level.levelEvent(1504, tip, 0);
        int delay = 50 + tip.getY() - cauldronPos.getY();
        BlockState cauldronState = level.getBlockState(cauldronPos);
        level.scheduleTick(cauldronPos, cauldronState.getBlock(), delay);
    }

    @Inject(method = "canFillCauldron", at = @At("HEAD"), cancellable = true)
    private static void createDragonsPlus$allowDragonBreathCauldron(Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (CDPConfig.features().dragonBreathFluid.get()
                && CDPConfig.features().dragonBreathFluidDripstoneDuplication.get()
                && fluid.isSame(CDPFluids.DRAGON_BREATH.getSource()))
            cir.setReturnValue(true);
    }
}
