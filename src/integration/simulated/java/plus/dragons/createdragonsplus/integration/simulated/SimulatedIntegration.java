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

package plus.dragons.createdragonsplus.integration.simulated;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.integration.simulated.common.registry.CDPSEDataMaps;
import plus.dragons.createdragonsplus.integration.simulated.config.CDPSEConfig;

@Mod(CDPCommon.ID)
public class SimulatedIntegration {
    public SimulatedIntegration(IEventBus modBus, ModContainer modContainer) {
        if (ModIntegration.AERONAUTICS.enabled()) {
            modBus.register(new Common(modBus, modContainer));
            if (FMLLoader.getDist() == Dist.CLIENT)
                modBus.register(new Client());
        }
    }

    public static class Common {
        private final IEventBus modBus;
        private final ModContainer modContainer;

        public Common(IEventBus modBus, ModContainer modContainer) {
            this.modBus = modBus;
            this.modContainer = modContainer;
        }

        @SubscribeEvent
        public void construct(final FMLConstructModEvent event) {
            CDPSEDataMaps.register(modBus);
            modBus.register(new CDPSEConfig(modContainer));
        }

        @SubscribeEvent
        public void generate(final GatherDataEvent event) {
            var generator = event.getGenerator();
            var lookupProvider = event.getLookupProvider();
            var output = generator.getPackOutput();
            var server = event.includeServer();
        }
    }

    public static class Client {
        @SubscribeEvent
        public void construct(final FMLConstructModEvent event) {
            //NDUPonderPlugin.register();
        }
    }
}
