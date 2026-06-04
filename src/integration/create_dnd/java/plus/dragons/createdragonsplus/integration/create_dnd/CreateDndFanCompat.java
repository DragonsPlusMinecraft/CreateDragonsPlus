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

package plus.dragons.createdragonsplus.integration.create_dnd;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import plus.dragons.createdragonsplus.common.kinetics.fan.ending.EndingRecipe;
import plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingRecipe;
import plus.dragons.createdragonsplus.common.kinetics.fan.sanding.SandingRecipe;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions.StandardFanProcessingCompat;
import plus.dragons.createdragonsplus.integration.ModIntegration;

public class CreateDndFanCompat {
    private static Optional<Item> industrialFan;

    public static void register() {
        CDPIntegrationContributions.registerFreezingCompat(new Freezing());
        CDPIntegrationContributions.registerSandingCompat(new Sanding());
        CDPIntegrationContributions.registerEndingCompat(new Ending());
        CDPIntegrationContributions.registerFanCatalysts(CreateDndFanCompat::addFanCatalysts);
    }

    private static void addFanCatalysts(List<Supplier<? extends ItemStack>> catalysts) {
        if (industrialFan == null)
            industrialFan = DeferredHolder.create(Registries.ITEM, ModIntegration.CREATE_DND.asResource("industrial_fan")).asOptional();
        industrialFan.ifPresent(item -> catalysts.add(() -> new ItemStack(item)));
    }

    private static class Freezing implements StandardFanProcessingCompat<FreezingRecipe> {
        private final DeferredHolder<FanProcessingType, FanProcessingType> type = ModIntegration.CREATE_DND.fanType("freezing");
        private final DeferredHolder<RecipeType<?>, RecipeType<StandardProcessingRecipe<SingleRecipeInput>>> recipe = ModIntegration.CREATE_DND.recipeType("freezing");

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

    private static class Sanding implements StandardFanProcessingCompat<SandingRecipe> {
        private final DeferredHolder<FanProcessingType, FanProcessingType> type = ModIntegration.CREATE_DND.fanType("sanding");
        private final DeferredHolder<RecipeType<?>, RecipeType<StandardProcessingRecipe<SingleRecipeInput>>> recipe = ModIntegration.CREATE_DND.recipeType("sanding");

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
        public void gatherJeiRecipes(RecipeManager manager, List<RecipeHolder<SandingRecipe>> recipes) {
            if (!recipe.isBound())
                return;
            manager.getAllRecipesFor(recipe.get()).forEach(holder -> recipes
                    .add(new RecipeHolder<>(holder.id(), SandingRecipe.builder(holder.id())
                            .withItemIngredients(holder.value().getIngredients())
                            .withItemOutputs(holder.value().getRollableResults().toArray(ProcessingOutput[]::new))
                            .build())));
        }
    }

    private static class Ending implements StandardFanProcessingCompat<EndingRecipe> {
        private final DeferredHolder<FanProcessingType, FanProcessingType> type = ModIntegration.CREATE_DND.fanType("dragon_breathing");
        private final DeferredHolder<RecipeType<?>, RecipeType<StandardProcessingRecipe<SingleRecipeInput>>> recipe = ModIntegration.CREATE_DND.recipeType("dragon_breathing");

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
        public void gatherJeiRecipes(RecipeManager manager, List<RecipeHolder<EndingRecipe>> recipes) {
            if (!recipe.isBound())
                return;
            manager.getAllRecipesFor(recipe.get()).forEach(holder -> recipes
                    .add(new RecipeHolder<>(holder.id(), EndingRecipe.builder(holder.id())
                            .withItemIngredients(holder.value().getIngredients())
                            .withItemOutputs(holder.value().getRollableResults().toArray(ProcessingOutput[]::new))
                            .build())));
        }

        @Override
        public void affectEntity(Entity entity, Level level) {
            if (type.isBound())
                type.get().affectEntity(entity, level);
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
