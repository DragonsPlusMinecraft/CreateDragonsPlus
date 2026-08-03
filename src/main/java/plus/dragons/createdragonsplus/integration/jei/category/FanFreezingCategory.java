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

package plus.dragons.createdragonsplus.integration.jei.category;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingRecipe;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.data.internal.CDPLang;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions;
import plus.dragons.createdragonsplus.integration.jei.CDPJeiPlugin;

public class FanFreezingCategory extends ProcessingViaFanCategory.MultiOutput<FreezingRecipe> {
    public static final mezz.jei.api.recipe.RecipeType<FreezingRecipe> TYPE = new mezz.jei.api.recipe.RecipeType<>(
            CDPRecipes.FREEZING.getId(), FreezingRecipe.class);

    private FanFreezingCategory(Info<FreezingRecipe> info) {
        super(info);
    }

    public static FanFreezingCategory create() {
        var id = CDPCommon.asResource("fan_freezing");
        var title = CDPLang.description("recipe", id).component();
        var background = new EmptyBackground(178, 72);
        var icon = new DoubleItemIcon(AllItems.PROPELLER::asStack, Items.POWDER_SNOW_BUCKET::getDefaultInstance);
        var catalyst = AllBlocks.ENCASED_FAN.asStack();
        catalyst.setHoverName(CDPLang.description("recipe", id, "fan").component()
                .withStyle(style -> style.withItalic(false)));
        var info = new Info<>(TYPE, title, background, icon, FanFreezingCategory::getAllRecipes, CDPIntegrationContributions.gatherFanCatalysts(catalyst));
        return new FanFreezingCategory(info);
    }

    @Override
    protected void renderAttachedBlock(GuiGraphics graphics) {
        GuiGameElement.of(Blocks.POWDER_SNOW.defaultBlockState())
                .scale(SCALE)
                .atLocal(0, 0, 2)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(graphics);
    }

    private static List<FreezingRecipe> getAllRecipes() {
        var manager = CDPJeiPlugin.getRecipeManager();
        var recipes = new ArrayList<>(manager.<RecipeWrapper, FreezingRecipe>getAllRecipesFor(CDPRecipes.FREEZING.getType()));
        CDPIntegrationContributions.gatherFreezingJeiRecipes(manager, recipes);
        return recipes;
    }
}
