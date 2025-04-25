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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CDPConfig {
    private static final CDPCommonConfig COMMON_CONFIG = new CDPCommonConfig();
    private static final CDPClientConfig CLIENT_CONFIG = new CDPClientConfig();
    private static final CDPServerConfig SERVER_CONFIG = new CDPServerConfig();
    private static ModConfigSpec COMMON_SPEC;
    private static ModConfigSpec CLIENT_SPEC;
    private static ModConfigSpec SERVER_SPEC;

    public CDPConfig(ModContainer container) {
        COMMON_SPEC = Util.make(new ModConfigSpec.Builder().configure(builder -> {
            COMMON_CONFIG.registerAll(builder);
            return Unit.INSTANCE;
        }).getValue(), spec -> container.registerConfig(Type.COMMON, spec));
        CLIENT_SPEC = Util.make(new ModConfigSpec.Builder().configure(builder -> {
            CLIENT_CONFIG.registerAll(builder);
            return Unit.INSTANCE;
        }).getValue(), spec -> container.registerConfig(Type.CLIENT, spec));
        SERVER_SPEC = Util.make(new ModConfigSpec.Builder().configure(builder -> {
            SERVER_CONFIG.registerAll(builder);
            return Unit.INSTANCE;
        }).getValue(), spec -> container.registerConfig(Type.SERVER, spec));
    }

    public static CDPCommonConfig common() {
        return COMMON_CONFIG;
    }

    public static CDPClientConfig client() {
        return CLIENT_CONFIG;
    }

    public static CDPServerConfig server() {
        return SERVER_CONFIG;
    }

    public static CDPFeaturesConfig features() {
        return COMMON_CONFIG.features;
    }

    public static CDPRecipesConfig recipes() {
        return SERVER_CONFIG.recipes;
    }

    @SubscribeEvent
    public void onLoad(ModConfigEvent.Loading event) {
        var spec = event.getConfig().getSpec();
        if (spec == COMMON_SPEC)
            COMMON_CONFIG.onLoad();
        else if (spec == CLIENT_SPEC)
            CLIENT_CONFIG.onLoad();
        else if (spec == SERVER_SPEC)
            SERVER_CONFIG.onLoad();
    }

    @SubscribeEvent
    public void onReload(ModConfigEvent.Reloading event) {
        var spec = event.getConfig().getSpec();
        if (spec == COMMON_SPEC)
            COMMON_CONFIG.onReload();
        else if (spec == CLIENT_SPEC)
            CLIENT_CONFIG.onReload();
        else if (spec == SERVER_SPEC)
            SERVER_CONFIG.onReload();
    }
}
