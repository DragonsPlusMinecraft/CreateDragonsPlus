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

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateDataMapProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import plus.dragons.createdragonsplus.common.CDPCommon;

import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;


public class CDPSEDataMaps {
    public static final DataMapType<Block, Block> BLOCK_INTERACTION_BLASTING = DataMapType
            .builder(CDPCommon.asResource("air_current_block_interaction/blasting"), Registries.BLOCK, BuiltInRegistries.BLOCK.byNameCodec())
            .synced(BuiltInRegistries.BLOCK.byNameCodec(), true)
            .build();
    public static final DataMapType<Block, Block> BLOCK_INTERACTION_SMOKING = DataMapType
            .builder(CDPCommon.asResource("air_current_block_interaction/smoking"), Registries.BLOCK, BuiltInRegistries.BLOCK.byNameCodec())
            .synced(BuiltInRegistries.BLOCK.byNameCodec(), true)
            .build();
    public static final DataMapType<Block, Block> BLOCK_INTERACTION_SPLASHING = DataMapType
            .builder(CDPCommon.asResource("air_current_block_interaction/splashing"), Registries.BLOCK, BuiltInRegistries.BLOCK.byNameCodec())
            .synced(BuiltInRegistries.BLOCK.byNameCodec(), true)
            .build();
    public static final DataMapType<Block, Block> BLOCK_INTERACTION_HAUNTING = DataMapType
            .builder(CDPCommon.asResource("air_current_block_interaction/haunting"), Registries.BLOCK, BuiltInRegistries.BLOCK.byNameCodec())
            .synced(BuiltInRegistries.BLOCK.byNameCodec(), true)
            .build();
    public static final DataMapType<Block, Block> BLOCK_INTERACTION_FREEZING = DataMapType
            .builder(CDPCommon.asResource("air_current_block_interaction/freezing"), Registries.BLOCK, BuiltInRegistries.BLOCK.byNameCodec())
            .synced(BuiltInRegistries.BLOCK.byNameCodec(), true)
            .build();
    public static final DataMapType<Block, Block> BLOCK_INTERACTION_SANDING = DataMapType
            .builder(CDPCommon.asResource("air_current_block_interaction/sanding"), Registries.BLOCK, BuiltInRegistries.BLOCK.byNameCodec())
            .synced(BuiltInRegistries.BLOCK.byNameCodec(), true)
            .build();
    public static final DataMapType<Block, Block> BLOCK_INTERACTION_ENDING = DataMapType
            .builder(CDPCommon.asResource("air_current_block_interaction/ending"), Registries.BLOCK, BuiltInRegistries.BLOCK.byNameCodec())
            .synced(BuiltInRegistries.BLOCK.byNameCodec(), true)
            .build();

    public static void register(IEventBus modBus) {
        modBus.addListener(RegisterDataMapTypesEvent.class, CDPSEDataMaps::register);
        REGISTRATE.addDataGenerator(ProviderType.DATA_MAP, CDPSEDataMaps::generate);
    }

    public static void register(final RegisterDataMapTypesEvent event) {
        event.register(BLOCK_INTERACTION_BLASTING);
        event.register(BLOCK_INTERACTION_SMOKING);
        event.register(BLOCK_INTERACTION_SPLASHING);
        event.register(BLOCK_INTERACTION_HAUNTING);
        event.register(BLOCK_INTERACTION_FREEZING);
        event.register(BLOCK_INTERACTION_SANDING);
        event.register(BLOCK_INTERACTION_ENDING);
    }

    public static void generate(RegistrateDataMapProvider provider) {
        provider.builder(BLOCK_INTERACTION_BLASTING)
                .add(Blocks.ICE.builtInRegistryHolder(), Blocks.WATER, false)
                .add(Blocks.SNOW.defaultBlockState().getBlockHolder(), Blocks.AIR, false)
                .add(Blocks.SNOW_BLOCK.defaultBlockState().getBlockHolder(), Blocks.AIR, false)
                .add(Blocks.POWDER_SNOW.defaultBlockState().getBlockHolder(), Blocks.AIR, false);
        provider.builder(BLOCK_INTERACTION_SMOKING)
                .add(Blocks.SNOW.defaultBlockState().getBlockHolder(), Blocks.AIR, false);
        provider.builder(BLOCK_INTERACTION_FREEZING)
                .add(Blocks.WATER.defaultBlockState().getBlockHolder(), Blocks.ICE, false);
    }
}
