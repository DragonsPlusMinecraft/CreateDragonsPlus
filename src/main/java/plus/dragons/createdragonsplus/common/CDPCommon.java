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

package plus.dragons.createdragonsplus.common;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import plus.dragons.createdragonsplus.common.registry.CDPConditions;
import plus.dragons.createdragonsplus.common.registry.CDPCriterions;
import plus.dragons.createdragonsplus.common.registry.CDPDataMaps;
import plus.dragons.createdragonsplus.common.registry.CDPFanProcessingTypes;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.common.registry.CDPItems;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.common.registry.CDPSounds;

@Mod(CDPCommon.ID)
public class CDPCommon {
    public static final String ID = "create_dragons_plus";
    public static final String PERSISTENT_DATA_KEY = "CreateDragonsPlusData";
    public static final CDPRegistrate REGISTRATE = new CDPRegistrate(ID);

    public CDPCommon(IEventBus modBus) {
        REGISTRATE.registerEventListeners(modBus);
        CDPConditions.register(modBus);
        CDPCriterions.register(modBus);
        CDPDataMaps.register(modBus);
        CDPFanProcessingTypes.register(modBus);
        CDPFluids.register(modBus);
        CDPItems.register(modBus);
        CDPRecipes.register(modBus);
        CDPSounds.register(modBus);
        modBus.register(this);
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event) {}

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}
