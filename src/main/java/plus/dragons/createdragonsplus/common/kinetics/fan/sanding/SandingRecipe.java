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

package plus.dragons.createdragonsplus.common.kinetics.fan.sanding;

import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import plus.dragons.createdragonsplus.common.recipe.CustomProcessingRecipe;
import plus.dragons.createdragonsplus.common.recipe.CustomProcessingRecipeBuilder;
import plus.dragons.createdragonsplus.common.recipe.CustomProcessingRecipeParams;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;

public class SandingRecipe extends CustomProcessingRecipe<SingleRecipeInput, CustomProcessingRecipeParams> {
    public SandingRecipe(CustomProcessingRecipeParams params) {
        super(CDPRecipes.FREEZING, params);
    }

    public static RecipeHolder<SandingRecipe> convertSandPaperPolishing(RecipeHolder<SandPaperPolishingRecipe> original) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                original.id().getNamespace(),
                original.id().getPath() + "_as_sanding");
        SandingRecipe recipe = new Builder(id)
                .require(original.value().getIngredients().getFirst())
                .output(original.value().getRollableResults().getFirst())
                .build();
        return new RecipeHolder<>(id, recipe);
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 12;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return getIngredients().getFirst().test(input.item());
    }

    public static Builder builder(ResourceLocation id) {
        return new Builder(id);
    }

    public static class Builder extends CustomProcessingRecipeBuilder<CustomProcessingRecipeParams, SandingRecipe> {
        protected Builder(ResourceLocation id) {
            super(SandingRecipe::new, id);
        }

        @Override
        protected CustomProcessingRecipeParams createParams(ResourceLocation id) {
            return new CustomProcessingRecipeParams(id);
        }
    }
}
