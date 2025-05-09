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

import com.google.common.collect.ImmutableMap;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeColors;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.ColoringFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.ending.EndingFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.sanding.SandingFanProcessingType;

public class CDPFanProcessingTypes {
    private static final DeferredRegister<FanProcessingType> TYPES = DeferredRegister
            .create(CreateRegistries.FAN_PROCESSING_TYPE, CDPCommon.ID);
    public static final Map<DyeColor, Supplier<ColoringFanProcessingType>> COLORING = Util.make(() -> {
        var builder = ImmutableMap.<DyeColor, Supplier<ColoringFanProcessingType>>builder();
        for (var color : DyeColors.ALL) {
            // In case there are modded DyeColor
            var name = "coloring_" + ResourceLocation.parse(color.getName()).getPath();
            var type = TYPES.register(name, () -> new ColoringFanProcessingType(color));
            builder.put(color, type);
        }
        return builder.build();
    });
    public static final DeferredHolder<FanProcessingType, FreezingFanProcessingType> FREEZING = TYPES
            .register("freezing", FreezingFanProcessingType::new);
    public static final DeferredHolder<FanProcessingType, SandingFanProcessingType> SANDING = TYPES
            .register("sanding", SandingFanProcessingType::new);
    public static final DeferredHolder<FanProcessingType, EndingFanProcessingType> ENDING = TYPES
            .register("ending", EndingFanProcessingType::new);

    public static void register(IEventBus modBus) {
        TYPES.register(modBus);
    }
}
