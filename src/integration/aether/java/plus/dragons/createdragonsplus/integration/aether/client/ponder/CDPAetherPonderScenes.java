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

package plus.dragons.createdragonsplus.integration.aether.client.ponder;

import com.aetherteam.aether.block.AetherBlocks;
import com.simibubi.create.AllBlocks;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class CDPAetherPonderScenes {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        var registration = helper.<ItemLike>withKeyFunction(CatnipServices.REGISTRIES::getKeyOrThrow);
        registration.forComponents(AllBlocks.ENCASED_FAN.asItem(), AetherBlocks.GOLDEN_AERCLOUD.get())
                .addStoryBoard("bulk_enchanting", CDPAetherFanScenes::bulkEnchanting);
    }
}
