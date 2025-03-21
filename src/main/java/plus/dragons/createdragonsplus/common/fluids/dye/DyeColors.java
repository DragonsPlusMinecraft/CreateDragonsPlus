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

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import net.minecraft.Util;
import net.minecraft.world.item.DyeColor;

public class DyeColors {
    public static final DyeColor[] ALL = Util.make(new DyeColor[16], colors ->
            System.arraycopy(DyeColor.values(), 0, colors, 0, 16));
    public static final DyeColor[] CREATIVE_MODE_TAB = new DyeColor[]{
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
    public static final EnumMap<DyeColor, String> LOCALIZATION = Util.make(new EnumMap<>(DyeColor.class), map -> {
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

    public static Comparator<DyeColor> creativeModeTabOrder() {
        var list = Arrays.asList(CREATIVE_MODE_TAB);
        return Comparator.comparingInt(list::indexOf);
    }
}
