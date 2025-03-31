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

import com.mojang.serialization.Codec;
import com.simibubi.create.api.stress.BlockStressValues;
import java.util.function.DoubleSupplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.jetbrains.annotations.Nullable;
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
    public static final DataMapType<Block, Double> BLOCK_STRESS_IMPACTS = DataMapType
            .builder(CDPCommon.asResource("stress/impacts"), Registries.BLOCK, Codec.doubleRange(0, Double.MAX_VALUE))
            .synced(Codec.DOUBLE, true)
            .build();
    public static final DataMapType<Block, Double> BLOCK_STRESS_CAPACITIES = DataMapType
            .builder(CDPCommon.asResource("stress/capacities"), Registries.BLOCK, Codec.doubleRange(0, Double.MAX_VALUE))
            .synced(Codec.DOUBLE, true)
            .build();

    public static void register(IEventBus modBus) {
        modBus.addListener(RegisterDataMapTypesEvent.class, CDPDataMaps::register);
        NeoForge.EVENT_BUS.addListener(DataMapsUpdatedEvent.class, CDPDataMaps::onUpdated);
    }

    public static void register(final RegisterDataMapTypesEvent event) {
        event.register(FLUID_FAN_COLORING_CATALYSTS);
        event.register(BLOCK_FAN_COLORING_CATALYSTS);
        event.register(BLOCK_STRESS_IMPACTS);
        event.register(BLOCK_STRESS_CAPACITIES);
        BlockStressValues.IMPACTS.registerProvider(CDPDataMaps::getStressImpact);
        BlockStressValues.CAPACITIES.registerProvider(CDPDataMaps::getStressCapacity);
    }

    public static void onUpdated(final DataMapsUpdatedEvent event) {
        BlockStressValues.IMPACTS.invalidate();
        BlockStressValues.CAPACITIES.invalidate();
    }

    private static @Nullable DoubleSupplier getStressImpact(Block block) {
        var holder = BuiltInRegistries.BLOCK.wrapAsHolder(block);
        Double impact = holder.getData(BLOCK_STRESS_IMPACTS);
        return impact == null ? null : impact::doubleValue;
    }

    private static @Nullable DoubleSupplier getStressCapacity(Block block) {
        var holder = BuiltInRegistries.BLOCK.wrapAsHolder(block);
        Double capacity = holder.getData(BLOCK_STRESS_CAPACITIES);
        return capacity == null ? null : capacity::doubleValue;
    }
}
