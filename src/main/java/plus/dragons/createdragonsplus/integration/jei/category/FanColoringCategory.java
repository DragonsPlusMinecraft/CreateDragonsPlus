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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.item.ItemHelper;
import com.tterrag.registrate.util.entry.FluidEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeColors;
import plus.dragons.createdragonsplus.common.recipe.color.ColoringRecipe;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.integration.jei.CDPJeiPlugin;
import plus.dragons.createdragonsplus.util.CDPLang;

public class FanColoringCategory extends ProcessingViaFanCategory<ColoringRecipe> {
    public static final RecipeType<ColoringRecipe> TYPE = new RecipeType<>(CDPRecipes.COLORING.getId(), ColoringRecipe.class);

    protected FanColoringCategory(Info<ColoringRecipe> info) {
        super(info);
    }

    public static FanColoringCategory create() {
        var id = CDPCommon.asResource("fan_coloring");
        var title = CDPLang.description("recipe", id).component();
        var background = new EmptyBackground(178, 72);
        var icon = new Icon();
        var catalyst = AllBlocks.ENCASED_FAN.asStack();
        catalyst.set(DataComponents.CUSTOM_NAME, CDPLang.description("recipe", id, "fan").component());
        var info = new Info<>(TYPE, title, background, icon, FanColoringCategory::getAllRecipes, List.of(() -> catalyst));
        return new FanColoringCategory(info);
    }

    @Override
    public void draw(ColoringRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        renderWidgets(graphics, recipe, mouseX, mouseY);

        PoseStack matrixStack = graphics.pose();

        matrixStack.pushPose();
        translateFan(matrixStack);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-12.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));

        AnimatedKinetics.defaultBlockElement(AllPartialModels.ENCASED_FAN_INNER)
                .rotateBlock(180, 0, AnimatedKinetics.getCurrentAngle() * 16)
                .scale(SCALE)
                .render(graphics);

        AnimatedKinetics.defaultBlockElement(AllBlocks.ENCASED_FAN.getDefaultState())
                .rotateBlock(0, 180, 0)
                .atLocal(0, 0, 0)
                .scale(SCALE)
                .render(graphics);

        renderAttachedBlock(graphics, recipe.getColor());
        matrixStack.popPose();
    }

    protected void renderAttachedBlock(GuiGraphics graphics, DyeColor color) {
        GuiGameElement.of((Fluid) CDPFluids.DYES_BY_COLOR.get(color).getSource())
                .scale(SCALE)
                .atLocal(0, 0, 2)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(graphics);
    }

    /**
     * @deprecated use color-sensitive version instead.
     */
    @Override
    @Deprecated
    protected void renderAttachedBlock(GuiGraphics graphics) {}

    private static List<RecipeHolder<ColoringRecipe>> getAllRecipes() {
        var level = CDPJeiPlugin.getLevel();
        var manager = CDPJeiPlugin.getRecipeManager();
        List<RecipeHolder<ColoringRecipe>> allColoring = new ArrayList<>(manager.getAllRecipesFor(CDPRecipes.COLORING.getType()));
        List<RecipeHolder<CraftingRecipe>> allCrafting = manager
                .getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING);
        for (var holder : allCrafting) {
            var crafting = holder.value();
            if (crafting.isSpecial())
                continue;
            var ingredients = crafting.getIngredients();
            var result = crafting.getResultItem(level.registryAccess());
            if (crafting.canCraftInDimensions(2, 1) && ingredients.size() == 2 && result.getCount() == 1) {
                for (var color : DyeColors.ALL) {
                    convert2x1(holder.id(), color, ingredients, result)
                            .ifPresent(coloring -> allColoring.add(new RecipeHolder<>(holder.id(), coloring)));
                }
            } else if (crafting.canCraftInDimensions(3, 3) && ingredients.size() == 9 && result.getCount() == 8) {
                for (var color : DyeColors.ALL) {
                    convert3x3(holder.id(), color, ingredients, result)
                            .ifPresent(coloring -> allColoring.add(new RecipeHolder<>(holder.id(), coloring)));
                }
            }
        }
        allColoring.sort(Comparator
                .<RecipeHolder<ColoringRecipe>, DyeColor>comparing(holder -> holder.value().getColor(), DyeColors.creativeModeTabOrder())
                .thenComparing(RecipeHolder::id));
        return allColoring;
    }

    private static Optional<ColoringRecipe> convert2x1(ResourceLocation id, DyeColor color, List<Ingredient> ingredients, ItemStack result) {
        var dye = new ItemStack(DyeItem.byColor(color));
        if (ingredients.get(0).test(dye)) {
            var recipe = ColoringRecipe.builder(id, color)
                    .require(ingredients.get(1))
                    .output(result)
                    .build();
            return Optional.of(recipe);
        } else if (ingredients.get(1).test(dye)) {
            var recipe = ColoringRecipe.builder(id, color)
                    .require(ingredients.get(0))
                    .output(result)
                    .build();
            return Optional.of(recipe);
        }
        return Optional.empty();
    }

    private static Optional<ColoringRecipe> convert3x3(ResourceLocation id, DyeColor color, List<Ingredient> ingredients, ItemStack result) {
        var dye = new ItemStack(DyeItem.byColor(color));
        Ingredient dyeable = null;
        boolean hasDye = false;
        for (var ingredient : ingredients) {
            if (ingredient.hasNoItems()) {
                return Optional.empty();
            } else if (ingredient.test(dye)) {
                if (hasDye)
                    return Optional.empty();
                hasDye = true;
            } else if (dyeable == null) {
                dyeable = ingredient;
            } else if (!ItemHelper.matchIngredients(dyeable, ingredient)) {
                return Optional.empty();
            }
        }
        if (!hasDye || dyeable == null)
            return Optional.empty();
        var recipe = ColoringRecipe.builder(id, color)
                .require(dyeable)
                .output(result.copyWithCount(1))
                .build();
        return Optional.of(recipe);
    }

    protected static class Icon implements IDrawable {
        private final ItemStack propeller = AllItems.PROPELLER.asStack();
        private final ItemStack[] buckets = Arrays.stream(DyeColors.CREATIVE_MODE_TAB)
                .map(CDPFluids.DYES_BY_COLOR::get)
                .map(FluidEntry::getBucket)
                .flatMap(Optional::stream)
                .map(ItemStack::new)
                .toArray(ItemStack[]::new);

        @Override
        public int getWidth() {
            return 18;
        }

        @Override
        public int getHeight() {
            return 18;
        }

        @Override
        public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
            PoseStack poseStack = graphics.pose();

            RenderSystem.enableDepthTest();
            poseStack.pushPose();
            poseStack.translate(xOffset, yOffset, 0);

            poseStack.pushPose();
            poseStack.translate(1, 1, 0);
            GuiGameElement.of(this.propeller).render(graphics);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(10, 10, 100);
            poseStack.scale(.5f, .5f, .5f);
            int index = (AnimationTickHolder.getTicks() / 20) % this.buckets.length;
            GuiGameElement.of(this.buckets[index]).render(graphics);
            poseStack.popPose();

            poseStack.popPose();
        }
    }
}
