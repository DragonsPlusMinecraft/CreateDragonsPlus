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

package plus.dragons.createdragonsplus.integration.simulated.common.registry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllFluids;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.impl.effect.MilkEffectHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.FluidStack;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariant;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariantRegistry;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.ColoringFanProcessingType;
import plus.dragons.createdragonsplus.common.registry.CDPFanProcessingTypes;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.integration.simulated.api.fluids.tank.FragileFluidTankBreakEffectHandler;
import plus.dragons.createdragonsplus.integration.simulated.api.fluids.tank.FragileFluidTankImpactContext;
import plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank.DefaultRangedEffectHandler;
import plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank.OpenEndedPipeEffectHandlerWrapper;
import plus.dragons.createdragonsplus.integration.simulated.config.CDPSEConfig;
import plus.dragons.createdragonsplus.util.CodeReference;

public class CDPSEFragileTankBreakEffectHandlers {
    public static final TagKey<Fluid> EXPLOSIVE_FLUIDS = TagKey.create(Registries.FLUID, CDPCommon.asResource("fragile_fluid_tank/explosive"));
    private static final List<Runnable> RELOAD_FUNCTION = new ArrayList<>();
    private static final ResourceManagerReloadListener RELOAD_LISTENER = resourceManager -> RELOAD_FUNCTION.forEach(Runnable::run);

