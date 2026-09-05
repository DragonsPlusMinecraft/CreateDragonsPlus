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

import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.integration.aether.client.ponder.CDPAetherPonderPlugin;
import plus.dragons.createdragonsplus.integration.aether.common.kinetics.fan.freezing.AetherFreezingCompat;
import plus.dragons.createdragonsplus.integration.aether.common.registry.CDPAetherFanProcessingTypes;
import plus.dragons.createdragonsplus.integration.aether.common.registry.CDPAetherItemAttributes;
import plus.dragons.createdragonsplus.integration.aether.config.CDPAetherConfig;

@Mod.EventBusSubscriber(modid = CDPCommon.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AetherExtension {
    @SubscribeEvent
    public static void construct(final FMLConstructModEvent event) {
        if (!ModIntegration.AETHER.enabled())
            return;
        CDPIntegrationContributions.registerFanProcessingTypes(CDPAetherFanProcessingTypes::register);
        CDPIntegrationContributions.registerItemAttributes(CDPAetherItemAttributes::register);
        FMLJavaModLoadingContext.get().getModEventBus().register(new CDPAetherConfig());
        AetherFreezingCompat.register();
        if (DatagenModLoader.isRunningDataGen())
            REGISTRATE.registerPonderLocalization(CDPAetherPonderPlugin::new);
    }

    @Mod.EventBusSubscriber(modid = CDPCommon.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Client {
        @SubscribeEvent
        public static void setup(final FMLClientSetupEvent event) {
            if (ModIntegration.AETHER.enabled())
                PonderIndex.addPlugin(new CDPAetherPonderPlugin());
        }
    }
}
