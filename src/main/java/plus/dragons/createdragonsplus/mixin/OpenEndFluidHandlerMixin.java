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

package plus.dragons.createdragonsplus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import com.simibubi.create.content.fluids.OpenEndedPipe;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonsplus.common.fluid.ConsumingOpenPipeEffectHandler;

@Mixin(targets = "com.simibubi.create.content.fluids.OpenEndedPipe.OpenEndFluidHandler", remap = false)
public abstract class OpenEndFluidHandlerMixin extends FluidTank {
    @Shadow
    @Final
    OpenEndedPipe this$0;

    private OpenEndFluidHandlerMixin(int capacity) {
        super(capacity);
    }

    @WrapOperation(
            method = "fill",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lcom/simibubi/create/foundation/fluid/FluidHelper;copyStackWithAmount(Lnet/neoforged/neoforge/fluids/FluidStack;I)Lnet/neoforged/neoforge/fluids/FluidStack;"))
    private FluidStack create_dragons_plus$fill$copyStack(
            FluidStack resource, int amount, Operation<FluidStack> original, @Local OpenPipeEffectHandler handler) {
        if (handler instanceof ConsumingOpenPipeEffectHandler) return resource.copy();
        return original.call(resource, amount);
    }

    @Inject(
            method = "fill",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lcom/simibubi/create/content/fluids/OpenEndedPipe$OpenEndFluidHandler;getFluidAmount()I"))
    private void create_dragons_plus$fill$applyConsumingEffect(
            FluidStack resource,
            FluidAction action,
            CallbackInfoReturnable<Integer> cir,
            @Local OpenPipeEffectHandler handler) {
        if (handler instanceof ConsumingOpenPipeEffectHandler) {
            FluidStack remainder = ConsumingOpenPipeEffectHandler.getRemainder(
                    (ConsumingOpenPipeEffectHandler) handler, this$0, this.getFluid());
            this.setFluid(remainder);
        }
    }
}
