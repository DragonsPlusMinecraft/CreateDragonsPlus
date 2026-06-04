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
    public final ConfigInt fragileFluidTankCapacity = i(1000, 500,
            "fragileFluidTankCapacity",
            Comments.fragileFluidTankCapacity,
            RequiresRestart.SERVER.asComment());
    public final ConfigInt fragileFluidTankEffectAmplifiedUnit = i(250, 1,
            "fragileFluidTankEffectAmplifiedUnit",
            Comments.fragileFluidTankEffectAmplifiedUnit,
            RequiresRestart.SERVER.asComment());
    public final ConfigInt fragileFluidTankAffectMaxRadius = i(8, 2,
            "fragileFluidTankAffectMaxRadius",
            Comments.fragileFluidTankAffectMaxRadius);
    public final ConfigBool fragileFluidTankLavaIgniteBlock = b(true,
            "fragileFluidTankLavaIgniteBlock",
            Comments.fragileFluidTankLavaIgniteBlock);
    public final ConfigBool fragileFluidTankLavaSpreadFire = b(true,
            "fragileFluidTankLavaSpreadFire",
            Comments.fragileFluidTankLavaSpreadFire);
    public final ConfigBool fragileFluidTankDyeColorBlock = b(true,
            "fragileFluidTankDyeColorBlock",
            Comments.fragileFluidTankDyeColorBlock);

    @Override
    public String getName() {
        return "fluids";
    }

    static class Comments {
        static final String fragileFluidTankCapacity = "The amount of liquid a Fragile Fluid Tank can hold (mB).";
        static final String fragileFluidTankEffectAmplifiedUnit = "The amount of liquid makes effect of breaking Fragile Fluid Tank amplified. (such as potion, tea)" +
                "For example: x (mB) of potion gives y ticks of duration, then 2x (mB) gives 2y ticks of duration-\"x\" is this option. Specific effect alters by implementation of specific fluid.";
        static final String fragileFluidTankAffectMaxRadius = "The maximum radius of the area affected when a full Fragile Fluid Tank breaks on impact";
        static final String fragileFluidTankLavaIgniteBlock = "Should Fragile Fluid Tank containing lava ignite block when it breaks";
        static final String fragileFluidTankLavaSpreadFire = "Should Fragile Fluid Tank containing lava spread fire when it breaks";
        static final String fragileFluidTankDyeColorBlock = "Should Fragile Fluid Tank containing dye fluid color block when it breaks";
    }
}
