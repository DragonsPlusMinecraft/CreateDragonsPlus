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

package plus.dragons.createdragonsplus.integration.create_dnd;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions;
import plus.dragons.createdragonsplus.integration.ModIntegration;

@Mod(CDPCommon.ID)
public class CreateDndExtension {
    public CreateDndExtension(IEventBus modBus) {
        if (ModIntegration.CREATE_DND.enabled())
            modBus.register(new Common());
    }

    public static class Common {
        @SubscribeEvent
        private void construct(final FMLConstructModEvent event) {
            CreateDndFanCompat.register();
            CDPIntegrationContributions.registerSandingCatalystTag(TagKey.create(
                    Registries.BLOCK,
                    ModIntegration.CREATE_DND.asResource("fan_processing_catalysts/sanding")));
        }
    }
}
