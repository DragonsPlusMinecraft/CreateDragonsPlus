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

import com.simibubi.create.AllRecipeTypes;
import java.util.HashSet;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.recipe.UpdateRecipesEvent;
import plus.dragons.createdragonsplus.common.registry.CDPBlocks;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.data.recipe.CreateRecipeBuilders;

@Mod.EventBusSubscriber
public class CDPRuntimeRecipeProvider extends RecipeProvider {
    public CDPRuntimeRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> output) {
        if (CDPConfig.features().generateSandPaperPolishingRecipeForPolishedBlocks.get()) {
            buildPolishedBlockRecipes(output);
        }
        if (CDPConfig.features().generateSandPaperPolishingRecipeForOxidizedBlocks.get()) {
            buildOxidizedBlockRecipes(output);
        }
        if (CDPConfig.features().generateSandPaperPolishingRecipeForWaxedBlocks.get()) {
            buildWaxedBlockRecipes(output);
        }
    }

    private static void buildPolishedBlockRecipes(Consumer<FinishedRecipe> output) {
        var generatedRecipeIds = new HashSet<ResourceLocation>();
        BuiltInRegistries.BLOCK.holders()
                .filter(holder -> holder.key().location().getPath().contains("polished_"))
                .forEach(holder -> {
                    var polishedId = holder.key().location();
                    var baseId = polishedId.withPath(name -> name.replace("polished_", ""));
                    if (!BuiltInRegistries.BLOCK.containsKey(baseId))
                        return;
                    var polishedItem = holder.value().asItem();
                    var baseBlock = BuiltInRegistries.BLOCK.get(baseId);
                    var baseItem = baseBlock.asItem();
                    if (polishedItem == Items.AIR || baseItem == Items.AIR)
                        return;
                    var recipeId = automaticPolishingRecipeId(baseId);
                    if (!generatedRecipeIds.add(recipeId))
                        return;
                    CreateRecipeBuilders.polishing(recipeId)
                            .require(baseItem)
                            .output(polishedItem)
                            .build(output);
                });
    }

    private static ResourceLocation automaticPolishingRecipeId(ResourceLocation baseId) {
        return CDPCommon.asResource(baseId.toString().replace(':', '/'));
    }

    private static void removeNotApplicablePolishedBlockRecipes(UpdateRecipesEvent event) {
        BuiltInRegistries.BLOCK.holders()
                .filter(holder -> holder.key().location().getPath().contains("polished_"))
                .forEach(holder -> {
                    var polishedId = holder.key().location();
                    var baseId = polishedId.withPath(name -> name.replace("polished_", ""));
                    var baseBlock = BuiltInRegistries.BLOCK.get(baseId);
                    if (baseBlock == Blocks.AIR || !baseBlock.defaultBlockState().is(CDPBlocks.MOD_TAGS.notApplicablePolishing))
                        return;
                    var polishedItem = holder.value().asItem();
                    var baseItem = baseBlock.asItem();
                    if (polishedItem == Items.AIR || baseItem == Items.AIR)
                        return;
                    event.getRecipe(automaticPolishingRecipeId(baseId))
                            .filter(recipe -> recipe.getType() == AllRecipeTypes.SANDPAPER_POLISHING.getType())
                            .ifPresent(event::removeRecipe);
                });
    }

    private static void buildOxidizedBlockRecipes(Consumer<FinishedRecipe> output) {
        WeatheringCopper.PREVIOUS_BY_BLOCK.get().forEach((oxidized, previous) -> {
            var oxidizedItem = oxidized.asItem();
            var previousItem = previous.asItem();
            if (oxidizedItem == Items.AIR || previousItem == Items.AIR)
                return;
            var oxidizedId = BuiltInRegistries.BLOCK.getKey(oxidized);
            var recipeId = CDPCommon.asResource(oxidizedId.toString().replace(':', '/'));
            CreateRecipeBuilders.polishing(recipeId)
                    .require(oxidizedItem)
                    .output(previousItem)
                    .build(output);
        });
    }

    private static void buildWaxedBlockRecipes(Consumer<FinishedRecipe> output) {
        HoneycombItem.WAX_OFF_BY_BLOCK.get().forEach((waxed, unwaxed) -> {
            var waxedItem = waxed.asItem();
            var unwaxedItem = unwaxed.asItem();
            if (waxedItem == Items.AIR || unwaxedItem == Items.AIR)
                return;
            var waxedId = BuiltInRegistries.BLOCK.getKey(waxed);
            var recipeId = CDPCommon.asResource(waxedId.toString().replace(':', '/'));
            CreateRecipeBuilders.polishing(recipeId)
                    .require(waxedItem)
                    .output(unwaxedItem)
                    .build(output);
        });
    }

    @SubscribeEvent
    public static void buildRecipesForUpdate(final UpdateRecipesEvent event) {
        if (CDPConfig.features().generateSandPaperPolishingRecipeForPolishedBlocks.get()) {
            removeNotApplicablePolishedBlockRecipes(event);
        }
    }
}
