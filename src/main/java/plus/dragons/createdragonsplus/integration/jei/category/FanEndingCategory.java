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
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.neoforged.neoforge.registries.DeferredHolder;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.kinetics.fan.ending.EndingRecipe;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.data.internal.CDPLang;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.integration.industrial_fan.IndustrialFanCompat;
import plus.dragons.createdragonsplus.integration.jei.CDPJeiPlugin;
import plus.dragons.createdragonsplus.util.FieldsNullabilityUnknownByDefault;

@FieldsNullabilityUnknownByDefault
public class FanEndingCategory extends ProcessingViaFanCategory.MultiOutput<EndingRecipe> {
    public static final mezz.jei.api.recipe.RecipeType<RecipeHolder<EndingRecipe>> TYPE = mezz.jei.api.recipe.RecipeType.createRecipeHolderType(CDPRecipes.ENDING.getId());

    private FanEndingCategory(Info<EndingRecipe> info) {
        super(info);
    }

    public static FanEndingCategory create() {
        var id = CDPCommon.asResource("fan_ending");
        var title = CDPLang.description("recipe", id).component();
        var background = new EmptyBackground(178, 72);
        var icon = new DoubleItemIcon(AllItems.PROPELLER::asStack, () -> new ItemStack(Items.DRAGON_BREATH));
        var catalyst = AllBlocks.ENCASED_FAN.asStack();
        catalyst.set(DataComponents.CUSTOM_NAME, CDPLang.description("recipe", id, "fan").component().withStyle(style -> style.withItalic(false)));
        var info = new Info<>(TYPE, title, background, icon, FanEndingCategory::getAllRecipes, IndustrialFanCompat.catalystWithIndustryFan(catalyst));
        return new FanEndingCategory(info);
    }

    @Override
    protected void renderAttachedBlock(GuiGraphics graphics) {
        GuiGameElement.of(new SkullBlockEntity(BlockPos.ZERO, Blocks.DRAGON_HEAD.defaultBlockState()))
                .rotateBlock(0, 180, 0)
                .scale(SCALE)
                .atLocal(0, 0, 2)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(graphics);
    }

    private static List<RecipeHolder<EndingRecipe>> getAllRecipes() {
        var manager = CDPJeiPlugin.getRecipeManager();
        var recipes = new ArrayList<>(manager.getAllRecipesFor(CDPRecipes.ENDING.getType()));
        DeferredHolder<RecipeType<?>, RecipeType<StandardProcessingRecipe<SingleRecipeInput>>> createDNDRecipe = DeferredHolder.create(Registries.RECIPE_TYPE, ModIntegration.CREATE_DND.asResource("dragon_breathing"));
        if (createDNDRecipe.isBound()) {
            manager.getAllRecipesFor(createDNDRecipe.get()).forEach(holder -> recipes
                    .add(new RecipeHolder<>(holder.id(), EndingRecipe.builder(holder.id())
                            .withItemIngredients(holder.value().getIngredients())
                            .withItemOutputs(holder.value().getRollableResults().toArray(ProcessingOutput[]::new))
                            .build())));
        }
        return recipes;
    }
}
