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

package plus.dragons.createdragonsplus.integration.simulated.config;

import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.config.ui.ConfigAnnotations.RequiresRestart;

public class CDPSEFluidConfig extends ConfigBase {
    public final ConfigInt fragileFluidTankCapacity = i(8000, 1000,
            "fragileFluidTankCapacity",
            Comments.fragileFluidTankCapacity,
            RequiresRestart.SERVER.asComment());
    public final ConfigInt fragileFluidTankAffectMaxRadius = i(16, 2,
            "fragileFluidTankAffectMaxRadius",
            Comments.fragileFluidTankAffectMaxRadius);

    @Override
    public String getName() {
        return "fluids";
    }

    static class Comments {
        static final String fragileFluidTankCapacity = "The amount of liquid a Fragile Fluid Tank can hold (mB).";
        static final String fragileFluidTankAffectMaxRadius = "The maximum radius of the area affected when a full Fragile Fluid Tank breaks on impact";
    }
}
