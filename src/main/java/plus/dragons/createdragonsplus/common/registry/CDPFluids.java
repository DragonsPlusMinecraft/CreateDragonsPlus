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

package plus.dragons.createdragonsplus.common.registry;

import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;

import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import com.simibubi.create.api.event.PipeCollisionEvent;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider.IntrinsicImpl;
import com.tterrag.registrate.util.entry.FluidEntry;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry.InteractionInformation;
import net.neoforged.neoforge.fluids.FluidType;
import plus.dragons.createdragonsplus.client.color.SimpleItemColors;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.fluids.StandardDispenserBehaviour;
import plus.dragons.createdragonsplus.common.fluids.dragonBreath.DragonBreathFluidType;
import plus.dragons.createdragonsplus.common.fluids.dragonBreath.DragondBreathLiquidBlock;
import plus.dragons.createdragonsplus.common.fluids.dragonBreath.DragonsBreathOpenPipeEffect;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeFluidOpenPipeEffect;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeFluidType;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeLiquidBlock;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariant;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariantRegistry;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.data.recipe.CreateRecipeBuilders;
import plus.dragons.createdragonsplus.data.tag.IntrinsicTagRegistry;

public class CDPFluids {
    public static final ModTags MOD_TAGS = new ModTags();
    public static final CommonTags COMMON_TAGS = new CommonTags();
    public static final Map<ResourceLocation, FluidEntry<BaseFlowingFluid.Flowing>> DYES_BY_VARIANT = new LinkedHashMap<>();
    static {
        for (var variant : DyeVariantRegistry.all()) {
            DYES_BY_VARIANT.put(variant.id(), dye(variant));
        }
    }
    public static final FluidEntry<BaseFlowingFluid.Flowing> DRAGON_BREATH = REGISTRATE
            .fluid("dragon_breath",
                    REGISTRATE.asResource("fluid/dragon_breath_still"),
                    REGISTRATE.asResource("fluid/dragon_breath_flow"),
                    DragonBreathFluidType.create())
            .lang("Dragon's Breath")
            .properties(properties -> properties
                    .rarity(Rarity.UNCOMMON)
                    .density(3000)
                    .viscosity(6000)
                    .lightLevel(15)
                    .motionScale(0.07)
                    .canSwim(false)
                    .canDrown(false)
                    .pathType(PathType.DAMAGE_OTHER)
                    .adjacentPathType(null)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.DRAGON_FIREBALL_EXPLODE)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA))
            .fluidProperties(properties -> properties
                    .explosionResistance(100F)
                    .levelDecreasePerBlock(2)
                    .slopeFindDistance(2)
                    .tickRate(30))
            .source(BaseFlowingFluid.Source::new)
            .onRegister(flowing -> {
                BuiltInRegistries.FLUID.addAlias(
                        REGISTRATE.asResource("dragons_breath"),
                        REGISTRATE.asResource("dragon_breath"));
                BuiltInRegistries.FLUID.addAlias(
                        REGISTRATE.asResource("flowing_dragons_breath"),
                        REGISTRATE.asResource("flowing_dragon_breath"));
            })
            .tag(COMMON_TAGS.dragonBreath, MOD_TAGS.fanEndingCatalysts)
            .block(DragondBreathLiquidBlock::new)
            .lang("Dragon's Breath")
            .build()
            .bucket()
            .properties(properties -> properties.rarity(Rarity.UNCOMMON))
            .lang("Dragon's Breath Bucket")
            .tag(CDPItems.COMMON_TAGS.dragonBreathBuckets)
            .build()
            .setData(ProviderType.RECIPE, (ctx, prov) -> {
                CreateRecipeBuilders.emptying(ctx.getId().withPath("dragon_breath"))
                        .require(Items.DRAGON_BREATH)
                        .output(ctx.get(), 250)
                        .output(Items.GLASS_BOTTLE)
                        .withCondition(CDPConfig.features().dragonBreathFluid)
                        .build(prov);
                CreateRecipeBuilders.filling(ctx.getId().withPath("dragon_breath"))
                        .require(ctx.get(), 250)
                        .require(Items.GLASS_BOTTLE)
                        .output(Items.DRAGON_BREATH)
                        .withCondition(CDPConfig.features().dragonBreathFluid)
                        .build(prov);
            })
            .register();

    public static void register(IEventBus modBus) {
        modBus.register(CDPFluids.class);
        REGISTRATE.registerFluidTags(MOD_TAGS);
        REGISTRATE.registerFluidTags(COMMON_TAGS);
    }

    public static void registerDispenserBehavior() {
        DYES_BY_VARIANT.values().forEach(dyeFluid -> DispenserBlock.registerBehavior(dyeFluid.getBucket().get(), StandardDispenserBehaviour.INSTANCE));
        DispenserBlock.registerBehavior(DRAGON_BREATH.getBucket().get(), StandardDispenserBehaviour.INSTANCE);
    }

    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(Reactions::registerFluidInteractions);
        event.enqueueWork(Reactions::registerOpenPipeEffects);
        event.enqueueWork(CDPFluids::registerDispenserBehavior);
    }

    private static FluidEntry<BaseFlowingFluid.Flowing> dye(DyeVariant variant) {
        var stillTexture = REGISTRATE.asResource("fluid/dye_still");
        var flowingTexture = REGISTRATE.asResource("fluid/dye_flow");
        var tintColor = FastColor.ARGB32.opaque(variant.color());
        var name = variant.fluidName();
        var tag = COMMON_TAGS.dyesByVariant.get(variant.id());
        return REGISTRATE.fluid(name, stillTexture, flowingTexture, DyeFluidType.create(variant))
                .properties(properties -> properties
                        .fallDistanceModifier(0f)
                        .canExtinguish(true)
                        .supportsBoating(true)
                        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                        .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH))
                .fluidProperties(properties -> properties.explosionResistance(100))
                .block((fluid, prop) -> new DyeLiquidBlock(variant, fluid, prop))
                .build()
                .source(BaseFlowingFluid.Source::new)
                .bucket()
                .transform(builder -> tagDyeBucket(builder, variant))
                .tag(CDPItems.COMMON_TAGS.dyeBucketsByVariant.get(variant.id()))
                .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.modLoc("dye_bucket")))
                .color(() -> SimpleItemColors.singleLayer(tintColor))
                .build()
                .transform(builder -> tagDyeFluid(builder, variant, tag))
                .setData(ProviderType.RECIPE, (ctx, prov) -> {
                    var fromItem = CreateRecipeBuilders.mixing(ctx.getId().withPath(name + "_from_item"))
                            .require(variant.dyeItemTag())
                            .require(Fluids.WATER, 250)
                            .output(ctx.get(), 250)
                            .withCondition(CDPConfig.features().dyeFluids);
                    var fromFluid = CreateRecipeBuilders.mixing(ctx.getId().withPath(name + "_from_fluid"))
                            .require(ctx.get(), 250)
                            .output(variant.dyeItemId())
                            .requiresHeat(HeatCondition.HEATED)
                            .withCondition(CDPConfig.features().dyeFluids);
                    if (variant.requiredModId() != null) {
                        var condition = new ModLoadedCondition(variant.requiredModId());
                        fromItem.withCondition(condition);
                        fromFluid.withCondition(condition);
                    }
                    fromItem.build(prov);
                    fromFluid.build(prov);
                })
                .setData(ProviderType.DATA_MAP, (ctx, prov) -> prov
                        .builder(CDPDataMaps.FLUID_FAN_COLORING_CATALYSTS)
                        .add(tag, variant.id(), false))
                .register();
    }

    private static <I extends BucketItem, P> ItemBuilder<I, P> tagDyeBucket(ItemBuilder<I, P> builder, DyeVariant variant) {
        if (variant.requiredModId() != null)
            builder.asOptional();
        return builder;
    }

    private static <T extends BaseFlowingFluid, P> FluidBuilder<T, P> tagDyeFluid(FluidBuilder<T, P> builder, DyeVariant variant, TagKey<Fluid> tag) {
        if (variant.requiredModId() == null)
            return builder.tag(tag);
        COMMON_TAGS.addOptional(tag, CDPCommon.asResource(variant.fluidName()));
        COMMON_TAGS.addOptional(tag, CDPCommon.asResource("flowing_" + variant.fluidName()));
        return builder;
    }

    public static class ModTags extends IntrinsicTagRegistry<Fluid, IntrinsicImpl<Fluid>> {
        public final TagKey<Fluid> fanEndingCatalysts = tag("fan_processing_catalysts/ending", "Bulk Ending Catalysts");

        public ModTags() {
            super(CDPCommon.ID, Registries.FLUID);
        }
    }

    public static class CommonTags extends IntrinsicTagRegistry<Fluid, IntrinsicImpl<Fluid>> {
        public final TagKey<Fluid> dyes = tag("dyes", "Dyes");
        public final Map<ResourceLocation, TagKey<Fluid>> dyesByVariant = new LinkedHashMap<>();
        {
            for (var variant : DyeVariantRegistry.all()) {
                var tag = tag("dyes/" + variant.serializedName(), variant.displayName() + " Dye");
                dyesByVariant.put(variant.id(), tag);
                if (variant.requiredModId() == null)
                    addTag(this.dyes, tag);
                else
                    addOptionalTag(this.dyes, tag.location());
            }
        }
        public final TagKey<Fluid> dragonBreath = tag("dragon_breath", "Dragon's Breath");

        protected CommonTags() {
            super("c", Registries.FLUID);
        }
    }

    @EventBusSubscriber
    public static class Reactions {
        private static final Map<FluidType, BlockState> LAVA_INTERACTIONS = new HashMap<>();

        @SubscribeEvent
        public static void onPipeCollisionFlow(final PipeCollisionEvent.Flow event) {
            FluidType first = event.getFirstFluid().getFluidType();
            FluidType second = event.getSecondFluid().getFluidType();
            if (first == NeoForgeMod.LAVA_TYPE.value() && LAVA_INTERACTIONS.containsKey(second)) {
                event.setState(LAVA_INTERACTIONS.get(second));
            } else if (second == NeoForgeMod.LAVA_TYPE.value() && LAVA_INTERACTIONS.containsKey(first)) {
                event.setState(LAVA_INTERACTIONS.get(first));
            }
        }

        @SubscribeEvent
        public static void onPipeCollisionSpill(final PipeCollisionEvent.Spill event) {
            Fluid world = event.getWorldFluid();
            Fluid pipe = event.getPipeFluid();
            FluidType worldType = world.getFluidType();
            FluidType pipeType = pipe.getFluidType();
            if (worldType == NeoForgeMod.LAVA_TYPE.value() && LAVA_INTERACTIONS.containsKey(pipeType)) {
                if (world.isSource(world.defaultFluidState())) {
                    event.setState(Blocks.OBSIDIAN.defaultBlockState());
                } else {
                    event.setState(LAVA_INTERACTIONS.get(pipeType));
                }
            } else if (pipeType == NeoForgeMod.LAVA_TYPE.value() && LAVA_INTERACTIONS.containsKey(worldType)) {
                if (pipe.isSource(pipe.defaultFluidState())) {
                    event.setState(Blocks.OBSIDIAN.defaultBlockState());
                } else {
                    event.setState(LAVA_INTERACTIONS.get(worldType));
                }
            }
        }

        static void registerFluidInteractions() {
            var genConcrete = CDPConfig.common().features.dyeFluidsLavaInteractionGenerateColoredConcrete.get();
            DYES_BY_VARIANT.forEach((id, entry) -> {
                var variant = DyeVariantRegistry.get(id).orElseThrow();
                var type = entry.getType();
                var block = BuiltInRegistries.BLOCK.get(variant.concreteBlockId());
                var result = genConcrete && block != Blocks.AIR ? block.defaultBlockState() : Blocks.COBBLESTONE.defaultBlockState();
                LAVA_INTERACTIONS.put(type, result);
                FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new InteractionInformation(
                        type,
                        fluidState -> fluidState.isSource()
                                ? Blocks.OBSIDIAN.defaultBlockState()
                                : result));
            });
            LAVA_INTERACTIONS.put(DRAGON_BREATH.getType(), Blocks.END_STONE.defaultBlockState());
            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new InteractionInformation(
                    DRAGON_BREATH.getType(),
                    fluidState -> fluidState.isSource()
                            ? Blocks.OBSIDIAN.defaultBlockState()
                            : Blocks.END_STONE.defaultBlockState()));
        }

        static void registerOpenPipeEffects() {
            DYES_BY_VARIANT.forEach((id, entry) -> DyeVariantRegistry.get(id)
                    .ifPresent(variant -> OpenPipeEffectHandler.REGISTRY.register(entry.getSource(), new DyeFluidOpenPipeEffect(variant))));
            OpenPipeEffectHandler.REGISTRY.register(DRAGON_BREATH.getSource(), new DragonsBreathOpenPipeEffect());
        }
    }
}
