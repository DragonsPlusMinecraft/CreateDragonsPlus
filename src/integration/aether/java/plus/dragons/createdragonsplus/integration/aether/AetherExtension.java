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

package plus.dragons.createdragonsplus.integration.aether;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.integration.aether.common.kinetics.fan.freezing.AetherFreezingCompat;
import plus.dragons.createdragonsplus.integration.aether.common.registry.CDPAetherFanProcessingTypes;
import plus.dragons.createdragonsplus.integration.aether.common.registry.CDPAetherItemAttributes;
import plus.dragons.createdragonsplus.integration.aether.config.CDPAetherConfig;

@Mod(CDPCommon.ID)
public class AetherExtension {
    public AetherExtension(IEventBus modBus, ModContainer modContainer) {
        if (ModIntegration.AETHER.enabled())
            modBus.register(new Common(modBus, modContainer));
    }

    public static class Common {
        private final IEventBus modBus;
        private final ModContainer modContainer;

        public Common(IEventBus modBus, ModContainer modContainer) {
            this.modBus = modBus;
            this.modContainer = modContainer;
        }

        @SubscribeEvent
        private void construct(final FMLConstructModEvent event) {
            CDPAetherFanProcessingTypes.register(modBus);
            CDPAetherItemAttributes.register(modBus);
            modBus.register(new CDPAetherConfig(modContainer));
            AetherFreezingCompat.register();
        }
    }
}
