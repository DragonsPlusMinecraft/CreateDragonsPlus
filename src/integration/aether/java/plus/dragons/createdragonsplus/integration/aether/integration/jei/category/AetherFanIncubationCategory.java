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

package plus.dragons.createdragonsplus.integration.aether.integration.jei.category;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.integration.jei.CDPJeiPlugin;

public class AetherFanIncubationCategory extends ProcessingViaFanCategory<AetherIncubationDisplayRecipe> {
    public static final RecipeType<RecipeHolder<AetherIncubationDisplayRecipe>> TYPE = RecipeType.createRecipeHolderType(CDPCommon.asResource("aether_fan_incubation"));
    private static final String TITLE_KEY = "recipe.create_dragons_plus.aether_fan_incubation";
    private static final String FAN_KEY = "recipe.create_dragons_plus.aether_fan_incubation.fan";
    private static final String ENTITY_TOOLTIP_KEY = "recipe.create_dragons_plus.aether_fan_incubation.entity";

    private AetherFanIncubationCategory(Info<AetherIncubationDisplayRecipe> info) {
        super(info);
    }

    public static AetherFanIncubationCategory create() {
        var title = Component.translatable(TITLE_KEY);
        var background = new EmptyBackground(178, 72);
        var icon = new DoubleItemIcon(AllItems.PROPELLER::asStack, AetherBlocks.GOLDEN_AERCLOUD::toStack);
        var info = new Info<>(TYPE, title, background, icon, AetherFanIncubationCategory::getAllRecipes,
                List.of(() -> AetherFanCategorySupport.fanCatalyst(FAN_KEY)));
        return new AetherFanIncubationCategory(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AetherIncubationDisplayRecipe recipe, IFocusGroup focuses) {
        builder
                .addSlot(RecipeIngredientRole.INPUT, 21, 48)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getIngredients().getFirst());
        builder
                .addSlot(RecipeIngredientRole.OUTPUT, 141, 48)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStack(recipe.getDisplayOutput())
                .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable(ENTITY_TOOLTIP_KEY, recipe.getMoaType())));
    }

    @Override
    public void draw(AetherIncubationDisplayRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, iRecipeSlotsView, graphics, mouseX, mouseY);
    }

    @Override
    protected void renderAttachedBlock(GuiGraphics graphics) {
        AetherFanCategorySupport.renderGoldenAercloud(graphics);
    }

    private static List<RecipeHolder<AetherIncubationDisplayRecipe>> getAllRecipes() {
        var manager = CDPJeiPlugin.getRecipeManager();
        var recipes = new ArrayList<RecipeHolder<AetherIncubationDisplayRecipe>>();
        manager.getAllRecipesFor(AetherRecipeTypes.INCUBATION.get()).forEach(holder -> recipes.add(new RecipeHolder<>(
                holder.id(),
                new AetherIncubationDisplayRecipe(holder.value()))));
        return recipes;
    }
}
