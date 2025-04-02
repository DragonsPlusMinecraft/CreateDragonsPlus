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

package plus.dragons.createdragonsplus.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import plus.dragons.createdragonsplus.common.CDPCommon;

public class CDPDataMaps {
    public static final DataMapType<Fluid, DyeColor> FLUID_FAN_COLORING_CATALYSTS = DataMapType
            .builder(CDPCommon.asResource("fan_processing_catalysts/coloring"), Registries.FLUID, DyeColor.CODEC)
            .synced(DyeColor.CODEC, true)
            .build();
    public static final DataMapType<Block, DyeColor> BLOCK_FAN_COLORING_CATALYSTS = DataMapType
            .builder(CDPCommon.asResource("fan_processing_catalysts/coloring"), Registries.BLOCK, DyeColor.CODEC)
            .synced(DyeColor.CODEC, true)
            .build();

    public static void register(IEventBus modBus) {
        modBus.addListener(RegisterDataMapTypesEvent.class, CDPDataMaps::register);
    }

    public static void register(final RegisterDataMapTypesEvent event) {
        event.register(FLUID_FAN_COLORING_CATALYSTS);
        event.register(BLOCK_FAN_COLORING_CATALYSTS);
    }
}
