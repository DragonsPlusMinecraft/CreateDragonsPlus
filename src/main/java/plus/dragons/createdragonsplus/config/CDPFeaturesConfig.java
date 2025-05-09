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

package plus.dragons.createdragonsplus.config;

import plus.dragons.createdragonsplus.common.CDPCommon;

public class CDPFeaturesConfig extends FeaturesConfig {
    private final ConfigGroup fluid = group(1, "fluid", Comments.fluid);
    public final ConfigFeature dyeFluids = feature(
            true,
            "fluid/dye",
            Comments.dyeFluids).addAlias("dye_fluids");
    public final ConfigFeature dragonBreathFluid = feature(
            true,
            "fluid/dragon_breath",
            Comments.dragonBreathFluid).addAlias("dragons_breath_fluid");
    private final ConfigGroup block = group(1, "block", Comments.block);
    public final ConfigFeature fluidHatch = feature(
            true,
            "block/fluid_hatch",
            Comments.fluidHatch).addAlias("fluid_hatch");
    private final ConfigGroup item = group(1, "item", Comments.item);
    public final ConfigFeature blazeUpgradeSmithingTemplate = feature(
            false,
            "item/blaze_upgrade_smithing_template",
            Comments.blazeUpgradeSmithingTemplate).addAlias("blaze_upgrade_smithing_template");
    private final ConfigGroup recipe = group(1, "recipe", Comments.recipe);
    public final ConfigFeature generateAutomaticBrewingRecipeForDragonBreathFluid = feature(
            true,
            "recipe/automatic_brewing/dragon_breath",
            Comments.generateAutomaticBrewingRecipeForDragonBreathFluid);
    public final ConfigFeature generateSandPaperPolishingRecipeForPolishedBlocks = feature(
            true,
            "recipe/sand_paper_polishing/polished_blocks",
            Comments.generateSandPaperPolishingRecipeForPolishedBlocks);
    public final ConfigFeature generateSandPaperPolishingRecipeForOxidizedBlocks = feature(
            true,
            "recipe/sand_paper_polishing/oxidized_blocks",
            Comments.generateSandPaperPolishingRecipeForOxidizedBlocks);
    public final ConfigFeature generateSandPaperPolishingRecipeForWaxedBlocks = feature(
            true,
            "recipe/sand_paper_polishing/waxed_blocks",
            Comments.generateSandPaperPolishingRecipeForWaxedBlocks);

    public CDPFeaturesConfig() {
        super(CDPCommon.ID);
    }

    static class Comments {
        static final String fluid = "Fluid Feature Elements";
        static final String dyeFluids = "If Dye Fluids should be enabled";
        static final String dragonBreathFluid = "If Dragon's Breath Fluid should be enabled";
        static final String block = "Block Feature Elements";
        static final String fluidHatch = "If Fluid Hatch should be enabled";
        static final String item = "Item Feature Elements";
        static final String blazeUpgradeSmithingTemplate = "If Blaze Upgrade Smithing Template should be enabled";
        static final String recipe = "Recipe Feature Elements";
        static final String generateAutomaticBrewingRecipeForDragonBreathFluid = "If Automated Brewing Recipes for Dragon's Breath Fluid should be generated";
        static final String generateSandPaperPolishingRecipeForPolishedBlocks = "If Sand Paper Polishing Recipes for Polished Blocks should be generated";
        static final String generateSandPaperPolishingRecipeForOxidizedBlocks = "If Sand Paper Polishing Recipes for Oxidized Blocks should be generated";
        static final String generateSandPaperPolishingRecipeForWaxedBlocks = "If Sand Paper Polishing Recipes for Waxed Blocks should be generated";
    }
}
