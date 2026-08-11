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

package plus.dragons.createdragonsplus.integration.ars_nouveau;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.integration.ModIntegration;

@Mod.EventBusSubscriber(modid = CDPCommon.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ArsNouveauExtension {
    @SubscribeEvent
    public static void construct(final FMLConstructModEvent event) {
        if (ModIntegration.ARS_NOUVEAU.enabled())
            MinecraftForge.EVENT_BUS.register(ForgeEvents.class);
    }

    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event) {
        if (ModIntegration.ARS_NOUVEAU.enabled())
            event.enqueueWork(DragonBreathMobJarBehavior::register);
    }

    public static class ForgeEvents {
        @SubscribeEvent
        public static void attachCapabilities(final AttachCapabilitiesEvent<BlockEntity> event) {
            if (ModIntegration.STARBUNCLEMANIA.enabled())
                return;
            var provider = DragonBreathMobJarCapabilityProvider.create(event.getObject());
            if (provider == null)
                return;
            event.addCapability(CDPCommon.asResource("dragon_breath_mob_jar"), provider);
            event.addListener(provider::invalidate);
        }
    }
}
