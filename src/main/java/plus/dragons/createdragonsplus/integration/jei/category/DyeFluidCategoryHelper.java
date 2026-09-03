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

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariantRegistry;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;

final class DyeFluidCategoryHelper {
    private static ItemStack[] dyeBucketIcons;

    private DyeFluidCategoryHelper() {}

    static void addDyeItemLookupAlias(IRecipeLayoutBuilder builder, ResourceLocation color) {
        DyeVariantRegistry.get(color).ifPresent(variant -> {
            var dyeItems = Ingredient.of(variant.dyeItemTag());
            if (!dyeItems.isEmpty())
                builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST).addIngredients(dyeItems);
        });
    }

    static ItemStack getDyeBucketIcon() {
        if (dyeBucketIcons == null) {
            dyeBucketIcons = DyeVariantRegistry.all().stream()
                    .map(variant -> CDPFluids.DYES_BY_VARIANT.get(variant.id()))
                    .flatMap(entry -> entry.getBucket().stream())
                    .map(ItemStack::new)
                    .toArray(ItemStack[]::new);
        }
        if (dyeBucketIcons.length == 0)
            return ItemStack.EMPTY;
        return dyeBucketIcons[(AnimationTickHolder.getTicks() / 20) % dyeBucketIcons.length];
    }
}
