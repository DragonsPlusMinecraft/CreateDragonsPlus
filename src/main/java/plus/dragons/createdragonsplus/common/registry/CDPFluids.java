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
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider.IntrinsicImpl;
import com.tterrag.registrate.util.entry.FluidEntry;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
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
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry.InteractionInformation;
import net.neoforged.neoforge.fluids.FluidType;
import plus.dragons.createdragonsplus.client.color.SimpleItemColors;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.fluids.dragonBreath.DragonBreathFluidType;
import plus.dragons.createdragonsplus.common.fluids.dragonBreath.DragondBreathLiquidBlock;
import plus.dragons.createdragonsplus.common.fluids.dragonBreath.DragonsBreathOpenPipeEffect;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeColors;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeFluidOpenPipeEffect;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeFluidType;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeLiquidBlock;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.data.recipe.CreateRecipeBuilders;
import plus.dragons.createdragonsplus.data.tag.IntrinsicTagRegistry;

public class CDPFluids {
    public static final ModTags MOD_TAGS = new ModTags();
    public static final CommonTags COMMON_TAGS = new CommonTags();
    public static final EnumMap<DyeColor, FluidEntry<BaseFlowingFluid.Flowing>> DYES_BY_COLOR = Util.make(
            new EnumMap<>(DyeColor.class),
            map -> {
                for (var color : DyeColors.ALL) map.put(color, dye(color));
            });
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

    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(Reactions::registerFluidInteractions);
        event.enqueueWork(Reactions::registerOpenPipeEffects);
    }

    private static FluidEntry<BaseFlowingFluid.Flowing> dye(DyeColor color) {
        var stillTexture = REGISTRATE.asResource("fluid/dye_still");
        var flowingTexture = REGISTRATE.asResource("fluid/dye_flow");
        var tintColor = FastColor.ARGB32.opaque(color.getTextureDiffuseColor());
        var name = color.getName() + "_dye";
        var tag = COMMON_TAGS.dyesByColor.get(color);
        return REGISTRATE.fluid(name, stillTexture, flowingTexture, DyeFluidType.create(color))
                .properties(properties -> properties
                        .fallDistanceModifier(0f)
                        .canExtinguish(true)
                        .supportsBoating(true)
                        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                        .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH))
                .fluidProperties(properties -> properties.explosionResistance(100))
                .block((fluid, prop) -> new DyeLiquidBlock(color, fluid, prop))
                .build()
                .source(BaseFlowingFluid.Source::new)
                .bucket()
                .tag(CDPItems.COMMON_TAGS.dyeBucketsByColor.get(color))
                .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.modLoc("dye_bucket")))
                .color(() -> SimpleItemColors.singleLayer(tintColor))
                .tag(color.getDyedTag())
                .build()
                .tag(tag)
                .setData(ProviderType.RECIPE, (ctx, prov) -> {
                    CreateRecipeBuilders.mixing(ctx.getId().withPath(name + "_from_item"))
                            .require(DyeItem.byColor(color))
                            .require(Fluids.WATER, 250)
                            .output(ctx.get(), 250)
                            .withCondition(CDPConfig.features().dyeFluids)
                            .build(prov);
                    CreateRecipeBuilders.mixing(ctx.getId().withPath(name + "_from_fluid"))
                            .require(ctx.get(), 250)
                            .output(DyeItem.byColor(color))
                            .requiresHeat(HeatCondition.HEATED)
                            .withCondition(CDPConfig.features().dyeFluids)
                            .build(prov);
                })
                .setData(ProviderType.DATA_MAP, (ctx, prov) -> prov
                        .builder(CDPDataMaps.FLUID_FAN_COLORING_CATALYSTS)
                        .add(tag, color, false))
                .register();
    }

    public static class ModTags extends IntrinsicTagRegistry<Fluid, IntrinsicImpl<Fluid>> {
        public final TagKey<Fluid> fanEndingCatalysts = tag("fan_processing_catalysts/ending", "Bulk Ending Catalysts");

        public ModTags() {
            super(CDPCommon.ID, Registries.FLUID);
        }
    }

    public static class CommonTags extends IntrinsicTagRegistry<Fluid, IntrinsicImpl<Fluid>> {
        public final TagKey<Fluid> dyes = tag("dyes", "Dyes");
        public final EnumMap<DyeColor, TagKey<Fluid>> dyesByColor = Util.make(new EnumMap<>(DyeColor.class), map -> {
            for (var color : DyeColors.ALL) {
                var tag = tag("dyes/" + color.getName(), DyeColors.LOCALIZATION.get(color) + " Dye");
                map.put(color, tag);
                addTag(this.dyes, tag);
            }
        });
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
            DYES_BY_COLOR.forEach((color, entry) -> {
                var type = entry.getType();
                var block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(color.getName()).withSuffix("_concrete"));
                if (block == Blocks.AIR)
                    return;
                LAVA_INTERACTIONS.put(type, block.defaultBlockState());
                FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new InteractionInformation(
                        type,
                        fluidState -> fluidState.isSource()
                                ? Blocks.OBSIDIAN.defaultBlockState()
                                : block.defaultBlockState()));
            });
            LAVA_INTERACTIONS.put(DRAGON_BREATH.getType(), Blocks.END_STONE.defaultBlockState());
            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new InteractionInformation(
                    DRAGON_BREATH.getType(),
                    fluidState -> fluidState.isSource()
                            ? Blocks.OBSIDIAN.defaultBlockState()
                            : Blocks.END_STONE.defaultBlockState()));
        }

        static void registerOpenPipeEffects() {
            DYES_BY_COLOR.forEach((color, entry) -> OpenPipeEffectHandler.REGISTRY.register(entry.getSource(), new DyeFluidOpenPipeEffect(color)));
            OpenPipeEffectHandler.REGISTRY.register(DRAGON_BREATH.getSource(), new DragonsBreathOpenPipeEffect());
        }
    }
}
