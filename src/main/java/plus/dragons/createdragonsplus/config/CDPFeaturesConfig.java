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

import plus.dragons.createdragonsplus.common.CDPCommon;

public class CDPFeaturesConfig extends FeaturesConfig {
    public final ConfigFeature dyeFluids = feature(
            true,
            "dye_fluids",
            Comments.dyeFluids
    );
    public final ConfigFeature fluidHatch = feature(
            true,
            "fluid_hatch",
            Comments.fluidHatch
    );
    public final ConfigFeature blazeUpgradeSmithingTemplate = feature(
            false,
            "blaze_upgrade_smithing_template",
            Comments.blazeUpgradeSmithingTemplate
    );

    public CDPFeaturesConfig() {
        super(CDPCommon.ID);
    }

    static class Comments {
        static final String dyeFluids =
                "If Dye Fluids should be enabled and craftable from Dyes";
        static final String fluidHatch =
                "If Fluid Hatch should be enabled";
        static final String blazeUpgradeSmithingTemplate =
                "If Blaze Upgrade Smithing Template should be enabled and generated in loot";
    }
}
