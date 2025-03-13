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

package plus.dragons.createdragonsplus.common.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import java.util.function.Function;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class CustomProcessingRecipeBuilder<P extends CustomProcessingRecipeParams, R extends CustomProcessingRecipe<?, P>> extends ProcessingRecipeBuilder<R> {
    protected final Function<P, R> factory;

    public CustomProcessingRecipeBuilder(Function<P, R> factory, P params) {
        super(null, params.getId());
        this.factory = factory;
        this.params = params;
    }

    @SuppressWarnings("unchecked")
    protected final P getParams() {
        return (P) params;
    }

    protected @Nullable String getDirectory(R recipe) {
        return recipe.getTypeInfo().getId().getPath();
    }

    protected @Nullable AdvancementHolder buildAdvancement() {
        return null;
    }

    @Override
    public R build() {
        return factory.apply(getParams());
    }

    @Override
    public void build(RecipeOutput consumer) {
        R recipe = build();
        String directory = getDirectory(recipe);
        ResourceLocation id = directory == null ? recipe.id : recipe.id.withPrefix(directory + "/");
        consumer.accept(id, recipe, buildAdvancement(), recipeConditions.toArray(new ICondition[0]));
    }
}
