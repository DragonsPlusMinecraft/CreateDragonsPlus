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
import net.createmod.catnip.config.ui.ConfigAnnotations.RequiresRestart;

public class CDPCommonConfig extends ConfigBase {
    public final CDPFeaturesConfig features = nested(1, CDPFeaturesConfig::new,
            Comments.features,
            Comments.featuresOverride,
            RequiresRestart.BOTH.asComment()
    );

    @Override
    public String getName() {
        return "common";
    }

    static class Comments {
        static final String features =
                "Enable/Disable features of Create: Dragons Plus";
        static final String featuresOverride =
                "Mods depending on certain features may forcibly enable/disable them, " +
                "in that case, the corresponding config will be ignored";
    }
}
