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

package plus.dragons.createdragonsplus.integration.aether.config;

import net.minecraft.Util;
import net.minecraft.util.Unit;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import plus.dragons.createdragonsplus.common.CDPCommon;

public class CDPAetherConfig {
    private static final CDPAetherServerConfig SERVER_CONFIG = new CDPAetherServerConfig();
    private static ForgeConfigSpec SERVER_SPEC;

    public CDPAetherConfig() {
        var context = ModLoadingContext.get();
        SERVER_SPEC = Util.make(new ForgeConfigSpec.Builder().configure(builder -> {
            SERVER_CONFIG.registerAll(builder);
            return Unit.INSTANCE;
        }).getValue(), spec -> context.registerConfig(Type.SERVER, spec, CDPCommon.ID + "-aether-integration-server.toml"));
    }

    public static CDPAetherServerConfig server() {
        return SERVER_CONFIG;
    }

    public static boolean bulkEnchantingEnabled() {
        return SERVER_CONFIG.enableBulkEnchanting.get();
    }

    public static boolean bulkMoaIncubationEnabled() {
        return SERVER_CONFIG.enableBulkMoaIncubation.get();
    }

    @SubscribeEvent
    public void onLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SERVER_SPEC)
            SERVER_CONFIG.onLoad();
    }

    @SubscribeEvent
    public void onReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SERVER_SPEC)
            SERVER_CONFIG.onReload();
    }
}
