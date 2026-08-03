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
        register(event, "amber", "Amber", 0xD7AF00);
        register(event, "aqua", "Aqua", 0x5EF0CC);
        register(event, "beige", "Beige", 0xE1D5A3);
        register(event, "coral", "Coral", 0xDF7758);
        register(event, "forest", "Forest", 0x32A326);
        register(event, "ginger", "Ginger", 0xCF6121);
        register(event, "indigo", "Indigo", 0x331E57);
        register(event, "maroon", "Maroon", 0x7B2713);
        register(event, "mint", "Mint", 0x38CE7D);
        register(event, "navy", "Navy", 0x153D64);
        register(event, "olive", "Olive", 0x8C8F2A);
        register(event, "rose", "Rose", 0xFF5E64);
        register(event, "slate", "Slate", 0x4C5E86);
        register(event, "tan", "Tan", 0xF49C5D);
        register(event, "teal", "Teal", 0x2F7B67);
        register(event, "verdant", "Verdant", 0x255714);
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
