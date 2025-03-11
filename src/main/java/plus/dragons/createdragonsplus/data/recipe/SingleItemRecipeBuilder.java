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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import org.jetbrains.annotations.Nullable;

public class SingleItemRecipeBuilder extends BaseSingleItemRecipeBuilder<SingleItemRecipe, SingleItemRecipeBuilder> {
    private final SingleItemRecipe.Factory<?> factory;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    private String group = "";

    public SingleItemRecipeBuilder(@Nullable String directory, SingleItemRecipe.Factory<?> factory) {
        super(directory);
        this.factory = factory;
    }

    public SingleItemRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
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
    public RecipeHolder<SingleItemRecipe> build() {
        if (id == null) {
            id = result.getItemHolder().unwrapKey().orElseThrow().location();
        }
        var recipe = this.factory.create(group, ingredient, result);
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
        if (!this.criteria.isEmpty()) {
            this.criteria.forEach(builder::addCriterion);
        }
        return builder.build(this.id.withPrefix("recipes/"));
    }
}
