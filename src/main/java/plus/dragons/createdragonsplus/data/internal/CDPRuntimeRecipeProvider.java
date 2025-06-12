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

package plus.dragons.createdragonsplus.data.internal;

import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.DataMapHooks;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.recipe.UpdateRecipesEvent;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.data.recipe.CreateRecipeBuilders;

@EventBusSubscriber
public class CDPRuntimeRecipeProvider extends RecipeProvider {
    public CDPRuntimeRecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        if (CDPConfig.features().generateSandPaperPolishingRecipeForPolishedBlocks.get()) {
            buildPolishedBlockRecipes(output);
        }
    }

    private static void buildPolishedBlockRecipes(RecipeOutput output) {
        BuiltInRegistries.BLOCK.holders()
                .filter(holder -> holder.key().location().getPath().contains("polished_"))
                .forEach(holder -> {
                    var polishedId = holder.key().location();
                    var baseId = polishedId.withPath(name -> name.replace("polished_", ""));
                    if (!BuiltInRegistries.BLOCK.containsKey(baseId))
                        return;
                    var polishedItem = holder.value().asItem();
                    var baseItem = BuiltInRegistries.BLOCK.get(baseId).asItem();
                    if (polishedItem == Items.AIR || baseItem == Items.AIR)
                        return;
                    var recipeId = CDPCommon.asResource(baseId.toString().replace(':', '/'));
                    CreateRecipeBuilders.polishing(recipeId)
                            .require(baseItem)
                            .output(polishedItem)
                            .build(output);
                });
    }

    private static void buildOxidizedBlockRecipes(RecipeOutput output) {
        DataMapHooks.INVERSE_OXIDIZABLES_DATAMAP.forEach((oxidized, polished) -> {
            var oxidizedItem = oxidized.asItem();
            var polishedItem = polished.asItem();
            if (oxidizedItem == Items.AIR || polishedItem == Items.AIR)
                return;
            var oxidizedId = BuiltInRegistries.BLOCK.getKey(oxidized);
            var recipeId = CDPCommon.asResource(oxidizedId.toString().replace(':', '/'));
            CreateRecipeBuilders.polishing(recipeId)
                    .require(oxidizedItem)
                    .output(polishedItem)
                    .build(output);
        });
    }

    private static void buildWaxedBlockRecipes(RecipeOutput output) {
        DataMapHooks.INVERSE_WAXABLES_DATAMAP.forEach((waxed, polished) -> {
            var waxedItem = waxed.asItem();
            var polishedItem = polished.asItem();
            if (waxedItem == Items.AIR || polishedItem == Items.AIR)
                return;
            var waxedId = BuiltInRegistries.BLOCK.getKey(waxed);
            var recipeId = CDPCommon.asResource(waxedId.toString().replace(':', '/'));
            CreateRecipeBuilders.polishing(recipeId)
                    .require(waxedItem)
                    .output(polishedItem)
                    .build(output);
        });
    }

    @SubscribeEvent
    public static void buildRecipesForUpdate(final UpdateRecipesEvent event) {
        final RecipeOutput output = new RecipeOutput() {
            @Override
            public Builder advancement() {
                return Builder.advancement();
            }

            @Override
            public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
                event.addRecipe(new RecipeHolder<>(id, recipe));
            }
        };
        if (CDPConfig.features().generateSandPaperPolishingRecipeForOxidizedBlocks.get()) {
            buildOxidizedBlockRecipes(output);
        }
        if (CDPConfig.features().generateSandPaperPolishingRecipeForWaxedBlocks.get()) {
            buildWaxedBlockRecipes(output);
        }
    }
}
