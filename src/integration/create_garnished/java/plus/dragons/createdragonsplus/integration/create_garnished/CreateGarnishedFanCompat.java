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

package plus.dragons.createdragonsplus.integration.create_garnished;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariant;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariantRegistry;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.ColoringRecipe;
import plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingRecipe;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions.ColoringCompat;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions.StandardFanProcessingCompat;
import plus.dragons.createdragonsplus.integration.ModIntegration;

public class CreateGarnishedFanCompat {
    public static void register() {
        CDPIntegrationContributions.registerColoringCompat(new Coloring());
        CDPIntegrationContributions.registerFreezingCompat(new Freezing());
    }

    private static class Coloring implements ColoringCompat {
        @Override
        public boolean canProcess(DyeVariant variant, ItemStack stack, Level level) {
            var recipeType = dyeBlowingRecipe(variant);
            if (!recipeType.isBound())
                return false;
            return level.getRecipeManager()
                    .getRecipeFor(recipeType.get(), new SingleRecipeInput(stack), level)
                    .isPresent();
        }

        @Override
        public Optional<List<ItemStack>> process(DyeVariant variant, ItemStack stack, Level level) {
            return processRecipe(dyeBlowingRecipe(variant), stack, level);
        }

        @Override
        public Optional<List<ProcessingOutput>> getProcessingOutputs(DyeVariant variant, ItemStack stack, Level level) {
            var recipeType = dyeBlowingRecipe(variant);
            if (!recipeType.isBound())
                return Optional.empty();
            return level.getRecipeManager()
                    .getRecipeFor(recipeType.get(), new SingleRecipeInput(stack), level)
                    .map(recipe -> List.copyOf(recipe.value().getRollableResults()));
        }

        @Override
        public void gatherJeiRecipes(RecipeManager manager, List<RecipeHolder<ColoringRecipe>> recipes) {
            for (var variant : DyeVariantRegistry.all()) {
                var recipeType = dyeBlowingRecipe(variant);
                if (!recipeType.isBound())
                    continue;
                manager.getAllRecipesFor(recipeType.get()).forEach(holder -> recipes
                        .add(new RecipeHolder<>(holder.id(), ColoringRecipe.builder(holder.id(), variant.id())
                                .withItemIngredients(holder.value().getIngredients())
                                .withItemOutputs(holder.value().getRollableResults().toArray(ProcessingOutput[]::new))
                                .build())));
            }
        }

        private static DeferredHolder<RecipeType<?>, RecipeType<StandardProcessingRecipe<SingleRecipeInput>>> dyeBlowingRecipe(DyeVariant variant) {
            return ModIntegration.CREATE_GARNISHED.recipeType(variant.serializedName() + "_dye_blowing");
        }
    }

    private static class Freezing implements StandardFanProcessingCompat<FreezingRecipe> {
        private final DeferredHolder<FanProcessingType, FanProcessingType> type = ModIntegration.CREATE_GARNISHED.fanType("freezing");
        private final DeferredHolder<RecipeType<?>, RecipeType<StandardProcessingRecipe<SingleRecipeInput>>> recipe = ModIntegration.CREATE_GARNISHED.recipeType("freezing");

        @Override
        public boolean isValidAt(Level level, BlockPos pos) {
            return type.isBound() && type.get().isValidAt(level, pos);
        }

        @Override
        public boolean canProcess(ItemStack stack, Level level) {
            return canProcessRecipe(recipe, stack, level);
        }

        @Override
        public Optional<List<ItemStack>> process(ItemStack stack, Level level) {
            return processRecipe(recipe, stack, level);
        }

        @Override
        public void gatherJeiRecipes(RecipeManager manager, List<RecipeHolder<FreezingRecipe>> recipes) {
            if (!recipe.isBound())
                return;
            manager.getAllRecipesFor(recipe.get()).forEach(holder -> recipes
                    .add(new RecipeHolder<>(holder.id(), FreezingRecipe.builder(holder.id())
                            .withItemIngredients(holder.value().getIngredients())
                            .withItemOutputs(holder.value().getRollableResults().toArray(ProcessingOutput[]::new))
                            .build())));
        }
    }

    private static boolean canProcessRecipe(DeferredHolder<RecipeType<?>, RecipeType<StandardProcessingRecipe<SingleRecipeInput>>> recipeType,
            ItemStack stack, Level level) {
        if (!recipeType.isBound())
            return false;
        return level.getRecipeManager()
                .getRecipeFor(recipeType.get(), new SingleRecipeInput(stack), level)
                .isPresent();
    }

    private static Optional<List<ItemStack>> processRecipe(DeferredHolder<RecipeType<?>, RecipeType<StandardProcessingRecipe<SingleRecipeInput>>> recipeType,
            ItemStack stack, Level level) {
        if (!recipeType.isBound())
            return Optional.empty();
        return level.getRecipeManager()
                .getRecipeFor(recipeType.get(), new SingleRecipeInput(stack), level)
                .map(recipe -> RecipeApplier.applyRecipeOn(level, stack, recipe.value(), false));
    }
}
