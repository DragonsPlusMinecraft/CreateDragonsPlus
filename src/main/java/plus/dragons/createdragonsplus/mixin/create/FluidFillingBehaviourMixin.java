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

import com.simibubi.create.content.fluids.transfer.FluidFillingBehaviour;
import com.simibubi.create.content.fluids.transfer.FluidManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidFillingBehaviour.class)
public abstract class FluidFillingBehaviourMixin extends FluidManipulationBehaviour {
    private FluidFillingBehaviourMixin(SmartBlockEntity be) {
        super(be);
    }

    @ModifyVariable(method = "tryDeposit", at = @At(value = "STORE", ordinal = 0), name = "evaporate")
    private boolean tryDeposite$isVaporizedOnPlacement(boolean vaporize, Fluid fluid, BlockPos root, boolean simulate) {
        var fluidStack = new FluidStack(fluid, 1000);
        return fluid.getFluidType().isVaporizedOnPlacement(getWorld(), getPos(), fluidStack);
    }

    @Inject(method = "tryDeposit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"), cancellable = true)
    private void tryDeposit$onVaporize(Fluid fluid, BlockPos root, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        var fluidStack = new FluidStack(fluid, 1000);
        fluid.getFluidType().onVaporize(null, getWorld(), root, fluidStack);
        cir.setReturnValue(true);
    }
}
