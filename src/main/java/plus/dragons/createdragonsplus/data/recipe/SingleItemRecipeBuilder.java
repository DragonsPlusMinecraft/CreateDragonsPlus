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

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import org.jetbrains.annotations.Nullable;

public class SingleItemRecipeBuilder extends BaseSingleItemRecipeBuilder<SingleItemRecipe, SingleItemRecipeBuilder> {
    private final Factory factory;
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();
    private String group = "";

    public SingleItemRecipeBuilder(@Nullable String directory, Factory factory) {
        super(directory);
        this.factory = factory;
    }

    public SingleItemRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        criteria.put(name, criterion);
        return this;
    }

    public SingleItemRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    @Override
    protected SingleItemRecipeBuilder builder() {
        return this;
    }

    @Override
    public FinishedRecipe build() {
        if (id == null)
            id = BuiltInRegistries.ITEM.getKey(result.getItem());
        SingleItemRecipe recipe = factory.create(outputId(), group, ingredient, result);
        Advancement.Builder advancement = Advancement.Builder.recipeAdvancement()
                .parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT)
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(outputId()))
                .rewards(AdvancementRewards.Builder.recipe(outputId()))
                .requirements(RequirementsStrategy.OR);
        criteria.forEach(advancement::addCriterion);
        return new net.minecraft.data.recipes.SingleItemRecipeBuilder.Result(outputId(), recipe.getSerializer(), group,
                ingredient, result.getItem(), result.getCount(), advancement,
                new net.minecraft.resources.ResourceLocation(outputId().getNamespace(), "recipes/" + outputId().getPath()));
    }

    @FunctionalInterface
    public interface Factory {
        SingleItemRecipe create(ResourceLocation id, String group, Ingredient ingredient, ItemStack result);
    }
}
