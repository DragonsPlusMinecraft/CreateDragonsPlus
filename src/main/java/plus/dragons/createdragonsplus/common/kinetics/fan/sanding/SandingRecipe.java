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
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;

public class SandingRecipe extends ProcessingRecipe<RecipeWrapper> {
    public SandingRecipe(ProcessingRecipeParams params) {
        super(CDPRecipes.SANDING, params);
    }

    public static SandingRecipe convertSandPaperPolishing(SandPaperPolishingRecipe original) {
        ResourceLocation id = new ResourceLocation(
                original.getId().getNamespace(),
                original.getId().getPath() + "_as_sanding");
        return builder(id)
                .require(original.getIngredients().get(0))
                .output(original.getRollableResults().get(0))
                .build();
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
    public boolean matches(RecipeWrapper input, Level level) {
        return !input.isEmpty() && getIngredients().get(0).test(input.getItem(0));
    }

    public static ProcessingRecipeBuilder<SandingRecipe> builder(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(SandingRecipe::new, id);
    }
}
