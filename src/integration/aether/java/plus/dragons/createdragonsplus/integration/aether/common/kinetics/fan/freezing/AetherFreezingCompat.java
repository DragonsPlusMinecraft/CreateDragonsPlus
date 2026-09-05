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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
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
                .map(recipe -> AetherCookingRecipeResults.apply(recipe, stack, level));
    }

    @Override
    public void gatherJeiRecipes(RecipeManager manager, List<plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingRecipe> recipes) {
        manager.getAllRecipesFor(AetherRecipeTypes.FREEZING.get()).forEach(recipe -> {
            if (recipe.getIngredients().isEmpty())
                return;

            // Keep interchangeable inputs together only when their outputs (including
            // container remainders, counts and NBT) are identical.
            var inputGroups = new LinkedHashMap<List<CompoundTag>, List<ItemStack>>();
            for (var input : recipe.getIngredients().get(0).getItems()) {
                var outputKey = AetherCookingRecipeResults.getDisplayOutputs(recipe, input).stream()
                        .map(ItemStack::serializeNBT)
                        .toList();
                inputGroups.computeIfAbsent(outputKey, key -> new ArrayList<>()).add(input);
            }

            int variant = 0;
            for (var inputs : inputGroups.values()) {
                var id = recipe.getId();
                var ingredients = recipe.getIngredients();
                if (inputGroups.size() > 1) {
                    id = new ResourceLocation(id.getNamespace(), id.getPath() + "/jei_" + variant++);
                    ingredients = NonNullList.of(Ingredient.EMPTY, Ingredient.of(inputs.stream()));
                }
                var outputs = AetherCookingRecipeResults.getDisplayOutputs(recipe, inputs.get(0)).stream()
                        .map(stack -> new ProcessingOutput(stack, 1.0F))
                        .toArray(ProcessingOutput[]::new);
                recipes.add(plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingRecipe.builder(id)
                        .withItemIngredients(ingredients)
                        .withItemOutputs(outputs)
                        .build());
            }
        });
    }

    private static Optional<FreezingRecipe> findRecipe(ItemStack stack, Level level) {
        return level.getRecipeManager()
                .getRecipeFor(AetherRecipeTypes.FREEZING.get(), new SimpleContainer(stack), level);
    }
}
