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

package plus.dragons.createdragonsplus.data.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.recipe.BaseRecipeBuilder;
import plus.dragons.createdragonsplus.data.recipe.integration.IntegrationResultRecipe;

public abstract class BaseShapelessRecipeBuilder<R extends Recipe<?>, B extends BaseShapelessRecipeBuilder<R, B>> extends BaseRecipeBuilder<R, B> {
    protected final NonNullList<Ingredient> ingredients = NonNullList.create();
    protected ItemStack result = new ItemStack(Items.BARRIER);

    protected BaseShapelessRecipeBuilder(@Nullable String directory) {
        super(directory);
    }

    public B require(TagKey<Item> tag) {
        return require(Ingredient.of(tag));
    }

    public B require(ItemLike item) {
        return require(Ingredient.of(item));
    }

    public B require(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return builder();
    }

    public B output(ItemLike item) {
        this.result = new ItemStack(item);
        return builder();
    }

    public B output(ItemLike item, int count) {
        this.result = new ItemStack(item, count);
        return builder();
    }

    public B output(ItemStack stack) {
        this.result = stack;
        return builder();
    }

    public IntegrationResultRecipe.Builder output(ResourceLocation result) {
        return new IntegrationResultRecipe.Builder(this, this.result, result);
    }
}
