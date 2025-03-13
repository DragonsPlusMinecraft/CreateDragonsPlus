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

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeFactory;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import plus.dragons.createdragonsplus.mixin.accessor.SequencedAssemblyRecipeBuilderAccessor;

public class CustomSequencedAssemblyRecipeBuilder extends SequencedAssemblyRecipeBuilder {
    public CustomSequencedAssemblyRecipeBuilder(ResourceLocation id) {
        super(id);
    }

    @Override
    public CustomSequencedAssemblyRecipeBuilder transitionTo(ItemLike item) {
        return (CustomSequencedAssemblyRecipeBuilder) super.transitionTo(item);
    }

    @Override
    public <T extends ProcessingRecipe<?>> CustomSequencedAssemblyRecipeBuilder addStep(ProcessingRecipeFactory<T> factory, UnaryOperator<ProcessingRecipeBuilder<T>> builder) {
        return (CustomSequencedAssemblyRecipeBuilder) super.addStep(factory, builder);
    }

    public CustomSequencedAssemblyRecipeBuilder addStep(BiFunction<Item, ResourceLocation, ProcessingRecipeBuilder<?>> builder) {
        var recipe = ((SequencedAssemblyRecipeBuilderAccessor) this).getRecipe();
        ItemStack transitionalItem = recipe.getTransitionalItem();
        recipe.getSequence().add(new SequencedRecipe<>(builder
                .apply(transitionalItem.getItem(), ResourceLocation.withDefaultNamespace("dummy"))
                .output(transitionalItem).build()));
        return this;
    }
}
