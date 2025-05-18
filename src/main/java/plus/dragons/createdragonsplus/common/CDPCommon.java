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

import com.simibubi.create.foundation.item.ItemDescription;
import java.util.concurrent.CompletableFuture;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack.Position;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import plus.dragons.createdragonsplus.common.registry.CDPBlockEntities;
import plus.dragons.createdragonsplus.common.registry.CDPBlockFreezers;
import plus.dragons.createdragonsplus.common.registry.CDPBlocks;
import plus.dragons.createdragonsplus.common.registry.CDPConditions;
import plus.dragons.createdragonsplus.common.registry.CDPCreativeModeTabs;
import plus.dragons.createdragonsplus.common.registry.CDPCriterions;
import plus.dragons.createdragonsplus.common.registry.CDPDataMaps;
import plus.dragons.createdragonsplus.common.registry.CDPFanProcessingTypes;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.common.registry.CDPItemAttributes;
import plus.dragons.createdragonsplus.common.registry.CDPItems;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.data.internal.CDPRuntimeRecipeProvider;
import plus.dragons.createdragonsplus.data.runtime.RuntimePackResources;
import plus.dragons.createdragonsplus.integration.ModIntegration;

@Mod(CDPCommon.ID)
public class CDPCommon {
    public static final String ID = "create_dragons_plus";
    public static final String NAME = "Create: Dragons Plus";
    public static final String PERSISTENT_DATA_KEY = "CreateDragonsPlusData";
    public static final CDPRegistrate REGISTRATE = new CDPRegistrate(ID)
            .setTooltipModifier(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE));
    private final ModContainer modContainer;
    private final Component runtimePackTitle = REGISTRATE
            .addLang("pack", asResource("runtime"), NAME);
    private final Component runtimePackDescription = REGISTRATE
            .addLang("pack", asResource("runtime"), "description", NAME + " Runtime Generated Resources");

    public CDPCommon(IEventBus modBus, ModContainer modContainer) {
        this.modContainer = modContainer;
        REGISTRATE.registerEventListeners(modBus);
        CDPFluids.register(modBus);
        CDPBlocks.register(modBus);
        CDPBlockEntities.register(modBus);
        CDPItems.register(modBus);
        CDPCreativeModeTabs.register(modBus);
        CDPCriterions.register(modBus);
        CDPRecipes.register(modBus);
        CDPConditions.register(modBus);
        CDPFanProcessingTypes.register(modBus);
        CDPItemAttributes.register();
        CDPDataMaps.register(modBus);
        modBus.register(this);
        modBus.register(new CDPConfig(modContainer));
    }

    @SubscribeEvent
    public void construct(final FMLConstructModEvent event) {
        for (ModIntegration integration : ModIntegration.values()) {
            if (integration.enabled())
                event.enqueueWork(integration::onConstructMod);
        }
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CDPBlockFreezers::register);
        for (ModIntegration integration : ModIntegration.values()) {
            if (integration.enabled())
                event.enqueueWork(integration::onCommonSetup);
        }
    }

    @SubscribeEvent
    public void addPackFinders(final AddPackFindersEvent event) {
        var type = event.getPackType();
        if (type == PackType.SERVER_DATA) {
            var pack = new RuntimePackResources("runtime", modContainer, type, Position.TOP, runtimePackTitle, runtimePackDescription);
            var registries = CompletableFuture.<HolderLookup.Provider>completedFuture(RegistryLayer.createRegistryAccess().compositeAccess());
            pack.addDataProvider(new CDPRuntimeRecipeProvider(pack.getPackOutput(), registries));
            event.addRepositorySource(pack);
        }
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}
