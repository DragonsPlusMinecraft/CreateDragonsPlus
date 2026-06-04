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

import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class AetherIncubationDisplayRecipe implements net.minecraft.world.item.crafting.Recipe<SingleRecipeInput> {
    private final IncubationRecipe recipe;

    public AetherIncubationDisplayRecipe(IncubationRecipe recipe) {
        this.recipe = recipe;
    }

    public ItemStack getDisplayOutput() {
        var stack = AetherItems.MOA_SPAWN_EGG.get().getDefaultInstance();
        stack.set(DataComponents.CUSTOM_NAME, recipe.getEntity().getDescription());
        return stack;
    }

    public Component getMoaType() {
        var tag = recipe.getTag().orElse(null);
        if (tag != null && tag.contains("MoaType"))
            return Component.literal(tag.getString("MoaType"));
        return recipe.getEntity().getDescription();
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return recipe.matches(input, level);
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return recipe.canCraftInDimensions(width, height);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return getDisplayOutput();
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
