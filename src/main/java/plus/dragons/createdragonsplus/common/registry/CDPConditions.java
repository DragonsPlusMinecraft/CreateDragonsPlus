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

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.config.FeaturesConfig.ConfigFeature;

public class CDPConditions {
    private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
            DeferredRegister.create(NeoForgeRegistries.CONDITION_SERIALIZERS, CDPCommon.ID);

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<ConfigFeature>> CONFIG_FEATURE =
            CONDITION_CODECS.register("config_feature", () -> ConfigFeature.CODEC);

    public static void register(IEventBus modBus) {
        CONDITION_CODECS.register(modBus);
    }
}
