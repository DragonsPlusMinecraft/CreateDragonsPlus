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

package plus.dragons.createdragonsplus.integration.aether.integration.jei.category;

import com.aetherteam.aether.recipe.recipes.item.AbstractAetherCookingRecipe;
import com.aetherteam.aether.recipe.recipes.item.AltarRepairRecipe;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import plus.dragons.createdragonsplus.integration.aether.common.kinetics.fan.enchanting.AetherCookingRecipeResults;

public class AetherEnchantingDisplayRecipe implements net.minecraft.world.item.crafting.Recipe<SingleRecipeInput> {
    private final AbstractAetherCookingRecipe recipe;

    public AetherEnchantingDisplayRecipe(AbstractAetherCookingRecipe recipe) {
        this.recipe = recipe;
    }

    public AbstractAetherCookingRecipe recipe() {
        return recipe;
    }

    public boolean isRepairing() {
        return recipe instanceof AltarRepairRecipe;
    }

    public ItemStack getDisplayInput() {
        var ingredients = recipe.getIngredients();
        if (ingredients.isEmpty())
            return ItemStack.EMPTY;
        var stacks = ingredients.get(0).getItems();
        if (stacks.length == 0)
            return ItemStack.EMPTY;
        var input = stacks[0].copy();
        if (isRepairing() && input.isDamageableItem())
            input.setDamageValue(input.getMaxDamage() * 3 / 4);
        return input;
    }

    public List<ItemStack> getDisplayOutputs() {
        return AetherCookingRecipeResults.getDisplayOutputs(recipe, getDisplayInput());
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return recipe.matches(input, level);
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return recipe.assemble(input, registries);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return recipe.canCraftInDimensions(width, height);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return recipe.getResultItem(registries);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipe.getIngredients();
    }

    @Override
    public ItemStack getToastSymbol() {
        return recipe.getToastSymbol();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return recipe.getSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return recipe.getType();
    }
}
