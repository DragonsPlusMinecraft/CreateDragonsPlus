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

import com.google.gson.JsonObject;
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
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public class CookingRecipeBuilder<R extends AbstractCookingRecipe> extends BaseSingleItemRecipeBuilder<R, CookingRecipeBuilder<R>> {
    private final Factory<R> factory;
    private float experience;
    private int cookingTime;
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();
    private CookingBookCategory category = CookingBookCategory.MISC;
    private String group = "";

    protected CookingRecipeBuilder(@Nullable String directory, Factory<R> factory) {
        super(directory);
        this.factory = factory;
    }

    public CookingRecipeBuilder(@Nullable String directory, Factory<R> factory, int cookingTime) {
        super(directory);
        this.factory = factory;
        this.cookingTime = cookingTime;
    }

    public CookingRecipeBuilder<R> experience(float experience) {
        this.experience = experience;
        return this;
    }

    public CookingRecipeBuilder<R> cookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
        return this;
    }

    public CookingRecipeBuilder<R> unlockedBy(String name, CriterionTriggerInstance criterion) {
        criteria.put(name, criterion);
        return this;
    }

    public CookingRecipeBuilder<R> category(CookingBookCategory category) {
        this.category = category;
        return this;
    }

    public CookingRecipeBuilder<R> group(String group) {
        this.group = group;
        return this;
    }

    @Override
    protected CookingRecipeBuilder<R> builder() {
        return this;
    }

    @Override
    public FinishedRecipe build() {
        if (id == null)
            id = BuiltInRegistries.ITEM.getKey(result.getItem());
        R recipe = factory.create(outputId(), group, category, ingredient, result, experience, cookingTime);
        Advancement.Builder advancement = Advancement.Builder.recipeAdvancement()
                .parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT)
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(outputId()))
                .rewards(AdvancementRewards.Builder.recipe(outputId()))
                .requirements(RequirementsStrategy.OR);
        criteria.forEach(advancement::addCriterion);
        ResourceLocation advancementId = new ResourceLocation(outputId().getNamespace(), "recipes/" + outputId().getPath());
        return new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject json) {
                if (!group.isEmpty())
                    json.addProperty("group", group);
                json.addProperty("category", category.getSerializedName());
                json.add("ingredient", ingredient.toJson());
                json.addProperty("result", BuiltInRegistries.ITEM.getKey(result.getItem()).toString());
                json.addProperty("experience", experience);
                json.addProperty("cookingtime", cookingTime);
            }

            @Override
            public ResourceLocation getId() {
                return outputId();
            }

            @Override
            public RecipeSerializer<?> getType() {
                return recipe.getSerializer();
            }

            @Override
            public JsonObject serializeAdvancement() {
                return advancement.serializeToJson();
            }

            @Override
            public ResourceLocation getAdvancementId() {
                return advancementId;
            }
        };
    }

    @FunctionalInterface
    public interface Factory<R extends AbstractCookingRecipe> {
        R create(ResourceLocation id, String group, CookingBookCategory category, Ingredient ingredient, ItemStack result,
                float experience, int cookingTime);
    }
}
