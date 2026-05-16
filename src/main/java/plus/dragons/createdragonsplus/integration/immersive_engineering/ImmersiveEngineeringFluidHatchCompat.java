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

package plus.dragons.createdragonsplus.integration.immersive_engineering;

import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.fluids.FluidStack;
import plus.dragons.createdragonsplus.common.fluids.hatch.FluidHatchItemFilling;
import plus.dragons.createdragonsplus.integration.ModIntegration;

public class ImmersiveEngineeringFluidHatchCompat implements FluidHatchItemFilling.Handler {
    private static final ImmersiveEngineeringFluidHatchCompat INSTANCE = new ImmersiveEngineeringFluidHatchCompat();
    private static final int BOTTLE_FLUID_AMOUNT = 250;
    private static final ResourceLocation POTION_BOTTLE_TYPE = ModIntegration.IMMERSIVE_ENGINEERING.asResource("potion_bottle_type");

    public static void register() {
        FluidHatchItemFilling.register(INSTANCE);
    }

    @Override
    public OptionalInt getRequiredAmountForItem(ItemStack stack, FluidStack availableFluid) {
        if (canFillGlassBottle(stack, availableFluid))
            return OptionalInt.of(BOTTLE_FLUID_AMOUNT);
        return OptionalInt.empty();
    }

    @Override
    public Optional<ItemStack> fillItem(int requiredAmount, ItemStack stack, FluidStack availableFluid) {
        if (requiredAmount == BOTTLE_FLUID_AMOUNT && canFillGlassBottle(stack, availableFluid))
            return Optional.of(fillGlassBottle(stack, availableFluid));
        return Optional.empty();
    }

    private static boolean canFillGlassBottle(ItemStack stack, FluidStack availableFluid) {
        return stack.is(Items.GLASS_BOTTLE) && isPotionFluid(availableFluid);
    }

    private static boolean isPotionFluid(FluidStack fluidStack) {
        return BuiltInRegistries.FLUID.getOptional(ModIntegration.IMMERSIVE_ENGINEERING.asResource("potion"))
                .filter(fluidStack::is)
                .isPresent()
                && fluidStack.has(DataComponents.POTION_CONTENTS);
    }

    private static ItemStack fillGlassBottle(ItemStack stack, FluidStack fluidStack) {
        ItemStack result = new ItemStack(getPotionBottleItem(fluidStack));
        result.set(DataComponents.POTION_CONTENTS, fluidStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY));
        stack.shrink(1);
        return result;
    }

    private static Item getPotionBottleItem(FluidStack fluidStack) {
        var bottleType = BuiltInRegistries.DATA_COMPONENT_TYPE
                .getOptional(ResourceKey.create(Registries.DATA_COMPONENT_TYPE, POTION_BOTTLE_TYPE))
                .map(fluidStack::get)
                .map(ImmersiveEngineeringFluidHatchCompat::getBottleTypeName)
                .orElse("regular");
        return switch (bottleType) {
            case "splash" -> Items.SPLASH_POTION;
            case "lingering" -> Items.LINGERING_POTION;
            default -> Items.POTION;
        };
    }

    private static String getBottleTypeName(Object bottleType) {
        if (bottleType instanceof StringRepresentable representable)
            return representable.getSerializedName();
        return bottleType.toString();
    }
}
