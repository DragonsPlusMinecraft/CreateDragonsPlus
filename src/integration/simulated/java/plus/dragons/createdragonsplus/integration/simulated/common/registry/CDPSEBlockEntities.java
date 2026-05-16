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

package plus.dragons.createdragonsplus.integration.simulated.common.registry;

import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank.FragileFluidTankBlockEntity;
import plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank.FragileFluidTankRenderer;

public class CDPSEBlockEntities {
    public static final BlockEntityEntry<FragileFluidTankBlockEntity> FRAGILE_FLUID_TANK = REGISTRATE
            .blockEntity("fragile_fluid_tank", FragileFluidTankBlockEntity::new)
            .renderer(() -> FragileFluidTankRenderer::new)
            .validBlocks(CDPSEBlocks.FRAGILE_FLUID_TANK, CDPSEBlocks.LEVITITE_FRAGILE_FLUID_TANK)
            .register();

    public static void register(IEventBus modBus) {
        modBus.register(CDPSEBlockEntities.class);
    }

    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                FRAGILE_FLUID_TANK.get(), FragileFluidTankBlockEntity::getFluidHandler);
    }
}
