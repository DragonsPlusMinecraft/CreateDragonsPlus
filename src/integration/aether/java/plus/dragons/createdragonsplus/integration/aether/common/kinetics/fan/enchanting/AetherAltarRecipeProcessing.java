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

package plus.dragons.createdragonsplus.integration.aether.common.kinetics.fan.enchanting;

import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.recipes.item.AbstractAetherCookingRecipe;
import com.aetherteam.aether.recipe.recipes.item.AltarRepairRecipe;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class AetherAltarRecipeProcessing {
    public static boolean canProcess(ItemStack stack, Level level) {
        return findRecipe(stack, level)
                .map(holder -> canProcess(holder.value(), stack))
                .orElse(false);
    }

    public static Optional<List<ItemStack>> process(ItemStack stack, Level level) {
        return findRecipe(stack, level)
                .filter(holder -> canProcess(holder.value(), stack))
                .map(holder -> AetherCookingRecipeResults.apply(holder.value(), stack, level));
    }

    @SuppressWarnings("unchecked")
    public static Optional<RecipeHolder<AbstractAetherCookingRecipe>> findRecipe(ItemStack stack, Level level) {
        var type = (RecipeType<AbstractAetherCookingRecipe>) AetherRecipeTypes.ENCHANTING.get();
        return level.getRecipeManager().getRecipeFor(type, new SingleRecipeInput(stack), level);
    }

    private static boolean canProcess(AbstractAetherCookingRecipe recipe, ItemStack stack) {
        if (recipe instanceof AltarRepairRecipe)
            return stack.isDamageableItem() && stack.isDamaged();
        return true;
    }
}
