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

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.api.registry.registrate.SimpleBuilder;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.VirtualFluidBuilder;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockEntityBuilder.BlockEntityFactory;
import com.tterrag.registrate.builders.Builder;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider.IntrinsicImpl;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.dragons.createdragonsplus.data.lang.ForeignLanguageProvider;
import plus.dragons.createdragonsplus.data.tag.IntrinsicTagRegistry;
import plus.dragons.createdragonsplus.data.tag.ItemTagRegistry;
import plus.dragons.createdragonsplus.data.tag.TagRegistry;
import plus.dragons.createdragonsplus.mixin.accessor.ExistingFileHelperAccessor;
import plus.dragons.createdragonsplus.util.CodeReference;

@CodeReference(value = CreateRegistrate.class, source = "create", license = "mit")
public class CDPRegistrate extends AbstractRegistrate<CDPRegistrate> {
    protected final Logger logger;
    protected final Map<Holder<?>, Holder<CreativeModeTab>> creativeModeTabLookup = new HashMap<>();
    protected @Nullable Holder<CreativeModeTab> creativeModeTab;
    protected @Nullable Function<Item, TooltipModifier> tooltipModifier;
    protected @Nullable ExistingFileHelper existingFileHelper;
    protected @Nullable String templateLocale;

    public CDPRegistrate(String modid) {
        super(modid);
        this.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
        this.logger = LoggerFactory.getLogger(this.getClass().getSimpleName() + "[" + modid + "]");
    }

