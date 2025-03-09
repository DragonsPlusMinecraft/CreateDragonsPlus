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

package plus.dragons.createdragonsplus.client;

import net.minecraft.util.FastColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;

@Mod(CDPCommon.ID)
public class CDPClient {
    public CDPClient(IEventBus modBus) {
        modBus.register(this);
    }

    @SubscribeEvent
    public void setup(final FMLClientSetupEvent event) {}

    @SubscribeEvent
    public void registerItemColors(final RegisterColorHandlersEvent.Item event) {
        CDPFluids.DYES_BY_COLOR.forEach((color, entry) -> {
            var tintColor = FastColor.ARGB32.opaque(color.getTextureDiffuseColor());
            var bucket = entry.getBucket().orElseThrow();
            event.register((stack, tintIndex) -> tintIndex > 0 ? -1 : tintColor, bucket);
        });
    }
}
