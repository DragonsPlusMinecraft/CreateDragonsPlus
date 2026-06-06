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

package plus.dragons.createdragonsplus.integration.dyenamics;

import net.minecraft.resources.ResourceLocation;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeColors;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariant;
import plus.dragons.createdragonsplus.common.fluids.dye.RegisterDyeVariantsEvent;
import plus.dragons.createdragonsplus.integration.ModIntegration;

public class DyenamicsDyeVariants {
    public static void register(RegisterDyeVariantsEvent event) {
        register(event, "amber", "Amber", 0xFFBF00);
        register(event, "aquamarine", "Aquamarine", 0x7FFFD4);
        register(event, "bubblegum", "Bubblegum", 0xFF85C8);
        register(event, "cherenkov", "Cherenkov", 0x18F0FF);
        register(event, "conifer", "Conifer", 0x1F5F3A);
        register(event, "fluorescent", "Fluorescent", 0xCCFF00);
        register(event, "honey", "Honey", 0xE6A21A);
        register(event, "icy_blue", "Icy Blue", 0x9EDBFF);
        register(event, "lavender", "Lavender", 0xB497D6);
        register(event, "maroon", "Maroon", 0x800020);
        register(event, "mint", "Mint", 0x98FF98);
        register(event, "navy", "Navy", 0x000080);
        register(event, "peach", "Peach", 0xFFCBA4);
        register(event, "persimmon", "Persimmon", 0xEC5800);
        register(event, "rose", "Rose", 0xFF5C8A);
        register(event, "spring_green", "Spring Green", 0x00FF7F);
        register(event, "ultramarine", "Ultramarine", 0x3F00FF);
        register(event, "wine", "Wine", 0x722F37);
    }

    private static void register(RegisterDyeVariantsEvent event, String name, String displayName, int color) {
        event.register(new DyeVariant(
                ResourceLocation.fromNamespaceAndPath(ModIntegration.Constants.DYENAMICS, name),
                ModIntegration.Constants.DYENAMICS + "_" + name,
                displayName,
                color,
                DyeColors.modDyeItemTag(ModIntegration.Constants.DYENAMICS, name),
                ResourceLocation.fromNamespaceAndPath(ModIntegration.Constants.DYENAMICS, name + "_dye"),
                ResourceLocation.fromNamespaceAndPath(ModIntegration.Constants.DYENAMICS, name + "_concrete"),
                null,
                ModIntegration.Constants.DYENAMICS));
    }
}
