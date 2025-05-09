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

import com.simibubi.create.content.fluids.OpenEndedPipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OpenEndedPipe.class)
public class OpenEndedPipeMixin {
    @Shadow
    private Level world;

    @Shadow
    private BlockPos outputPos;

    @Inject(method = "provideFluidToSpace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;ultraWarm()Z"), cancellable = true)
    private void provideFluidToSpace$checkVaporize(FluidStack fluid, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        var type = fluid.getFluidType();
        if (world.dimensionType().ultraWarm() && type.isVaporizedOnPlacement(world, outputPos, fluid)) {
            type.onVaporize(null, world, outputPos, fluid);
            cir.setReturnValue(true);
        }
    }
}
