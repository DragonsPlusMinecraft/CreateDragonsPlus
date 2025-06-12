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
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.kinetics.fan.sanding.SandingRecipe;
import plus.dragons.createdragonsplus.common.registry.CDPBlocks;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.data.internal.CDPLang;
import plus.dragons.createdragonsplus.integration.jei.CDPJeiPlugin;
import plus.dragons.createdragonsplus.integration.jei.widget.FanProcessingIcon;
import plus.dragons.createdragonsplus.util.FieldsNullabilityUnknownByDefault;

@FieldsNullabilityUnknownByDefault
public class FanSandingCategory extends ProcessingViaFanCategory<SandingRecipe> {
    public static final mezz.jei.api.recipe.RecipeType<RecipeHolder<SandingRecipe>> TYPE = mezz.jei.api.recipe.RecipeType.createRecipeHolderType(CDPRecipes.SANDING.getId());
    private HolderSet<Block> catalystBlocks;
    private BlockState[] catalystStates;

    private FanSandingCategory(Info<SandingRecipe> info) {
        super(info);
    }

    public static FanSandingCategory create() {
        var id = CDPCommon.asResource("fan_sanding");
        var title = CDPLang.description("recipe", id).component();
        var background = new EmptyBackground(178, 72);
        var icon = new Icon();
        var catalyst = AllBlocks.ENCASED_FAN.asStack();
        catalyst.set(DataComponents.CUSTOM_NAME, CDPLang.description("recipe", id, "fan").component().withStyle(style -> style.withItalic(false)));
        var info = new Info<>(TYPE, title, background, icon, FanSandingCategory::getAllRecipes, List.of(() -> catalyst));
        return new FanSandingCategory(info);
    }

    @Override
    protected void renderAttachedBlock(GuiGraphics graphics) {
        var optional = BuiltInRegistries.BLOCK.getTag(CDPBlocks.MOD_TAGS.fanSandingCatalysts);
        if (optional.isEmpty())
            return;
        if (catalystBlocks != optional.get()) {
            catalystBlocks = optional.get();
            catalystStates = catalystBlocks.stream()
                    .map(Holder::value)
                    .map(Block::defaultBlockState)
                    .toArray(BlockState[]::new);
        }
        if (catalystStates.length == 0)
            return;
        GuiGameElement.of(catalystStates[(AnimationTickHolder.getTicks() / 20) % catalystStates.length])
                .scale(SCALE)
                .atLocal(0, 0, 2)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(graphics);
    }

    @Override
    public boolean isHandled(RecipeHolder<SandingRecipe> recipe) {
        var tag = BuiltInRegistries.BLOCK.getTag(CDPBlocks.MOD_TAGS.fanSandingCatalysts);
        return tag.isPresent() && tag.get().size() > 0;
    }

    private static List<RecipeHolder<SandingRecipe>> getAllRecipes() {
        var level = CDPJeiPlugin.getLevel();
        var manager = CDPJeiPlugin.getRecipeManager();
        var recipes = new ArrayList<>(manager.getAllRecipesFor(CDPRecipes.SANDING.getType()));
        manager.getAllRecipesFor(AllRecipeTypes.SANDPAPER_POLISHING.<SingleRecipeInput, SandPaperPolishingRecipe>getType())
                .stream()
                .filter(AllRecipeTypes.CAN_BE_AUTOMATED)
                .map(SandingRecipe::convertSandPaperPolishing)
                .forEach(recipes::add);
        return recipes;
    }

    protected static class Icon extends FanProcessingIcon {
        private HolderSet<Block> catalystBlocks;
        private ItemStack[] catalystStacks;

        @Override
        protected ItemStack getCatalyst() {
            var optional = BuiltInRegistries.BLOCK.getTag(CDPBlocks.MOD_TAGS.fanSandingCatalysts);
            if (optional.isEmpty())
                return ItemStack.EMPTY;
            if (catalystBlocks != optional.get()) {
                catalystBlocks = optional.get();
                catalystStacks = catalystBlocks.stream()
                        .map(Holder::value)
                        .map(ItemStack::new)
                        .toArray(ItemStack[]::new);
            }
            if (catalystStacks.length == 0)
                return ItemStack.EMPTY;
            return catalystStacks[(AnimationTickHolder.getTicks() / 20) % catalystStacks.length];
        }
    }
}
