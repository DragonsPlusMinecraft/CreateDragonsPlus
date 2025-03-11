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
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class CookingRecipeBuilder<R extends AbstractCookingRecipe> extends
                                                                   BaseSingleItemRecipeBuilder<R, CookingRecipeBuilder<R>> {
    private final AbstractCookingRecipe.Factory<R> factory;
    private float experience;
    private int cookingTime;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    private CookingBookCategory category = CookingBookCategory.MISC;
    private String group = "";

    protected CookingRecipeBuilder(@Nullable String directory, AbstractCookingRecipe.Factory<R> factory) {
        super(directory);
        this.factory = factory;
    }

    public CookingRecipeBuilder(@Nullable String directory, AbstractCookingRecipe.Factory<R> factory, int cookingTime) {
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

    public CookingRecipeBuilder<R> unlockedBy(String name, Criterion<?> criterion) {
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
    public RecipeHolder<R> build() {
        if (id == null) {
            id = result.getItemHolder().unwrapKey().orElseThrow().location();
        }
        var recipe = factory.create(group, category, ingredient, result, experience, cookingTime);
        return new RecipeHolder<>(id, recipe);
    }

    @Override
    public @Nullable AdvancementHolder buildAdvancement() {
        if (id == null) {
            id = result.getItemHolder().unwrapKey().orElseThrow().location();
        }
        var builder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        return builder.build(id);
    }
}
