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

package plus.dragons.createdragonsplus.integration.aether.common.kinetics.fan.freezing;

import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.recipes.item.FreezingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions.StandardFanProcessingCompat;
import plus.dragons.createdragonsplus.integration.aether.common.kinetics.fan.enchanting.AetherCookingRecipeResults;

public class AetherFreezingCompat implements StandardFanProcessingCompat<plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingRecipe> {
    public static void register() {
        CDPIntegrationContributions.registerFreezingCompat(new AetherFreezingCompat());
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        return findRecipe(stack, level).isPresent();
    }

    @Override
    public Optional<List<ItemStack>> process(ItemStack stack, Level level) {
        return findRecipe(stack, level)
                .map(holder -> AetherCookingRecipeResults.apply(holder.value(), stack, level));
    }

    @Override
    public void gatherJeiRecipes(RecipeManager manager, List<RecipeHolder<plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingRecipe>> recipes) {
        manager.getAllRecipesFor(AetherRecipeTypes.FREEZING.get()).forEach(holder -> {
            var displayInput = firstIngredient(holder.value());
            var outputs = AetherCookingRecipeResults.getDisplayOutputs(holder.value(), displayInput).stream()
                    .map(stack -> new ProcessingOutput(stack, 1.0F))
                    .toArray(ProcessingOutput[]::new);
            recipes.add(new RecipeHolder<>(holder.id(), plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingRecipe.builder(holder.id())
                    .withItemIngredients(holder.value().getIngredients())
                    .withItemOutputs(outputs)
                    .build()));
        });
    }

    private static Optional<RecipeHolder<FreezingRecipe>> findRecipe(ItemStack stack, Level level) {
        return level.getRecipeManager()
                .getRecipeFor(AetherRecipeTypes.FREEZING.get(), new SingleRecipeInput(stack), level);
    }

    private static ItemStack firstIngredient(FreezingRecipe recipe) {
        var ingredients = recipe.getIngredients();
        if (ingredients.isEmpty())
            return ItemStack.EMPTY;
        var stacks = ingredients.getFirst().getItems();
        return stacks.length == 0 ? ItemStack.EMPTY : stacks[0];
    }
}
