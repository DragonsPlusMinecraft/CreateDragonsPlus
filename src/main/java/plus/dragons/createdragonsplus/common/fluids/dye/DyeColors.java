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

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import plus.dragons.createdragonsplus.common.CDPCommon;

public class DyeColors {
    public static final DyeColor[] VANILLA_CREATIVE_MODE_TAB = new DyeColor[] {
            DyeColor.WHITE,
            DyeColor.LIGHT_GRAY,
            DyeColor.GRAY,
            DyeColor.BLACK,
            DyeColor.BROWN,
            DyeColor.RED,
            DyeColor.ORANGE,
            DyeColor.YELLOW,
            DyeColor.LIME,
            DyeColor.GREEN,
            DyeColor.CYAN,
            DyeColor.LIGHT_BLUE,
            DyeColor.BLUE,
            DyeColor.PURPLE,
            DyeColor.MAGENTA,
            DyeColor.PINK
    };
    public static final Map<DyeColor, String> LOCALIZATION = Util.make(new EnumMap<>(DyeColor.class), map -> {
        map.put(DyeColor.WHITE, "White");
        map.put(DyeColor.LIGHT_GRAY, "Light Gray");
        map.put(DyeColor.GRAY, "Gray");
        map.put(DyeColor.BLACK, "Black");
        map.put(DyeColor.BROWN, "Brown");
        map.put(DyeColor.RED, "Red");
        map.put(DyeColor.ORANGE, "Orange");
        map.put(DyeColor.YELLOW, "Yellow");
        map.put(DyeColor.LIME, "Lime");
        map.put(DyeColor.GREEN, "Green");
        map.put(DyeColor.CYAN, "Cyan");
        map.put(DyeColor.LIGHT_BLUE, "Light Blue");
        map.put(DyeColor.BLUE, "Blue");
        map.put(DyeColor.PURPLE, "Purple");
        map.put(DyeColor.MAGENTA, "Magenta");
        map.put(DyeColor.PINK, "Pink");
    });

    public static void registerVanilla(DyeVariantRegistry.Builder builder) {
        for (var color : VANILLA_CREATIVE_MODE_TAB) {
            builder.add(new DyeVariant(
                    new ResourceLocation(color.getSerializedName()),
                    color.getSerializedName(),
                    LOCALIZATION.get(color),
                    textureDiffuseColor(color),
                    vanillaDyeItemTag(color),
                    getKey(DyeItem.byColor(color)),
                    new ResourceLocation(color.getSerializedName() + "_concrete"),
                    color,
                    null));
        }
    }

    public static TagKey<Item> vanillaDyeItemTag(DyeColor color) {
        return TagKey.create(Registries.ITEM, CDPCommon.asResource("dyes/minecraft/" + color.getSerializedName()));
    }

    public static TagKey<Item> modDyeItemTag(String modId, String color) {
        return TagKey.create(Registries.ITEM, CDPCommon.asResource("dyes/" + modId + "/" + color));
    }

    private static ResourceLocation getKey(Item item) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
    }

    private static int textureDiffuseColor(DyeColor color) {
        float[] components = color.getTextureDiffuseColors();
        return (int) (components[0] * 255) << 16
                | (int) (components[1] * 255) << 8
                | (int) (components[2] * 255);
    }
}
