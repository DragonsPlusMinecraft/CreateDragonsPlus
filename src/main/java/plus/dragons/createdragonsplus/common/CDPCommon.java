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

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.foundation.item.ItemDescription;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeColors;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariantRegistry;
import plus.dragons.createdragonsplus.common.fluids.dye.RegisterDyeVariantsEvent;
import plus.dragons.createdragonsplus.common.recipe.RecipeConverter;
import plus.dragons.createdragonsplus.common.registry.CDPBlockEntities;
import plus.dragons.createdragonsplus.common.registry.CDPBlockFreezers;
import plus.dragons.createdragonsplus.common.registry.CDPBlocks;
import plus.dragons.createdragonsplus.common.registry.CDPCauldrons;
import plus.dragons.createdragonsplus.common.registry.CDPConditions;
import plus.dragons.createdragonsplus.common.registry.CDPCreativeModeTabs;
import plus.dragons.createdragonsplus.common.registry.CDPCriterions;
import plus.dragons.createdragonsplus.common.registry.CDPFanProcessingTypes;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.common.registry.CDPItemAttributes;
import plus.dragons.createdragonsplus.common.registry.CDPItems;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.data.internal.CDPRuntimeRecipeProvider;
import plus.dragons.createdragonsplus.data.runtime.RuntimePackResources;
import plus.dragons.createdragonsplus.integration.CDPCompatFix;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions;

@Mod(CDPCommon.ID)
public class CDPCommon {
    public static final String ID = "create_dragons_plus";
    public static final String NAME = "Create: Dragons Plus";
    public static final String PERSISTENT_DATA_KEY = "CreateDragonsPlusData";
    public static final CDPRegistrate REGISTRATE = new CDPRegistrate(ID)
            .setTooltipModifier(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE));
    private final ModContainer modContainer;
    private final IEventBus modBus;
    private final Component runtimePackTitle = REGISTRATE
            .addLang("pack", asResource("runtime"), NAME);
    private final Component runtimePackDescription = REGISTRATE
            .addLang("pack", asResource("runtime"), "description", NAME + " Runtime Generated Resources");
    private static final ResourceManagerReloadListener RELOAD_LISTENER = resourceManager -> {
        CDPFanProcessingTypes.COLORING.values().forEach(t -> t.get().recreateCache());
        CDPItemAttributes.recreateCache();
        RecipeConverter.invalidateCaches();
    };

    public CDPCommon() {
        this.modContainer = ModLoadingContext.get().getActiveContainer();
        this.modBus = FMLJavaModLoadingContext.get().getModEventBus();
        REGISTRATE.registerEventListeners(modBus);
        modBus.register(this);
        modBus.register(new CDPConfig());
        MinecraftForge.EVENT_BUS.addListener(CDPCommon::addReloadListeners);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void construct(final FMLConstructModEvent event) {
        bootstrapDyeVariants();
        CDPCauldrons.register(modBus);
        CDPFluids.register(modBus);
        CDPBlocks.register(modBus);
        CDPBlockEntities.register(modBus);
        CDPItems.register(modBus);
        CDPCreativeModeTabs.register(modBus);
        CDPCriterions.register(modBus);
        CDPRecipes.register(modBus);
        CDPConditions.register(modBus);
        registerCreateOwnedTypes();
    }

    private void bootstrapDyeVariants() {
        if (DyeVariantRegistry.isFrozen())
            return;
        var builder = new DyeVariantRegistry.Builder();
        DyeColors.registerVanilla(builder);
        CDPIntegrationContributions.gatherDyeVariants(new RegisterDyeVariantsEvent(builder));
        DyeVariantRegistry.freeze(builder.build());
    }

    private void registerCreateOwnedTypes() {
        populateFrozenRegistry(CreateBuiltInRegistries.FAN_PROCESSING_TYPE,
                () -> {
                    CDPFanProcessingTypes.register(modBus);
                    CDPIntegrationContributions.gatherFanProcessingTypes();
                });
        populateFrozenRegistry(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE,
                () -> {
                    CDPItemAttributes.register(modBus);
                    CDPIntegrationContributions.gatherItemAttributes();
                });
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    private static <T> void populateFrozenRegistry(Registry<T> registry, Runnable registration) {
        if (!(registry instanceof MappedRegistry<?>))
            throw new IllegalStateException("Expected a mapped Create registry: " + registry.key());
        var mapped = (MappedRegistry<T>) registry;
        mapped.unfreeze();
        try {
            registration.run();
        } finally {
            mapped.freeze();
        }
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CDPBlockFreezers::register);
        event.enqueueWork(CDPCompatFix::register);
    }

    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(RELOAD_LISTENER);
    }

    @SubscribeEvent
    public void addPackFinders(final AddPackFindersEvent event) {
        var type = event.getPackType();
        if (type == PackType.SERVER_DATA) {
            var pack = new RuntimePackResources("runtime", modContainer, type, Position.TOP);
            pack.addDataProvider(new CDPRuntimeRecipeProvider(pack.getPackOutput()));
            event.addRepositorySource(pack);
        }
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(ID, path);
    }
}
