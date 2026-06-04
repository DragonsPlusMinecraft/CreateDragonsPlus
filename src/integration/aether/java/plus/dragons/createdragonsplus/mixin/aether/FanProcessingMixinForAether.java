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
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessing;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import java.util.List;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.integration.aether.common.kinetics.fan.enchanting.AetherEnchantingFanProcessingType;
import plus.dragons.createdragonsplus.integration.aether.common.kinetics.fan.enchanting.AetherIncubationContext;
import plus.dragons.createdragonsplus.integration.aether.common.kinetics.fan.enchanting.AetherIncubationProcessing;

@Restriction(require = @Condition(ModIntegration.Constants.AETHER))
@Mixin(FanProcessing.class)
public class FanProcessingMixinForAether {
    @WrapOperation(method = "applyProcessing(Lnet/minecraft/world/entity/item/ItemEntity;Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;)Z", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;process(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)Ljava/util/List;"))
    private static List<ItemStack> applyProcessing$incubateItemEntity(FanProcessingType type, ItemStack stack, Level level, Operation<List<ItemStack>> original,
            @Local(argsOnly = true) ItemEntity entity) {
        if (type instanceof AetherEnchantingFanProcessingType && AetherIncubationProcessing.tryIncubate(stack, level, entity.position()))
            return List.of();
        return original.call(type, stack, level);
    }

    @WrapOperation(method = "applyProcessing(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lnet/minecraft/world/level/Level;Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;)Lcom/simibubi/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour$TransportedResult;", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;process(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)Ljava/util/List;"))
    private static List<ItemStack> applyProcessing$incubateTransportedItem(FanProcessingType type, ItemStack stack, Level level, Operation<List<ItemStack>> original,
            @Local(argsOnly = true) TransportedItemStack transported) {
        var position = AetherIncubationContext.transportedItemPosition();
        if (position != null && type instanceof AetherEnchantingFanProcessingType && AetherIncubationProcessing.tryIncubate(stack, level, position))
            return List.of();
        return original.call(type, stack, level);
    }
}
