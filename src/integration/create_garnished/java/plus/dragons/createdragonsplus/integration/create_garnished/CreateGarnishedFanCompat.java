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

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import java.util.List;
import java.util.Optional;
import net.dakotapride.garnished.recipe.DyeBlowingFanRecipe;
import net.dakotapride.garnished.recipe.DyeBlowingFanRecipe.DyeBlowingWrapper;
import net.dakotapride.garnished.recipe.FreezingFanRecipe;
import net.dakotapride.garnished.recipe.FreezingFanRecipe.FreezingWrapper;
import net.dakotapride.garnished.registry.GarnishedRecipeTypes;
import net.dakotapride.garnished.registry.GarnishedTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariant;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariantRegistry;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.ColoringRecipe;
import plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingRecipe;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions.ColoringCompat;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions.StandardFanProcessingCompat;

public class CreateGarnishedFanCompat {
    public static void register() {
        CDPIntegrationContributions.registerColoringCompat(new Coloring());
        CDPIntegrationContributions.registerFreezingCompat(new Freezing());
    }

    private static class Coloring implements ColoringCompat {
        @Override
        public boolean canProcess(DyeVariant variant, ItemStack stack, Level level) {
            return findDyeRecipe(variant, stack, level).isPresent();
        }

        @Override
        public Optional<List<ItemStack>> process(DyeVariant variant, ItemStack stack, Level level) {
            return findDyeRecipe(variant, stack, level)
                    .map(recipe -> RecipeApplier.applyRecipeOn(level, stack, recipe, true));
        }

        @Override
        public Optional<List<ProcessingOutput>> getProcessingOutputs(DyeVariant variant, ItemStack stack, Level level) {
            return findDyeRecipe(variant, stack, level)
                    .map(recipe -> List.copyOf(recipe.getRollableResults()));
        }

        @Override
        public void gatherJeiRecipes(RecipeManager manager, List<ColoringRecipe> recipes) {
            for (var variant : DyeVariantRegistry.all()) {
                var recipeType = dyeBlowingRecipe(variant);
                if (recipeType == null)
                    continue;
                manager.<DyeBlowingWrapper, DyeBlowingFanRecipe>getAllRecipesFor(recipeType.getType())
                        .forEach(recipe -> recipes.add(ColoringRecipe.builder(recipe.getId(), variant.id())
                                .withItemIngredients(recipe.getIngredients())
                                .withItemOutputs(recipe.getRollableResults().toArray(ProcessingOutput[]::new))
                                .build()));
            }
        }
    }

    private static class Freezing implements StandardFanProcessingCompat<FreezingRecipe> {
        @Override
        public boolean isValidAt(Level level, BlockPos pos) {
            return level.getFluidState(pos).is(GarnishedTags.FAN_FREEZING_PROCESSING_FLUID_TAG)
                    || level.getBlockState(pos).is(GarnishedTags.FAN_FREEZING_PROCESSING_TAG);
        }

        @Override
        public boolean canProcess(ItemStack stack, Level level) {
            return findFreezingRecipe(stack, level).isPresent();
        }

        @Override
        public Optional<List<ItemStack>> process(ItemStack stack, Level level) {
            return findFreezingRecipe(stack, level)
                    .map(recipe -> RecipeApplier.applyRecipeOn(level, stack, recipe, false));
        }

        @Override
        public void gatherJeiRecipes(RecipeManager manager, List<FreezingRecipe> recipes) {
            manager.<FreezingWrapper, FreezingFanRecipe>getAllRecipesFor(GarnishedRecipeTypes.FREEZING.getType())
                    .forEach(recipe -> recipes.add(FreezingRecipe.builder(recipe.getId())
                            .withItemIngredients(recipe.getIngredients())
                            .withItemOutputs(recipe.getRollableResults().toArray(ProcessingOutput[]::new))
                            .build()));
        }
    }

    private static Optional<DyeBlowingFanRecipe> findDyeRecipe(DyeVariant variant, ItemStack stack, Level level) {
        var recipeType = dyeBlowingRecipe(variant);
        if (recipeType == null)
            return Optional.empty();
        var wrapper = new DyeBlowingWrapper();
        wrapper.setItem(0, stack);
        return recipeType.find(wrapper, level);
    }

    private static Optional<FreezingFanRecipe> findFreezingRecipe(ItemStack stack, Level level) {
        var wrapper = new FreezingWrapper();
        wrapper.setItem(0, stack);
        return GarnishedRecipeTypes.FREEZING.find(wrapper, level);
    }

    private static @Nullable GarnishedRecipeTypes dyeBlowingRecipe(DyeVariant variant) {
        DyeColor color = variant.vanillaColor();
        if (color == null)
            return null;
        return switch (color) {
            case WHITE -> GarnishedRecipeTypes.WHITE_DYE_BLOWING;
            case ORANGE -> GarnishedRecipeTypes.ORANGE_DYE_BLOWING;
            case MAGENTA -> GarnishedRecipeTypes.MAGENTA_DYE_BLOWING;
            case LIGHT_BLUE -> GarnishedRecipeTypes.LIGHT_BLUE_DYE_BLOWING;
            case YELLOW -> GarnishedRecipeTypes.YELLOW_DYE_BLOWING;
            case LIME -> GarnishedRecipeTypes.LIME_DYE_BLOWING;
            case PINK -> GarnishedRecipeTypes.PINK_DYE_BLOWING;
            case GRAY -> GarnishedRecipeTypes.GRAY_DYE_BLOWING;
            case LIGHT_GRAY -> GarnishedRecipeTypes.LIGHT_GRAY_DYE_BLOWING;
            case CYAN -> GarnishedRecipeTypes.CYAN_DYE_BLOWING;
            case PURPLE -> GarnishedRecipeTypes.PURPLE_DYE_BLOWING;
            case BLUE -> GarnishedRecipeTypes.BLUE_DYE_BLOWING;
            case BROWN -> GarnishedRecipeTypes.BROWN_DYE_BLOWING;
            case GREEN -> GarnishedRecipeTypes.GREEN_DYE_BLOWING;
            case RED -> GarnishedRecipeTypes.RED_DYE_BLOWING;
            case BLACK -> GarnishedRecipeTypes.BLACK_DYE_BLOWING;
        };
    }
}
