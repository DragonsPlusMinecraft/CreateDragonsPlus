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
import com.aetherteam.aether.recipe.recipes.item.AbstractAetherCookingRecipe;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.integration.jei.CDPJeiPlugin;

public class AetherFanEnchantingCategory extends ProcessingViaFanCategory<AetherEnchantingDisplayRecipe> {
    public static final RecipeType<AetherEnchantingDisplayRecipe> TYPE = new RecipeType<>(
            CDPCommon.asResource("aether_fan_enchanting"), AetherEnchantingDisplayRecipe.class);
    private static final String TITLE_KEY = "recipe.create_dragons_plus.aether_fan_enchanting";
    private static final String FAN_KEY = "recipe.create_dragons_plus.aether_fan_enchanting.fan";

    private AetherFanEnchantingCategory(Info<AetherEnchantingDisplayRecipe> info) {
        super(info);
    }

    public static AetherFanEnchantingCategory create() {
        var title = Component.translatable(TITLE_KEY);
        var background = new EmptyBackground(178, 72);
        var icon = new DoubleItemIcon(AllItems.PROPELLER::asStack,
                () -> AetherBlocks.GOLDEN_AERCLOUD.get().asItem().getDefaultInstance());
        List<Supplier<? extends ItemStack>> catalysts = List.of(() -> AetherFanCategorySupport.fanCatalyst(FAN_KEY));
        var info = new Info<>(TYPE, title, background, icon, AetherFanEnchantingCategory::getAllRecipes,
                catalysts);
        return new AetherFanEnchantingCategory(info);
    }

    @Override
    protected void renderAttachedBlock(GuiGraphics graphics) {
        AetherFanCategorySupport.renderGoldenAercloud(graphics);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AetherEnchantingDisplayRecipe recipe, IFocusGroup focuses) {
        var outputs = recipe.getDisplayOutputs();
        var xOffsetAmount = 1 - Math.min(3, outputs.size());

        var input = builder
                .addSlot(RecipeIngredientRole.INPUT, 5 * xOffsetAmount + 21, 48)
                .setBackground(getRenderedSlot(), -1, -1);
        if (recipe.isRepairing()) {
            input.addItemStack(recipe.getDisplayInput())
                    .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component
                            .translatable("recipe.create_dragons_plus.aether_fan_enchanting.repairing")
                            .withStyle(ChatFormatting.GRAY)));
        } else {
            input.addIngredients(recipe.getIngredients().get(0));
        }

        for (int i = 0; i < outputs.size(); i++) {
            int xOffset = (i % 3) * 19 + 9 * xOffsetAmount;
            int yOffset = (i / 3) * -19 + (outputs.size() > 9 ? 8 : 0);
            var output = builder
                    .addSlot(RecipeIngredientRole.OUTPUT, 141 + xOffset, 48 + yOffset)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addItemStack(outputs.get(i));
            if (recipe.isRepairing()) {
                output.addRichTooltipCallback((view, tooltip) -> tooltip.add(Component
                        .translatable("recipe.create_dragons_plus.aether_fan_enchanting.repairing")
                        .withStyle(ChatFormatting.GRAY)));
            }
        }
    }

    @Override
    protected void renderWidgets(GuiGraphics graphics, AetherEnchantingDisplayRecipe recipe, double mouseX, double mouseY) {
        int size = recipe.getDisplayOutputs().size();
        int xOffsetAmount = 1 - Math.min(3, size);

        AllGuiTextures.JEI_SHADOW.render(graphics, 46, 29);
        getBlockShadow().render(graphics, 65, 39);
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 7 * xOffsetAmount + 54, 51);
    }

    @SuppressWarnings("unchecked")
    private static List<AetherEnchantingDisplayRecipe> getAllRecipes() {
        var manager = CDPJeiPlugin.getRecipeManager();
        var type = (net.minecraft.world.item.crafting.RecipeType<AbstractAetherCookingRecipe>) (net.minecraft.world.item.crafting.RecipeType<?>) AetherRecipeTypes.ENCHANTING.get();
        var recipes = new ArrayList<AetherEnchantingDisplayRecipe>();
        manager.getAllRecipesFor(type).forEach(recipe -> recipes.add(new AetherEnchantingDisplayRecipe(recipe)));
        return recipes;
    }
}