    public static void registerDefaults() {
        FragileFluidTankBreakEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(Tags.Fluids.MILK, OpenEndedPipeEffectHandlerWrapper.of(new MilkEffectHandler())));
        FragileFluidTankBreakEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(Tags.Fluids.LAVA, new LavaHandler()));
        FragileFluidTankBreakEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(Tags.Fluids.WATER, new WaterHandler()));
        FragileFluidTankBreakEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(CDPFluids.COMMON_TAGS.dragonBreath, new DragonBreathHandler()));
        FragileFluidTankBreakEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(EXPLOSIVE_FLUIDS, new ExplosiveFluidHandler()));
        FragileFluidTankBreakEffectHandler.REGISTRY.register(AllFluids.POTION.getSource(), new PotionHandler());
        FragileFluidTankBreakEffectHandler.REGISTRY.register(AllFluids.TEA.getSource(), new TeaHandler());
        for (var variant : DyeVariantRegistry.all()) {
            var fluid = CDPFluids.DYES_BY_VARIANT.get(variant.id());
            if (fluid != null) {
                FragileFluidTankBreakEffectHandler.REGISTRY.register(fluid.getSource(), new DyeFluidHandler(variant));
            }
        }
    }

    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(RELOAD_LISTENER);
    }

    private static class LavaHandler extends DefaultRangedEffectHandler.AffectBlock {
        @Override
        protected void onHitDoBlock(Level level, BlockPos pos, BlockState state, FluidStack fluid) {
            if (state.isAir()) return;
            if (CDPSEConfig.fluid().fragileFluidTankLavaIgniteBlock.get()) {
                if ((state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE)) && !state.getValue(CampfireBlock.LIT) && level.getFluidState(pos).isEmpty()) {
                    level.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, true));
                    return;
                } else if (state.is(AllBlocks.BLAZE_BURNER) && state.getValue(BlazeBurnerBlock.HEAT_LEVEL) == BlazeBurnerBlock.HeatLevel.NONE) {
                    level.setBlockAndUpdate(pos, AllBlocks.LIT_BLAZE_BURNER.getDefaultState());
                    return;
                }
            }
            if (CDPSEConfig.fluid().fragileFluidTankLavaSpreadFire.get()) {
                var d = Direction.getRandom(level.random);
                var p = pos.relative(d);
                if (level.getBlockState(p).isAir()) {
                    if (state.ignitedByLava(level, p, d)) {
                        level.setBlockAndUpdate(p, EventHooks.fireFluidPlaceBlockEvent(level, p, p, BaseFireBlock.getState(level, p)));
                        return;
                    }
                }
            }
            var r = state.getBlockHolder().getData(CDPSEDataMaps.FRAGILE_FLUID_TANK_LAVA);
            if (r != null)
                level.setBlockAndUpdate(pos, r.defaultBlockState());
        }

        @Override
        public void onHitDoRest(FragileFluidTankImpactContext context) {
            var duration = context.effectAmplifier() * 100;
            forEntitiesInRange(context, Entity.class, entity -> !entity.isInWater(), entity -> entity.setRemainingFireTicks(duration));
        }

        @Override
        public String getImpactEffectDescriptionKey(FluidStack fluid) {
            return "lava";
        }
    }

    private static class WaterHandler extends DefaultRangedEffectHandler.AffectBlock {
        @Override
        protected void onHitDoBlock(Level level, BlockPos pos, BlockState state, FluidStack fluid) {
            if (state.isAir()) return;
            if ((state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE)) && state.getValue(CampfireBlock.LIT)) {
                level.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, false));
                return;
            } else if (state.is(AllBlocks.LIT_BLAZE_BURNER)) {
                level.setBlockAndUpdate(pos, AllBlocks.BLAZE_BURNER.getDefaultState());
                return;
            }
            var r = state.getBlockHolder().getData(CDPSEDataMaps.FRAGILE_FLUID_TANK_WATER);
            if (r != null)
                level.setBlockAndUpdate(pos, r.defaultBlockState());
        }

        @Override
        public void onHitDoRest(FragileFluidTankImpactContext context) {
            forEntitiesInRange(context, Entity.class, Entity::isOnFire, Entity::extinguishFire);
        }

        @Override
        public String getImpactEffectDescriptionKey(FluidStack fluid) {
            return "water";
        }
    }

    private static class TeaHandler extends DefaultRangedEffectHandler {
        @Override
        public void onHit(FragileFluidTankImpactContext context) {
            var duration = context.effectAmplifier() * 300;
            forEntitiesInRange(context, LivingEntity.class, livingEntity -> true,
                    livingEntity -> livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, duration, 0, false, false, false)));
        }

        @Override
        public String getImpactEffectDescriptionKey(FluidStack fluid) {
            return "tea";
        }
    }

    private static class DragonBreathHandler extends DefaultRangedEffectHandler {
        @Override
        public void onHit(FragileFluidTankImpactContext context) {
            var amplifier = context.effectAmplifier();
            forEntitiesInRange(context, LivingEntity.class, livingEntity -> true,
                    livingEntity -> livingEntity.addEffect(new MobEffectInstance(MobEffects.HARM, 1, amplifier + 1, false, false, false)));
        }

        @Override
        public String getImpactEffectDescriptionKey(FluidStack fluid) {
            return "dragon_breath";
        }
    }

    @CodeReference(source = "com.simibubi.create.impl.effect.PotionEffectHandler", license = "mit")
    public static class PotionHandler extends DefaultRangedEffectHandler {
        @Override
        public void onHit(FragileFluidTankImpactContext context) {
            var amplifier = context.effectAmplifier();
            PotionContents contents = getContents(context.fluid());
            if (contents == PotionContents.EMPTY)
                return;
            List<LivingEntity> entities = context.level().getEntitiesOfClass(LivingEntity.class, context.area(),
                    livingEntity -> isEntityInRangeConsideringSubLevel(context, livingEntity) && livingEntity.isAffectedByPotions());
            for (LivingEntity entity : entities) {
                contents.forEachEffect(effectInstance -> {
                    MobEffect effect = effectInstance.getEffect().value();
                    if (effect.isInstantenous()) {
                        effect.applyInstantenousEffect(null, null, entity, effectInstance.getAmplifier() * amplifier, 0.5D);
                    } else {
                        entity.addEffect(new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration() * amplifier + 1, effectInstance.getAmplifier()));
                    }
                });
            }
        }

        @Override
        public String getImpactEffectDescriptionKey(FluidStack fluid) {
            return "effect";
        }

        private static PotionContents getContents(FluidStack fluid) {
            FluidStack copy = fluid.copy();
            copy.setAmount(250);
            ItemStack bottle = PotionFluidHandler.fillBottle(new ItemStack(Items.GLASS_BOTTLE), copy);
            return bottle.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        }
    }

    private static class DyeFluidHandler extends DefaultRangedEffectHandler.AffectBlock {
        private final DyeVariant variant;
        private ColoringFanProcessingType borrow;
        private final HashMap<Block, Block> transformingResultCache = new HashMap<>();

        private DyeFluidHandler(DyeVariant variant) {
            this.variant = variant;
            CDPSEFragileTankBreakEffectHandlers.RELOAD_FUNCTION.add(this::recreateCache);
        }

        public void recreateCache() {
            transformingResultCache.clear();
        }

        private ColoringFanProcessingType borrow() {
            if (borrow == null) {
                borrow = CDPFanProcessingTypes.COLORING.get(variant.id()).get();
            }
            return borrow;
        }

        @Override
        protected void onHitDoBlock(Level level, BlockPos pos, BlockState state, FluidStack fluid) {
            if (state.isAir()) return;
            if (CDPSEConfig.fluid().fragileFluidTankDyeColorBlock.get()) {
                if (transformingResultCache.containsKey(state.getBlock())) {
                    if (!transformingResultCache.get(state.getBlock()).equals(Blocks.AIR))
                        level.setBlockAndUpdate(pos, transformingResultCache.get(state.getBlock()).defaultBlockState());
                } else {
                    var result = borrow().process(new ItemStack(state.getBlock()), level);
                    if (result == null || result.size() != 1) {
                        transformingResultCache.put(state.getBlock(), Blocks.AIR);
                    } else {
                        transformingResultCache.put(state.getBlock(), Block.byItem(result.get(0).getItem()));
                        level.setBlockAndUpdate(pos, Block.byItem(result.get(0).getItem()).defaultBlockState());
                    }
                }
            }
        }

        @Override
        public void onHitDoRest(FragileFluidTankImpactContext context) {
            forEntitiesInRange(context, LivingEntity.class, entity -> true, entity -> {
                borrow().applyColoring(entity, context.level());
                if (entity instanceof EnderMan || entity.getType() == EntityType.SNOW_GOLEM || entity.getType() == EntityType.BLAZE) {
                    entity.hurt(entity.damageSources().drown(), 2);
                }
            });
        }

        @Override
        public String getImpactEffectDescriptionKey(FluidStack fluid) {
            return "dye";
        }
    }

    private static class ExplosiveFluidHandler extends DefaultRangedEffectHandler {
        @Override
        protected void onHit(FragileFluidTankImpactContext context) {
            if (!CDPSEConfig.fluid().fragileFluidTankExplosiveFluidExplosion.get())
                return;
            float min = CDPSEConfig.fluid().fragileFluidTankExplosiveFluidMinPower.getF();
            float max = CDPSEConfig.fluid().fragileFluidTankExplosiveFluidMaxPower.getF();
            float power = Mth.lerp(Mth.clamp(context.fullness(), 0.0f, 1.0f), min, max);
            if (power <= 0)
                return;
            context.level().explode(null,
                    context.hitPos().x,
                    context.hitPos().y,
                    context.hitPos().z,
                    power,
                    CDPSEConfig.fluid().fragileFluidTankExplosiveFluidCausesFire.get(),
                    Level.ExplosionInteraction.TNT);
        }

        @Override
        public String getImpactEffectDescriptionKey(FluidStack fluid) {
            return "explosive";
        }
    }
}
