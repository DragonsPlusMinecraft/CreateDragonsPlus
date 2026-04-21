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

import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.config.FeaturesConfig;

public class CDPSEFeaturesConfig extends FeaturesConfig {
    private final ConfigGroup fluid = group(1, "fluid", Comments.fluid);
    public final ConfigFeature airCurrentBlockInteraction = feature(
            true,
            "fluid/air_current_block_interaction",
            Comments.fluidAirCurrentBlockInteraction);

    public final ConfigFeature fragileFluidTank = feature(
            true,
            "fluid/fragile_fluid_tank",
            Comments.fragileFluidTank);

    public CDPSEFeaturesConfig() {
        super(CDPCommon.ID);
    }

    static class Comments {
        static final String fluid = "Fluid Feature Elements";
        static final String fluidAirCurrentBlockInteraction = "If block interaction of air current through fluid should be enabled";
        static final String fragileFluidTank = "If Fragile Fluid Tank should be enabled";
    }
}
