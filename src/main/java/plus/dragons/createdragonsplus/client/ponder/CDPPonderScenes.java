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

package plus.dragons.createdragonsplus.client.ponder;

import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import plus.dragons.createdragonsplus.client.ponder.scenes.CDPFanScenes;

public class CDPPonderScenes {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        var registration = helper.<ItemProviderEntry<?, ?>>withKeyFunction(RegistryEntry::getId);
        registration.forComponents(AllBlocks.ENCASED_FAN)
                .addStoryBoard("bulk_coloring", CDPFanScenes::bulkColoring)
                .addStoryBoard("bulk_freezing", CDPFanScenes::bulkFreezing);
    }
}
