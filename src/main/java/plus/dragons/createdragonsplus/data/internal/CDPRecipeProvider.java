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

import static com.simibubi.create.AllBlocks.ITEM_DRAIN;
import static net.minecraft.world.item.Items.*;
import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;
import static plus.dragons.createdragonsplus.common.registry.CDPBlocks.FLUID_HATCH;
import static plus.dragons.createdragonsplus.common.registry.CDPItems.BLAZE_UPGRADE_SMITHING_TEMPLATE;
import static plus.dragons.createdragonsplus.data.recipe.VanillaRecipeBuilders.shaped;
import static plus.dragons.createdragonsplus.data.recipe.VanillaRecipeBuilders.shapeless;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.common.Tags;
import plus.dragons.createdragonsplus.common.recipe.freeze.FreezingRecipe;
import plus.dragons.createdragonsplus.config.CDPConfig;

public class CDPRecipeProvider extends RegistrateRecipeProvider {
    public CDPRecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
        super(REGISTRATE, output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        buildMachineRecipes(output);
        buildMaterialRecipes(output);
        buildFreezingRecipes(output);
    }

    private void buildMachineRecipes(RecipeOutput output) {
        shapeless().output(FLUID_HATCH)
                .require(Tags.Items.INGOTS_COPPER)
                .require(ITEM_DRAIN)
                .unlockedBy("has_item_drain", has(ITEM_DRAIN))
                .withCondition(CDPConfig.features().fluidHatch)
                .accept(output);
    }

    private void buildMaterialRecipes(RecipeOutput output) {
        shaped().output(BLAZE_UPGRADE_SMITHING_TEMPLATE, 2)
                .define('t', BLAZE_UPGRADE_SMITHING_TEMPLATE)
                .define('n', NETHERRACK)
                .define('b', BLAZE_ROD)
                .pattern("btb")
                .pattern("bnb")
                .pattern("bbb")
                .unlockedBy("has_template", has(BLAZE_UPGRADE_SMITHING_TEMPLATE))
                .withCondition(CDPConfig.features().blazeUpgradeSmithingTemplate)
                .accept(output);
    }

    private void buildFreezingRecipes(RecipeOutput output) {
        FreezingRecipe.builder(REGISTRATE.asResource("packed_ice_from_ice"))
                .require(ICE)
                .output(PACKED_ICE)
                .build(output);
        FreezingRecipe.builder(REGISTRATE.asResource("blue_ice_from_packed_ice"))
                .require(PACKED_ICE)
                .output(BLUE_ICE)
                .build(output);
        FreezingRecipe.builder(REGISTRATE.asResource("slime_ball_from_magma_cream"))
                .require(MAGMA_CREAM)
                .output(SLIME_BALL)
                .build(output);
        FreezingRecipe.builder(REGISTRATE.asResource("breeze_rod_from_blaze_rod"))
                .require(BLAZE_ROD)
                .output(BREEZE_ROD)
                .build(output);
    }
}
