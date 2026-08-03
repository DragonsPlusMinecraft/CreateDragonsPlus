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
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.Nullable;

public class ShapelessRecipeBuilder extends BaseShapelessRecipeBuilder<ShapelessRecipe, ShapelessRecipeBuilder> {
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();
    private RecipeCategory category = RecipeCategory.MISC;
    private String group = "";

    public ShapelessRecipeBuilder(@Nullable String directory) {
        super(directory);
    }

    public ShapelessRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
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
    public FinishedRecipe build() {
        if (id == null)
            id = BuiltInRegistries.ITEM.getKey(result.getItem());
        net.minecraft.data.recipes.ShapelessRecipeBuilder vanilla = net.minecraft.data.recipes.ShapelessRecipeBuilder
                .shapeless(category, result.getItem(), result.getCount());
        ingredients.forEach(vanilla::requires);
        criteria.forEach(vanilla::unlockedBy);
        vanilla.group(group);
        AtomicReference<FinishedRecipe> built = new AtomicReference<>();
        vanilla.save(built::set, outputId());
        return built.get();
    }
}
