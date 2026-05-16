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

package plus.dragons.createdragonsplus.integration.simulated.data.internal;

import static com.simibubi.create.AllItems.COPPER_SHEET;
import static net.minecraft.world.item.Items.*;
import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;
import static plus.dragons.createdragonsplus.data.recipe.VanillaRecipeBuilders.shaped;
import static plus.dragons.createdragonsplus.integration.simulated.common.registry.CDPSEBlocks.FRAGILE_FLUID_TANK;
import static plus.dragons.createdragonsplus.integration.simulated.common.registry.CDPSEBlocks.LEVITITE_FRAGILE_FLUID_TANK;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.eriksonn.aeronautics.neoforge.index.AeroFluidsNeoForge;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import org.jetbrains.annotations.NotNull;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.integration.ModIntegration;

public class CDPSERecipeProvider extends RegistrateRecipeProvider {
    public CDPSERecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(REGISTRATE, output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        shaped().output(FRAGILE_FLUID_TANK, 1)
                .define('t', COPPER_SHEET)
                .define('n', BARREL)
                .define('g', GOLD_INGOT)
                .pattern(" t ")
                .pattern("gng")
                .pattern(" t ")
                .unlockedBy("has_copper_sheet", has(COPPER_SHEET))
                .withCondition(CDPConfig.features().fragileFluidTank)
                .withCondition(ModIntegration.SABLE.condition())
                .accept(output);

        shaped().output(LEVITITE_FRAGILE_FLUID_TANK, 8)
                .define('l', FRAGILE_FLUID_TANK)
                .define('t', AeroFluidsNeoForge.LEVITITE_BLEND.getBucket().get())
                .pattern("lll")
                .pattern("ltl")
                .pattern("lll")
                .unlockedBy("has_fragile_fluid_tank", has(AeroFluidsNeoForge.LEVITITE_BLEND.getBucket().get()))
                .withCondition(CDPConfig.features().fragileFluidTank)
                .withCondition(ModIntegration.SABLE.condition())
                .accept(output);
    }

    @Override
    public final @NotNull String getName() {
        return "Simulated Extension Recipes";
    }
}
