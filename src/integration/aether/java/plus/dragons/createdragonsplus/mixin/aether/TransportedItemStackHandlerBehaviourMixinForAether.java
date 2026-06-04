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

package plus.dragons.createdragonsplus.mixin.aether;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.ProcessingCallback;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import java.util.function.Function;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.integration.aether.common.kinetics.fan.enchanting.AetherIncubationContext;

@Restriction(require = @Condition(ModIntegration.Constants.AETHER))
@Mixin(TransportedItemStackHandlerBehaviour.class)
public class TransportedItemStackHandlerBehaviourMixinForAether {
    @WrapOperation(method = "handleCenteredProcessingOnAllItems", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour$ProcessingCallback;applyToAllItems(FLjava/util/function/Function;)V"))
    private void handleCenteredProcessingOnAllItems$captureTransportedItemPosition(ProcessingCallback callback, float maxDistanceFromCenter, Function<TransportedItemStack, TransportedResult> processFunction, Operation<Void> original) {
        var handler = (TransportedItemStackHandlerBehaviour) (Object) this;
        original.call(callback, maxDistanceFromCenter, (Function<TransportedItemStack, TransportedResult>) transported -> {
            AetherIncubationContext.setTransportedItemPosition(handler.getWorldPositionOf(transported));
            try {
                return processFunction.apply(transported);
            } finally {
                AetherIncubationContext.clearTransportedItemPosition();
            }
        });
    }
}
