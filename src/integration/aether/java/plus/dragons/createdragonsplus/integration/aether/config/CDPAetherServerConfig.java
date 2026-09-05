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

package plus.dragons.createdragonsplus.integration.aether.config;

import net.createmod.catnip.config.ConfigBase;

public class CDPAetherServerConfig extends ConfigBase {
    public final ConfigBool enableBulkEnchanting = b(true,
            "enableBulkEnchanting",
            Comments.enableBulkEnchanting);
    public final ConfigBool enableBulkMoaIncubation = b(true,
            "enableBulkMoaIncubation",
            Comments.enableBulkMoaIncubation);

    @Override
    public String getName() {
        return "server-aether-integration";
    }

    static class Comments {
        static final String enableBulkEnchanting = "If Bulk Enchanting for The Aether Altar should be enabled";
        static final String enableBulkMoaIncubation = "If Bulk Enchanting can incubate The Aether Moa Eggs";
    }
}
