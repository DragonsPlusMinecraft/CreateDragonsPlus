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
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.MixingCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedMixer;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import java.util.List;
import java.util.function.Supplier;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.DyeFluidMixingRecipes;
import plus.dragons.createdragonsplus.data.internal.CDPLang;
import plus.dragons.createdragonsplus.integration.jei.widget.FanProcessingIcon;
import plus.dragons.createdragonsplus.util.CodeReference;

@CodeReference(value = MixingCategory.class, source = "create", license = "mit")
public class AutomatedColoringCategory extends BasinCategory {
    public static final mezz.jei.api.recipe.RecipeType<BasinRecipe> TYPE = new mezz.jei.api.recipe.RecipeType<>(
            CDPCommon.asResource("automated_coloring"), BasinRecipe.class);
    private final AnimatedMixer mixer = new AnimatedMixer();

    private AutomatedColoringCategory(Info<BasinRecipe> info) {
        super(info, false);
    }

    public static AutomatedColoringCategory create() {
        var id = CDPCommon.asResource("automated_coloring");
        var title = CDPLang.description("recipe", id).component();
        var background = new EmptyBackground(177, 85);
        var icon = new Icon();
        List<Supplier<? extends ItemStack>> catalysts = List.of(AllBlocks.MECHANICAL_MIXER::asStack, AllBlocks.BASIN::asStack);
        var info = new Info<>(TYPE, title, background, icon, AutomatedColoringCategory::getAllRecipes, catalysts);
        return new AutomatedColoringCategory(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BasinRecipe recipe, IFocusGroup focuses) {
        super.setRecipe(builder, recipe, focuses);
        DyeFluidMixingRecipes.getJeiRecipeColor(recipe)
                .ifPresent(color -> DyeFluidCategoryHelper.addDyeItemLookupAlias(builder, color));
    }

    @Override
    public void draw(BasinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        mixer.draw(graphics, getBackground().getWidth() / 2 + 3, 34);
    }

    private static List<BasinRecipe> getAllRecipes() {
        return FanColoringCategory.getAllRecipes().stream()
                .map(DyeFluidMixingRecipes::createJeiRecipe)
                .flatMap(java.util.Optional::stream)
                .toList();
    }

    private static class Icon extends FanProcessingIcon {
        private Icon() {
            super(AllBlocks.MECHANICAL_MIXER::asStack);
        }

        @Override
        protected ItemStack getCatalyst() {
            return DyeFluidCategoryHelper.getDyeBucketIcon();
        }
    }
}
