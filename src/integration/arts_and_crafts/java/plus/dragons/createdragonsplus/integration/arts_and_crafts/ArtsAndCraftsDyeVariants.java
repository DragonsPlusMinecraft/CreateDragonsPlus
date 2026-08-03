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

package plus.dragons.createdragonsplus.integration.arts_and_crafts;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeColors;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariant;
import plus.dragons.createdragonsplus.common.fluids.dye.RegisterDyeVariantsEvent;
import plus.dragons.createdragonsplus.integration.ModIntegration;

public class ArtsAndCraftsDyeVariants {
    public static void register(RegisterDyeVariantsEvent event) {
        if (!ModIntegration.ARTS_AND_CRAFTS.enabled())
            return;
        event.register(new DyeVariant(
                new ResourceLocation(ModIntegration.Constants.ARTS_AND_CRAFTS, "bleached"),
                ModIntegration.Constants.ARTS_AND_CRAFTS + "_bleached",
                "Bleachdew",
                0xDDE7DD,
                DyeColors.modDyeItemTag(ModIntegration.Constants.ARTS_AND_CRAFTS, "bleached"),
                new ResourceLocation(ModIntegration.Constants.ARTS_AND_CRAFTS, "bleachdew"),
                new ResourceLocation("white_concrete"),
                DyeColor.WHITE,
                ModIntegration.Constants.ARTS_AND_CRAFTS));
    }
}
