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

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.integration.ModIntegration;

@Mod(CDPCommon.ID)
public class ArsNouveauExtension {
    public ArsNouveauExtension(IEventBus modBus) {
        if (!ModIntegration.ARS_NOUVEAU.enabled())
            return;
        modBus.addListener(this::setup);
        if (!ModIntegration.STARBUNCLEMANIA.enabled())
            modBus.addListener(this::registerCapabilities);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(DragonBreathMobJarBehavior::register);
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BlockRegistry.MOB_JAR_TILE.get(),
                (tile, side) -> new DragonBreathMobJarFluidHandler(tile));
    }
}
