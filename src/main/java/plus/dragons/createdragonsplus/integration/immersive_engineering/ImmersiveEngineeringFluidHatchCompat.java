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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import plus.dragons.createdragonsplus.common.fluids.hatch.FluidHatchItemFilling;
import plus.dragons.createdragonsplus.integration.ModIntegration;

public class ImmersiveEngineeringFluidHatchCompat implements FluidHatchItemFilling.Handler {
    private static final ImmersiveEngineeringFluidHatchCompat INSTANCE = new ImmersiveEngineeringFluidHatchCompat();
    private static final int BOTTLE_FLUID_AMOUNT = 250;

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
                .filter(fluid -> fluidStack.getFluid() == fluid)
                .isPresent()
                && fluidStack.hasTag()
                && fluidStack.getTag().contains("Potion", Tag.TAG_STRING);
    }

    private static ItemStack fillGlassBottle(ItemStack stack, FluidStack fluidStack) {
        ItemStack result = new ItemStack(Items.POTION);
        result.setTag(fluidStack.getTag().copy());
        stack.shrink(1);
        return result;
    }
}
