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

package plus.dragons.createdragonsplus.config;

import net.createmod.catnip.config.ConfigBase;

public class CDPClientConfig extends ConfigBase {
    public final ConfigFloat dyeVisionMultiplier = f(1f, 1f, 256f,
            "dyeVisionMultiplier",
            Comments.dyeVisionMultiplier);
    public final ConfigFloat dragonBreathVisionMultiplier = f(1f, 1f, 256f,
            "dragonBreathVisionMultiplier",
            Comments.dragonBreathVisionMultiplier);

    @Override
    public String getName() {
        return "client";
    }

    static class Comments {
        static final String dyeVisionMultiplier = "The vision range through Dye Fluids will be multiplied by this factor";
        static final String dragonBreathVisionMultiplier = "The vision range through Dragon's Breath Fluid will be multiplied by this factor";
    }
}
