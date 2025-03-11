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
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.Nullable;

public class ShapelessRecipeBuilder extends BaseShapelessRecipeBuilder<ShapelessRecipe, ShapelessRecipeBuilder> {
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    private RecipeCategory category = RecipeCategory.MISC;
    private String group = "";

    public ShapelessRecipeBuilder(@Nullable String directory) {
        super(directory);
    }

    public ShapelessRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        criteria.put(name, criterion);
        return this;
    }

    public ShapelessRecipeBuilder category(RecipeCategory category) {
        this.category = category;
        return this;
    }

    public ShapelessRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    @Override
    protected ShapelessRecipeBuilder builder() {
        return this;
    }

    @Override
    public RecipeHolder<ShapelessRecipe> build() {
        if (id == null) {
            id = result.getItemHolder().unwrapKey().orElseThrow().location();
        }
        var recipe = new ShapelessRecipe(this.group, RecipeBuilder.determineBookCategory(this.category), this.result, this.ingredients);
        return new RecipeHolder<>(this.id, recipe);
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
        if (!this.criteria.isEmpty()) {
            this.criteria.forEach(builder::addCriterion);
        }
        return builder.build(this.id.withPrefix("recipes/"));
    }
}
