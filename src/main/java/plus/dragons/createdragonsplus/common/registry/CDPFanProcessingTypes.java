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
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariantRegistry;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.ColoringFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.ending.EndingFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.sanding.SandingFanProcessingType;

public class CDPFanProcessingTypes {
    public static final Map<ResourceLocation, Supplier<ColoringFanProcessingType>> COLORING = Util.make(() -> {
        ImmutableMap.Builder<ResourceLocation, Supplier<ColoringFanProcessingType>> builder = ImmutableMap.builder();
        for (var variant : DyeVariantRegistry.all())
            builder.put(variant.id(), register(variant.fanProcessingName(), () -> new ColoringFanProcessingType(variant)));
        return builder.build();
    });
    public static final Supplier<FreezingFanProcessingType> FREEZING = register("freezing", FreezingFanProcessingType::new);
    public static final Supplier<SandingFanProcessingType> SANDING = register("sanding", SandingFanProcessingType::new);
    public static final Supplier<EndingFanProcessingType> ENDING = register("ending", EndingFanProcessingType::new);

    private static <T extends FanProcessingType> Supplier<T> register(String name, Supplier<T> factory) {
        T type = Registry.register(CreateBuiltInRegistries.FAN_PROCESSING_TYPE, CDPCommon.asResource(name), factory.get());
        return () -> type;
    }

    public static void register(IEventBus modBus) {}
}
