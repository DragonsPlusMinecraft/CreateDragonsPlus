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

public class CDPAirCurrentBlockInteractionConfig extends ConfigBase {
    public final ConfigBool enableBulkBlastingBlockInteraction = b(true,
            "enableBulkBlastingBlockInteraction",
            Comments.enableBulkBlastingBlockInteraction);
    public final ConfigBool enableBulkSmokingBlockInteraction = b(false,
            "enableBulkSmokingBlockInteraction",
            Comments.enableBulkSmokingBlockInteraction);
    public final ConfigBool bulkBlastingIgniteBlock = b(false,
            "bulkBlastingIgniteBlock",
            Comments.bulkBlastingIgniteBlock);
    public final ConfigBool bulkBlastingSpreadFire = b(true,
            "bulkBlastingSpreadFire",
            Comments.bulkBlastingSpreadFire);
    public final ConfigBool enableBulkSplashingBlockInteraction = b(true,
            "enableBulkSplashingBlockInteraction",
            Comments.enableBulkSplashingBlockInteraction);
    public final ConfigBool enableBulkHauntingBlockInteraction = b(false,
            "enableBulkHauntingBlockInteraction",
            Comments.enableBulkHauntingBlockInteraction);
    public final ConfigBool enableBulkFreezingBlockInteraction = b(true,
            "enableBulkFreezingBlockInteraction",
            Comments.enableBulkFreezingBlockInteraction);
    public final ConfigBool enableBulkEndingBlockInteraction = b(false,
            "enableBulkEndingBlockInteraction",
            Comments.enableBulkEndingBlockInteraction);
    public final ConfigBool enableBulkSandingBlockInteraction = b(false,
            "enableBulkSandingBlockInteraction",
            Comments.enableBulkSandingBlockInteraction);
    public final ConfigBool enableBulkColoringBlockInteraction = b(true,
            "enableBulkColoringBlockInteraction",
            Comments.enableBulkColoringBlockInteraction);

    @Override
    public String getName() {
        return "recipes";
    }

    static class Comments {
        static final String enableBulkBlastingBlockInteraction = "If air current block interaction of Bulk Blasting should be enabled";
        static final String bulkBlastingIgniteBlock = "If air current of Bulk Blasting ignite block when possible";
        static final String bulkBlastingSpreadFire = "If air current of Bulk Blasting spread fire on flammable block when possible";
        static final String enableBulkSmokingBlockInteraction = "If air current block interaction of Bulk Smoking should be enabled";
        static final String enableBulkSplashingBlockInteraction = "If air current block interaction of Bulk Splashing should be enabled";
        static final String enableBulkHauntingBlockInteraction = "If air current block interaction of Bulk Haunting should be enabled";
        static final String enableBulkFreezingBlockInteraction = "If air current block interaction of Bulk Freezing should be enabled";
        static final String enableBulkEndingBlockInteraction = "If air current block interaction of Bulk Ending should be enabled";
        static final String enableBulkSandingBlockInteraction = "If air current block interaction of Bulk Sanding should be enabled";
        static final String enableBulkColoringBlockInteraction = "If air current block interaction of Bulk Coloring should be enabled";
    }
}
