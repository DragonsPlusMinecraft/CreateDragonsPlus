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
    public static final DataMapType<Block, Block> FRAGILE_FLUID_TANK_LAVA = DataMapType
            .builder(CDPCommon.asResource("fragile_fluid_tank/lava"), Registries.BLOCK, BuiltInRegistries.BLOCK.byNameCodec())
            .synced(BuiltInRegistries.BLOCK.byNameCodec(), true)
            .build();
    public static final DataMapType<Block, Block> FRAGILE_FLUID_TANK_WATER = DataMapType
            .builder(CDPCommon.asResource("fragile_fluid_tank/water"), Registries.BLOCK, BuiltInRegistries.BLOCK.byNameCodec())
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
        event.register(FRAGILE_FLUID_TANK_LAVA);
        event.register(FRAGILE_FLUID_TANK_WATER);
    }

    public static void generate(RegistrateDataMapProvider provider) {
        genLavaDataMap(provider, BLOCK_INTERACTION_BLASTING);
        genLavaDataMap(provider, FRAGILE_FLUID_TANK_LAVA);
        provider.builder(BLOCK_INTERACTION_SMOKING)
                .add(Blocks.SNOW.defaultBlockState().getBlockHolder(), Blocks.AIR, false);
        genWaterDataMap(provider, BLOCK_INTERACTION_SPLASHING);
        genWaterDataMap(provider, FRAGILE_FLUID_TANK_WATER);
        provider.builder(BLOCK_INTERACTION_FREEZING)
                .add(Blocks.WATER.defaultBlockState().getBlockHolder(), Blocks.ICE, false);
    }

    private static void genLavaDataMap(RegistrateDataMapProvider provider, DataMapType<Block, Block> lavaMap) {
        provider.builder(lavaMap)
                .add(Blocks.WET_SPONGE.builtInRegistryHolder(), Blocks.SPONGE, false)
                .add(Blocks.ICE.builtInRegistryHolder(), Blocks.WATER, false)
                .add(Blocks.SNOW.defaultBlockState().getBlockHolder(), Blocks.AIR, false)
                .add(Blocks.SNOW_BLOCK.defaultBlockState().getBlockHolder(), Blocks.AIR, false)
                .add(Blocks.POWDER_SNOW.defaultBlockState().getBlockHolder(), Blocks.AIR, false);
    }

    private static void genWaterDataMap(RegistrateDataMapProvider provider, DataMapType<Block, Block> waterMap) {
        provider.builder(waterMap)
                .add(Blocks.SPONGE.defaultBlockState().getBlockHolder(), Blocks.WET_SPONGE, false)
                .add(Blocks.WET_SPONGE.builtInRegistryHolder(), Blocks.SPONGE, false)
                .add(Blocks.WHITE_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.WHITE_CONCRETE, false)
                .add(Blocks.LIGHT_GRAY_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.LIGHT_GRAY_CONCRETE, false)
                .add(Blocks.GRAY_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.GRAY_CONCRETE, false)
                .add(Blocks.BLACK_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.BLACK_CONCRETE, false)
                .add(Blocks.BROWN_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.BROWN_CONCRETE, false)
                .add(Blocks.RED_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.RED_CONCRETE, false)
                .add(Blocks.YELLOW_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.YELLOW_CONCRETE, false)
                .add(Blocks.ORANGE_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.ORANGE_CONCRETE, false)
                .add(Blocks.LIME_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.LIME_CONCRETE, false)
                .add(Blocks.GREEN_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.GREEN_CONCRETE, false)
                .add(Blocks.CYAN_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.CYAN_CONCRETE, false)
                .add(Blocks.LIGHT_BLUE_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.LIGHT_BLUE_CONCRETE, false)
                .add(Blocks.BLUE_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.BLUE_CONCRETE, false)
                .add(Blocks.PURPLE_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.PURPLE_CONCRETE, false)
                .add(Blocks.MAGENTA_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.MAGENTA_CONCRETE, false)
                .add(Blocks.PINK_CONCRETE_POWDER.builtInRegistryHolder(), Blocks.PINK_CONCRETE, false)
                .add(Blocks.LIGHT_GRAY_WOOL.builtInRegistryHolder(), Blocks.WHITE_WOOL, false)
                .add(Blocks.GRAY_WOOL.builtInRegistryHolder(), Blocks.WHITE_WOOL, false)
                .add(Blocks.BLACK_WOOL.builtInRegistryHolder(), Blocks.WHITE_WOOL, false)
                .add(Blocks.BROWN_WOOL.builtInRegistryHolder(), Blocks.WHITE_WOOL, false)
                .add(Blocks.RED_WOOL.builtInRegistryHolder(), Blocks.WHITE_WOOL, false)
                .add(Blocks.ORANGE_WOOL.builtInRegistryHolder(), Blocks.WHITE_WOOL, false)
                .add(Blocks.YELLOW_WOOL.builtInRegistryHolder(), Blocks.WHITE_WOOL, false)
                .add(Blocks.LIME_WOOL.builtInRegistryHolder(), Blocks.WHITE_WOOL, false)
                .add(Blocks.GREEN_WOOL.builtInRegistryHolder(), Blocks.WHITE_WOOL, false)
                .add(Blocks.CYAN_WOOL.builtInRegistryHolder(), Blocks.WHITE_WOOL, false)
                .add(Blocks.BLUE_WOOL.builtInRegistryHolder(), Blocks.WHITE_WOOL, false)
                .add(Blocks.PURPLE_WOOL.builtInRegistryHolder(), Blocks.WHITE_WOOL, false)
                .add(Blocks.MAGENTA_WOOL.builtInRegistryHolder(), Blocks.WHITE_WOOL, false)
                .add(Blocks.PINK_WOOL.builtInRegistryHolder(), Blocks.WHITE_WOOL, false)
                .add(Blocks.FIRE.builtInRegistryHolder(), Blocks.AIR, false);
    }
}
