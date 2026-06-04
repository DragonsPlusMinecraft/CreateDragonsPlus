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

public class CDPSEServerConfig extends ConfigBase {
    public final CDPSEFluidConfig fluid = nested(1, CDPSEFluidConfig::new, Comments.fluids);
    public final CDPAirCurrentBlockInteractionConfig airCurrentBlockInteraction = nested(1, CDPAirCurrentBlockInteractionConfig::new, Comments.airCurrentBlockInteraction);

    @Override
    public String getName() {
        return "server-simulated-extension";
    }

    static class Comments {
        static final String fluids = "Parameters and abilities of fluids and fluid operating components";
        static final String airCurrentBlockInteraction = "Control panel for block Interaction of air current through fluid should be enabled";
    }
}
