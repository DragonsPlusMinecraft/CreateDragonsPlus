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

package plus.dragons.createdragonsplus.common.kinetics.fan.freezing;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;

public class FreezingRecipe extends StandardProcessingRecipe<SingleRecipeInput> {
    public FreezingRecipe(ProcessingRecipeParams params) {
        super(CDPRecipes.FREEZING, params);
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

    public static StandardProcessingRecipe.Builder<FreezingRecipe> builder(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(FreezingRecipe::new, id);
    }
}
