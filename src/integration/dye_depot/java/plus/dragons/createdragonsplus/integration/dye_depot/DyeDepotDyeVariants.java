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

package plus.dragons.createdragonsplus.integration.dye_depot;

import net.minecraft.resources.ResourceLocation;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeColors;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariant;
import plus.dragons.createdragonsplus.common.fluids.dye.RegisterDyeVariantsEvent;
import plus.dragons.createdragonsplus.integration.ModIntegration;

public class DyeDepotDyeVariants {
    public static void register(RegisterDyeVariantsEvent event) {
        register(event, "amber", "Amber", 0xF0A11B);
        register(event, "aqua", "Aqua", 0x48C8C8);
        register(event, "beige", "Beige", 0xC9B38C);
        register(event, "coral", "Coral", 0xFF6F61);
        register(event, "forest", "Forest", 0x2E6B3F);
        register(event, "ginger", "Ginger", 0xC46A2B);
        register(event, "indigo", "Indigo", 0x4B3F8F);
        register(event, "maroon", "Maroon", 0x7F2438);
        register(event, "mint", "Mint", 0x8ED9A9);
        register(event, "navy", "Navy", 0x1F355D);
        register(event, "olive", "Olive", 0x7A7F28);
        register(event, "rose", "Rose", 0xD75A7A);
        register(event, "slate", "Slate", 0x5E6A75);
        register(event, "tan", "Tan", 0xB88B5A);
        register(event, "teal", "Teal", 0x147A78);
        register(event, "verdant", "Verdant", 0x5DAF45);
    }

    private static void register(RegisterDyeVariantsEvent event, String name, String displayName, int color) {
        event.register(new DyeVariant(
                ResourceLocation.fromNamespaceAndPath(ModIntegration.Constants.DYE_DEPOT, name),
                ModIntegration.Constants.DYE_DEPOT + "_" + name,
                displayName,
                color,
                DyeColors.modDyeItemTag(ModIntegration.Constants.DYE_DEPOT, name),
                ResourceLocation.fromNamespaceAndPath(ModIntegration.Constants.DYE_DEPOT, name + "_dye"),
                ResourceLocation.fromNamespaceAndPath(ModIntegration.Constants.DYE_DEPOT, name + "_concrete"),
                null,
                ModIntegration.Constants.DYE_DEPOT));
    }
}
