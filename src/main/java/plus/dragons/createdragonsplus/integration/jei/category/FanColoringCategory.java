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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.item.ItemHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeColors;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.ColoringRecipe;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.integration.jei.CDPJeiPlugin;
import plus.dragons.createdragonsplus.integration.jei.widget.FanProcessingIcon;
import plus.dragons.createdragonsplus.util.CDPLang;
import plus.dragons.createdragonsplus.util.FieldsNullabilityUnknownByDefault;

public class FanColoringCategory extends ProcessingViaFanCategory<ColoringRecipe> {
    public static final mezz.jei.api.recipe.RecipeType<ColoringRecipe> TYPE = new mezz.jei.api.recipe.RecipeType<>(CDPRecipes.COLORING.getId(), ColoringRecipe.class);

    private FanColoringCategory(Info<ColoringRecipe> info) {
        super(info);
    }

    public static FanColoringCategory create() {
        var id = CDPCommon.asResource("fan_coloring");
        var title = CDPLang.description("recipe", id).component();
        var background = new EmptyBackground(178, 72);
        var icon = new Icon();
        var catalyst = AllBlocks.ENCASED_FAN.asStack();
        catalyst.set(DataComponents.CUSTOM_NAME, CDPLang.description("recipe", id, "fan").component().withStyle(style -> style.withItalic(false)));
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
        var recipes = new ArrayList<>(manager.getAllRecipesFor(CDPRecipes.COLORING.getType()));
        for (var color : DyeColors.ALL) {
            DeferredHolder<RecipeType<?>, RecipeType<ProcessingRecipe<SingleRecipeInput>>> createGarnishedRecipe = DeferredHolder.create(Registries.RECIPE_TYPE, ModIntegration.CREATE_GARNISHED.asResource(color.getSerializedName() + "_dye_blowing"));
            if (!createGarnishedRecipe.isBound())
                continue;
            manager.getAllRecipesFor(createGarnishedRecipe.get()).forEach(holder -> recipes
                    .add(new RecipeHolder<>(holder.id(), ColoringRecipe.builder(holder.id(), color)
                            .withItemIngredients(holder.value().getIngredients())
                            .withItemOutputs(holder.value().getRollableResults().toArray(ProcessingOutput[]::new))
                            .build())));
        }
        for (var holder : manager.getAllRecipesFor(RecipeType.CRAFTING)) {
            var crafting = holder.value();
            if (crafting.isSpecial())
                continue;
            var ingredients = crafting.getIngredients();
            var result = crafting.getResultItem(level.registryAccess());
            if (crafting.canCraftInDimensions(2, 1) && ingredients.size() == 2 && result.getCount() == 1) {
                for (var color : DyeColors.ALL) {
                    convert2x1(holder.id().withSuffix("_as_coloring"), color, ingredients, result).ifPresent(recipes::add);
                }
            } else if (crafting.canCraftInDimensions(3, 3) && ingredients.size() == 9 && result.getCount() == 8) {
                for (var color : DyeColors.ALL) {
                    convert3x3(holder.id().withSuffix("_as_coloring"), color, ingredients, result).ifPresent(recipes::add);
                }
            }
        }
        recipes.sort(Comparator
                .<RecipeHolder<ColoringRecipe>, DyeColor>comparing(holder -> holder.value().getColor(), DyeColors.creativeModeTabOrder())
                .thenComparing(RecipeHolder::id));
        return recipes;
    }

    private static Optional<RecipeHolder<ColoringRecipe>> convert2x1(ResourceLocation id, DyeColor color, List<Ingredient> ingredients, ItemStack result) {
        var dye = new ItemStack(DyeItem.byColor(color));
        if (ingredients.get(0).test(dye)) {
            var recipe = ColoringRecipe.builder(id, color)
                    .require(ingredients.get(1))
                    .output(result)
                    .build();
            return Optional.of(new RecipeHolder<>(id, recipe));
        } else if (ingredients.get(1).test(dye)) {
            var recipe = ColoringRecipe.builder(id, color)
                    .require(ingredients.get(0))
                    .output(result)
                    .build();
            return Optional.of(new RecipeHolder<>(id, recipe));
        }
        return Optional.empty();
    }

    private static Optional<RecipeHolder<ColoringRecipe>> convert3x3(ResourceLocation id, DyeColor color, List<Ingredient> ingredients, ItemStack result) {
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
        return Optional.of(new RecipeHolder<>(id, recipe));
    }

    @FieldsNullabilityUnknownByDefault
    protected static class Icon extends FanProcessingIcon {
        private ItemStack[] catalystStacks;

        @Override
        protected ItemStack getCatalyst() {
            if (catalystStacks == null) {
                catalystStacks = Arrays.stream(DyeColors.ALL)
                        .map(CDPFluids.DYES_BY_COLOR::get)
                        .flatMap(entry -> entry.getBucket().stream())
                        .map(ItemStack::new)
                        .toArray(ItemStack[]::new);
            }
            return catalystStacks[(AnimationTickHolder.getTicks() / 20) % catalystStacks.length];
        }
    }
}
