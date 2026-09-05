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

package plus.dragons.createdragonsplus.integration.aether.common.kinetics.fan.enchanting;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.blockentity.AbstractAetherFurnaceBlockEntity;
import com.aetherteam.aether.recipe.recipes.item.AbstractAetherCookingRecipe;
import com.simibubi.create.foundation.item.ItemHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.util.CodeReference;

@CodeReference(value = AbstractAetherFurnaceBlockEntity.class, targets = "burn", source = "the_aether", license = "lgpl-3.0-or-later")
public class AetherCookingRecipeResults {
    public static List<ItemStack> apply(AbstractAetherCookingRecipe recipe, ItemStack input, Level level) {
        var outputs = new ArrayList<ItemStack>();
        for (int i = 0; i < input.getCount(); i++) {
            var singleInput = input.copy();
            singleInput.setCount(1);
            var result = recipe.assemble(new SimpleContainer(singleInput), level.registryAccess());
            outputs(singleInput, result).forEach(stack -> ItemHelper.addToList(stack, outputs));
        }
        return outputs;
    }

    public static List<ItemStack> getDisplayOutputs(AbstractAetherCookingRecipe recipe, @Nullable ItemStack representativeInput) {
        var input = representativeInput == null ? ItemStack.EMPTY : representativeInput;
        return outputs(input, recipe.getResult().copy());
    }

    private static List<ItemStack> outputs(ItemStack input, ItemStack result) {
        var outputs = new ArrayList<ItemStack>();
        if (!result.isEmpty()) {
            if (!input.isEmpty() && (input.is(result.getItem()) || result.is(AetherTags.Items.SAVE_NBT_IN_RECIPE))) {
                var tag = input.getTag();
                if (tag != null)
                    result.setTag(tag.copy());
            }
            if (!input.isEmpty() && input.is(result.getItem())) {
                result.setDamageValue(0);
            }
            outputs.add(result.copy());
        }
        if (!input.isEmpty() && input.hasCraftingRemainingItem()) {
            var remainder = input.getCraftingRemainingItem();
            if (!remainder.isEmpty() && !remainder.is(result.getCraftingRemainingItem().getItem())) {
                outputs.add(remainder.copy());
            }
        }
        return outputs;
    }
}
