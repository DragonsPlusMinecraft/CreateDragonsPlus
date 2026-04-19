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
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
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
    private static final ResourceManagerReloadListener RELOAD_LISTENER = resourceManager -> CDPFanProcessingTypes.COLORING.values().forEach(t -> t.get().recreateCache());

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
        CDPItemAttributes.register(modBus);
        CDPDataMaps.register(modBus);
        modBus.register(this);
        modBus.register(new CDPConfig(modContainer));
        NeoForge.EVENT_BUS.addListener(CDPCommon::addReloadListeners);
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CDPBlockFreezers::register);
    }

    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(RELOAD_LISTENER);
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
