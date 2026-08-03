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

package plus.dragons.createdragonsplus.data;

import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import plus.dragons.createdragonsplus.client.ponder.CDPPonderPlugin;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.data.internal.CDPRecipeProvider;

@Mod.EventBusSubscriber(modid = CDPCommon.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CDPData {
    static {
        if (DatagenModLoader.isRunningDataGen())
            bootstrap();
    }

    private static void bootstrap() {
        REGISTRATE.registerBuiltinLocalization("interface")
                .registerBuiltinLocalization("tooltips")
                .registerPonderLocalization(CDPPonderPlugin::new)
                .registerForeignLocalization();
    }

    @SubscribeEvent
    public static void generate(final GatherDataEvent event) {
        var client = event.includeClient();
        var server = event.includeServer();
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var existingFileHelper = event.getExistingFileHelper();
        var registries = event.getLookupProvider();
        generator.addProvider(server, new CDPRecipeProvider(output));
    }
}
