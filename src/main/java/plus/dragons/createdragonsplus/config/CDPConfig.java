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

package plus.dragons.createdragonsplus.config;

import net.minecraft.Util;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import plus.dragons.createdragonsplus.common.CDPCommon;

@Mod(CDPCommon.ID)
public class CDPConfig {
    private static ModConfigSpec COMMON_SPEC;
    private static CDPCommonConfig COMMON_CONFIG;
    private static ModConfigSpec CLIENT_SPEC;
    private static CDPClientConfig CLIENT_CONFIG;

    public CDPConfig(IEventBus modBus, ModContainer container) {
        COMMON_SPEC = Util.make(new ModConfigSpec.Builder().configure(builder -> {
            COMMON_CONFIG = new CDPCommonConfig();
            COMMON_CONFIG.registerAll(builder);
            return Unit.INSTANCE;
        }).getValue(), spec -> container.registerConfig(Type.COMMON, spec));
        CLIENT_SPEC = Util.make(new ModConfigSpec.Builder().configure(builder -> {
            CLIENT_CONFIG = new CDPClientConfig();
            CLIENT_CONFIG.registerAll(builder);
            return Unit.INSTANCE;
        }).getValue(), spec -> container.registerConfig(Type.CLIENT, spec));
        modBus.register(this);
    }

    public static CDPCommonConfig common() {
        return COMMON_CONFIG;
    }

    public static CDPClientConfig client() {
        return CLIENT_CONFIG;
    }

    public static CDPFeaturesConfig features() {
        return COMMON_CONFIG.features;
    }

    @SubscribeEvent
    public void onLoad(ModConfigEvent.Loading event) {
        var spec = event.getConfig().getSpec();
        if (spec == COMMON_SPEC)
            COMMON_CONFIG.onLoad();
        else if (spec == CLIENT_SPEC)
            CLIENT_CONFIG.onLoad();
    }

    @SubscribeEvent
    public void onReload(ModConfigEvent.Reloading event) {
        var spec = event.getConfig().getSpec();
        if (spec == COMMON_SPEC)
            COMMON_CONFIG.onReload();
        else if (spec == CLIENT_SPEC)
            CLIENT_CONFIG.onReload();
    }
}