    public final ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(getModid(), path);
    }

    public boolean isInCreativeModeTab(Holder<?> holder) {
        return this.creativeModeTabLookup.containsKey(holder);
    }

    @Nullable
    public Holder<CreativeModeTab> getCreativeModeTab(Holder<?> holder) {
        return this.creativeModeTabLookup.get(holder);
    }

    public CDPRegistrate setCreativeModeTab(@Nullable Holder<CreativeModeTab> creativeModeTab) {
        this.creativeModeTab = creativeModeTab;
        return this;
    }

    public CDPRegistrate setTooltipModifier(@Nullable Function<Item, TooltipModifier> tooltipModifier) {
        this.tooltipModifier = tooltipModifier;
        return this;
    }

    @Override
    protected <R, T extends R> RegistryEntry<R, T> accept(String name, ResourceKey<? extends Registry<R>> type, Builder<R, T, ?, ?> builder, NonNullSupplier<? extends T> creator, NonNullFunction<DeferredHolder<R, T>, ? extends RegistryEntry<R, T>> entryFactory) {
        RegistryEntry<R, T> entry = super.accept(name, type, builder, creator, entryFactory);
        if (type.equals(Registries.ITEM) && this.tooltipModifier != null) {
            Function<Item, TooltipModifier> tooltipModifier = this.tooltipModifier;
            this.addRegisterCallback(name, Registries.ITEM, item -> {
                TooltipModifier modifier = tooltipModifier.apply(item);
                TooltipModifier.REGISTRY.register(item, modifier);
            });
        }
        if (this.creativeModeTab != null) {
            this.creativeModeTabLookup.put(entry, this.creativeModeTab);
        }
        return entry;
    }

    /* Datagen */

    public <T, P extends RegistrateTagsProvider<T>> CDPRegistrate registerTags(ProviderType<P> type, TagRegistry<T, P> registry) {
        this.addDataGenerator(type, registry::generate);
        return this;
    }

    public CDPRegistrate registerEnchantmentTags(TagRegistry<Enchantment, RegistrateTagsProvider<Enchantment>> registry) {
        this.addDataGenerator(ProviderType.ENCHANTMENT_TAGS, registry::generate);
        return this;
    }

    public CDPRegistrate registerBlockTags(IntrinsicTagRegistry<Block, IntrinsicImpl<Block>> registry) {
        this.addDataGenerator(ProviderType.BLOCK_TAGS, registry::generate);
        this.addDataGenerator(ProviderType.LANG, registry::generate);
        return this;
    }

    public CDPRegistrate registerItemTags(ItemTagRegistry registry) {
        this.addDataGenerator(ProviderType.ITEM_TAGS, registry::generate);
        this.addDataGenerator(ProviderType.LANG, registry::generate);
        return this;
    }

    public CDPRegistrate registerFluidTags(IntrinsicTagRegistry<Fluid, IntrinsicImpl<Fluid>> registry) {
        this.addDataGenerator(ProviderType.FLUID_TAGS, registry::generate);
        this.addDataGenerator(ProviderType.LANG, registry::generate);
        return this;
    }

    public CDPRegistrate registerEntityTags(IntrinsicTagRegistry<EntityType<?>, IntrinsicImpl<EntityType<?>>> registry) {
        this.addDataGenerator(ProviderType.ENTITY_TAGS, registry::generate);
        this.addDataGenerator(ProviderType.LANG, registry::generate);
        return this;
    }

    public CDPRegistrate registerPonderLocalization(Supplier<PonderPlugin> plugin) {
        if (FMLLoader.getDist() == Dist.CLIENT) {
            PonderIndex.addPlugin(plugin.get());
            this.addDataGenerator(ProviderType.LANG, prov -> PonderIndex
                    .getLangAccess()
                    .provideLang(getModid(), prov::add));
        }
        return this;
    }

    public CDPRegistrate registerForeignLocalization(String templateLocale) {
        this.templateLocale = templateLocale;
        return this;
    }

    public CDPRegistrate registerForeignLocalization() {
        return this.registerForeignLocalization("en_us");
    }

    public CDPRegistrate registerBuiltinLocalization(String name) {
        this.addDataGenerator(ProviderType.LANG, provider -> this.generateBuiltinLocalization(name, provider));
        return this;
    }

    protected void generateBuiltinLocalization(String name, RegistrateLangProvider provider) {
        ResourceLocation location = asResource("lang/builtin/" + name + ".json");
        Resource resource = this.getExistingResource(PackType.CLIENT_RESOURCES)
                .getResource(location)
                .orElseThrow(() -> new RuntimeException("Failed to find builtin localization: " + location));
        JsonObject json = getJsonFromResource(resource);
        for (Entry<String, JsonElement> entry : json.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            provider.add(key, value);
        }
    }

    protected ResourceManager getExistingResource(PackType type) {
        if (this.existingFileHelper == null) {
            throw new IllegalStateException("Can not get existing resource outside datagen");
        }
        return ((ExistingFileHelperAccessor) this.existingFileHelper).invokeGetManager(type);
    }

    protected static JsonObject getJsonFromResource(Resource resource) {
        try (InputStream inputStream = resource.open()) {
            return GsonHelper.parse(new InputStreamReader(inputStream));
        } catch (IOException exception) {
            throw new JsonIOException(exception);
        }
    }

    @Override
    protected void onData(GatherDataEvent event) {
        super.onData(event);
        boolean client = event.includeClient();
        boolean server = event.includeServer();
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        this.existingFileHelper = event.getExistingFileHelper();
        if (this.templateLocale != null)
            generator.addProvider(client, new ForeignLanguageProvider(getModid(), this.templateLocale, output, this.existingFileHelper));
    }

    /* Builders */

    @Override
    public <T extends BlockEntity> CreateBlockEntityBuilder<T, CDPRegistrate> blockEntity(String name, BlockEntityFactory<T> factory) {
        return blockEntity(self(), name, factory);
    }

    @Override
    public <T extends BlockEntity, P> CreateBlockEntityBuilder<T, P> blockEntity(P parent, String name, BlockEntityFactory<T> factory) {
        return (CreateBlockEntityBuilder<T, P>) entry(name, callback -> CreateBlockEntityBuilder.create(this, parent, name, callback, factory));
    }

    @Override
    public <T extends Entity> CreateEntityBuilder<T, CDPRegistrate> entity(String name, EntityType.EntityFactory<T> factory, MobCategory category) {
        return this.entity(self(), name, factory, category);
    }

    @Override
    public <T extends Entity, P> CreateEntityBuilder<T, P> entity(P parent, String name, EntityType.EntityFactory<T> factory, MobCategory category) {
        return (CreateEntityBuilder<T, P>) this.entry(name, callback -> CreateEntityBuilder.create(this, parent, name, callback, factory, category));
    }

    public FluidType defaultFluidType(FluidType.Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        return new FluidType(properties) {
            @Override
            @SuppressWarnings("removal")
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public ResourceLocation getStillTexture() {
                        return stillTexture;
                    }

                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return flowingTexture;
                    }
                });
            }
        };
    }

    public <T extends BaseFlowingFluid> FluidBuilder<T, CDPRegistrate> virtualFluid(
            String name,
            FluidBuilder.FluidTypeFactory type,
            NonNullFunction<BaseFlowingFluid.Properties, T> source,
            NonNullFunction<BaseFlowingFluid.Properties, T> flowingFactory) {
        return entry(name, callback -> new VirtualFluidBuilder<>(self(), self(), name, callback,
                asResource("fluid/" + name + "_still"),
                asResource("fluid/" + name + "_flow"),
                type, source, flowingFactory));
    }

    public <T extends BaseFlowingFluid> FluidBuilder<T, CDPRegistrate> virtualFluid(
            String name,
            ResourceLocation stillTexture,
            ResourceLocation flowTexture,
            FluidBuilder.FluidTypeFactory typeFactory,
            NonNullFunction<BaseFlowingFluid.Properties, T> sourceFactory,
            NonNullFunction<BaseFlowingFluid.Properties, T> flowingFactory) {
        return entry(name, callback -> new VirtualFluidBuilder<>(self(), self(), name, callback,
                stillTexture, flowTexture, typeFactory, sourceFactory, flowingFactory));
    }

    public FluidBuilder<VirtualFluid, CDPRegistrate> virtualFluid(String name) {
        return entry(name, callback -> new VirtualFluidBuilder<>(self(), self(), name, callback,
                asResource("fluid/" + name + "_still"),
                asResource("fluid/" + name + "_flow"),
                this::defaultFluidType, VirtualFluid::createSource, VirtualFluid::createFlowing));
    }

    public FluidBuilder<VirtualFluid, CDPRegistrate> virtualFluid(String name, ResourceLocation stillTexture, ResourceLocation flowTexture) {
        return entry(name, callback -> new VirtualFluidBuilder<>(self(), self(), name, callback,
                stillTexture, flowTexture,
                this::defaultFluidType, VirtualFluid::createSource, VirtualFluid::createFlowing));
    }

    @Override
    public FluidBuilder<BaseFlowingFluid.Flowing, CDPRegistrate> fluid(String name) {
        return fluid(name,
                asResource("fluid/" + name + "_still"),
                asResource("fluid/" + name + "_flow"));
    }

    @Override
    public FluidBuilder<BaseFlowingFluid.Flowing, CDPRegistrate> fluid(String name, FluidBuilder.FluidTypeFactory typeFactory) {
        return fluid(name,
                asResource("fluid/" + name + "_still"),
                asResource("fluid/" + name + "_flow"),
                typeFactory);
    }

    public <T extends MountedItemStorageType<?>> SimpleBuilder<MountedItemStorageType<?>, T, CDPRegistrate> mountedItemStorage(String name, Supplier<T> supplier) {
        return this.entry(name, callback -> new SimpleBuilder<>(this, this, name, callback,
                CreateRegistries.MOUNTED_ITEM_STORAGE_TYPE, supplier).byBlock(MountedItemStorageType.REGISTRY));
    }

    public <T extends MountedFluidStorageType<?>> SimpleBuilder<MountedFluidStorageType<?>, T, CDPRegistrate> mountedFluidStorage(String name, Supplier<T> supplier) {
        return this.entry(name, callback -> new SimpleBuilder<>(this, this, name, callback,
                CreateRegistries.MOUNTED_FLUID_STORAGE_TYPE, supplier).byBlock(MountedFluidStorageType.REGISTRY));
    }

    public <T extends DisplaySource> SimpleBuilder<DisplaySource, T, CDPRegistrate> displaySource(String name, Supplier<T> supplier) {
        return this.entry(name, callback -> new SimpleBuilder<>(this, this, name, callback,
                CreateRegistries.DISPLAY_SOURCE, supplier).byBlock(DisplaySource.BY_BLOCK).byBlockEntity(DisplaySource.BY_BLOCK_ENTITY));
    }

    public <T extends DisplayTarget> SimpleBuilder<DisplayTarget, T, CDPRegistrate> displayTarget(String name, Supplier<T> supplier) {
        return this.entry(name, callback -> new SimpleBuilder<>(this, this, name, callback,
                CreateRegistries.DISPLAY_TARGET, supplier).byBlock(DisplayTarget.BY_BLOCK).byBlockEntity(DisplayTarget.BY_BLOCK_ENTITY));
    }
}
