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

package plus.dragons.createdragonsplus.common.fluids.dye;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.CDPCommon;

public record DyeVariant(
        ResourceLocation id,
        String serializedName,
        String displayName,
        int color,
        TagKey<Item> dyeItemTag,
        ResourceLocation dyeItemId,
        ResourceLocation concreteBlockId,
        @Nullable DyeColor vanillaColor,
        @Nullable String requiredModId) {
    public boolean isVanilla() {
        return "minecraft".equals(id.getNamespace());
    }

    public boolean isAvailable() {
        return requiredModId == null || ModList.get().isLoaded(requiredModId);
    }

    public String fluidName() {
        return isVanilla() ? id.getPath() + "_dye" : id.getNamespace() + "_" + id.getPath() + "_dye";
    }

    public String fanProcessingName() {
        return "coloring_" + serializedName;
    }

    public TagKey<Fluid> coloringCatalystFluidTag() {
        return TagKey.create(Registries.FLUID,
                CDPCommon.asResource("fan_processing_catalysts/coloring/" + serializedName));
    }

    public TagKey<Block> coloringCatalystBlockTag() {
        return TagKey.create(Registries.BLOCK,
                CDPCommon.asResource("fan_processing_catalysts/coloring/" + serializedName));
    }

    public ItemStack dyeItemStack() {
        var item = BuiltInRegistries.ITEM.get(dyeItemId);
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }
}
