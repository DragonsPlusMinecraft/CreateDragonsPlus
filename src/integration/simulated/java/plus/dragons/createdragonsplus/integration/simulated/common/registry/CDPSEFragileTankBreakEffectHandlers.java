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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3d;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.ColoringFanProcessingType;
import plus.dragons.createdragonsplus.common.registry.CDPFanProcessingTypes;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.integration.simulated.api.fluids.tank.FragileFluidTankBreakEffectHandler;
import plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank.DefaultRangedEffectHandler;
import plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank.OpenEndedPipeEffectHandlerWrapper;
import plus.dragons.createdragonsplus.integration.simulated.config.CDPSEConfig;
import plus.dragons.createdragonsplus.util.CodeReference;

public class CDPSEFragileTankBreakEffectHandlers {
    private static final List<Runnable> RELOAD_FUNCTION = new ArrayList<>();
    private static final ResourceManagerReloadListener RELOAD_LISTENER = resourceManager -> RELOAD_FUNCTION.forEach(Runnable::run);

    public static void registerDefaults() {
        FragileFluidTankBreakEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(Tags.Fluids.MILK, OpenEndedPipeEffectHandlerWrapper.of(new MilkEffectHandler())));
        FragileFluidTankBreakEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(Tags.Fluids.LAVA, new LavaHandler()));
        FragileFluidTankBreakEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(Tags.Fluids.WATER, new WaterHandler()));
        FragileFluidTankBreakEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(CDPFluids.COMMON_TAGS.dragonBreath, new DragonBreathHandler()));
        FragileFluidTankBreakEffectHandler.REGISTRY.register(AllFluids.POTION.getSource(), new PotionHandler());
        FragileFluidTankBreakEffectHandler.REGISTRY.register(AllFluids.TEA.getSource(), new TeaHandler());
        for (DyeColor color : DyeColor.values()) {
            if (CDPFluids.DYES_BY_COLOR.containsKey(color)) {
                var fluid = CDPFluids.DYES_BY_COLOR.get(color);
                FragileFluidTankBreakEffectHandler.REGISTRY.register(fluid.getSource(), new DyeFluidHandler(color));
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
        public void onHitDoRest(Level level, AABB area, Vector3d hitPos, FluidStack fluid) {
            var duration = fluid.getAmount() / CDPSEConfig.fluid().fragileFluidTankEffectAmplifiedUnit.get() * 100;
            double validRange = (double) fluid.getAmount() / CDPSEConfig.fluid().fragileFluidTankCapacity.get() * CDPSEConfig.fluid().fragileFluidTankAffectMaxRadius.get();
            level.getEntitiesOfClass(Entity.class, area, entity -> isEntityInRangeConsideringSubLevel(level, entity, hitPos, validRange) && !entity.isInWater())
                    .forEach(livingEntity -> livingEntity.setRemainingFireTicks(duration));
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
        public void onHitDoRest(Level level, AABB area, Vector3d hitPos, FluidStack fluid) {
            double validRange = (double) fluid.getAmount() / CDPSEConfig.fluid().fragileFluidTankCapacity.get() * CDPSEConfig.fluid().fragileFluidTankAffectMaxRadius.get();
            level.getEntitiesOfClass(Entity.class, area, entity -> isEntityInRangeConsideringSubLevel(level, entity, hitPos, validRange) && entity.isOnFire()).forEach(Entity::extinguishFire);
        }
    }

    private static class TeaHandler extends DefaultRangedEffectHandler {
        @Override
        public void onHit(Level level, AABB area, Vector3d hitPos, FluidStack fluid) {
            var duration = fluid.getAmount() / CDPSEConfig.fluid().fragileFluidTankEffectAmplifiedUnit.get() * 300;
            double validRange = (double) fluid.getAmount() / CDPSEConfig.fluid().fragileFluidTankCapacity.get() * CDPSEConfig.fluid().fragileFluidTankAffectMaxRadius.get();
            level.getEntitiesOfClass(LivingEntity.class, area, (livingEntity) -> isEntityInRangeConsideringSubLevel(level, livingEntity, hitPos, validRange))
                    .forEach(livingEntity -> livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, duration, 0, false, false, false)));
        }
    }

    private static class DragonBreathHandler extends DefaultRangedEffectHandler {
        @Override
        public void onHit(Level level, AABB area, Vector3d hitPos, FluidStack fluid) {
            var amplifier = fluid.getAmount() / CDPSEConfig.fluid().fragileFluidTankEffectAmplifiedUnit.get();
            double validRange = (double) fluid.getAmount() / CDPSEConfig.fluid().fragileFluidTankCapacity.get() * CDPSEConfig.fluid().fragileFluidTankAffectMaxRadius.get();
            level.getEntitiesOfClass(LivingEntity.class, area, (livingEntity) -> isEntityInRangeConsideringSubLevel(level, livingEntity, hitPos, validRange))
                    .forEach(livingEntity -> livingEntity.addEffect(new MobEffectInstance(MobEffects.HARM, 1, amplifier + 1, false, false, false)));
        }
    }

    @CodeReference(source = "com.simibubi.create.impl.effect.PotionEffectHandler", license = "mit")
    public static class PotionHandler extends DefaultRangedEffectHandler {
        @Override
        public void onHit(Level level, AABB area, Vector3d hitPos, FluidStack fluid) {
            var amplifier = fluid.getAmount() / CDPSEConfig.fluid().fragileFluidTankEffectAmplifiedUnit.get();
            PotionContents contents = getContents(fluid);
            if (contents == PotionContents.EMPTY)
                return;
            double validRange = (double) fluid.getAmount() / CDPSEConfig.fluid().fragileFluidTankCapacity.get() * CDPSEConfig.fluid().fragileFluidTankAffectMaxRadius.get();
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, livingEntity -> isEntityInRangeConsideringSubLevel(level, livingEntity, hitPos, validRange) && livingEntity.isAffectedByPotions());
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

        private static PotionContents getContents(FluidStack fluid) {
            FluidStack copy = fluid.copy();
            copy.setAmount(250);
            ItemStack bottle = PotionFluidHandler.fillBottle(new ItemStack(Items.GLASS_BOTTLE), copy);
            return bottle.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        }
    }

    private static class DyeFluidHandler extends DefaultRangedEffectHandler.AffectBlock {
        private final DyeColor color;
        private ColoringFanProcessingType borrow;
        private final HashMap<Block, Block> transformingResultCache = new HashMap<>();

        private DyeFluidHandler(DyeColor color) {
            this.color = color;
            CDPSEFragileTankBreakEffectHandlers.RELOAD_FUNCTION.add(this::recreateCache);
        }

        public void recreateCache() {
            transformingResultCache.clear();
        }

        @Override
        protected void onHitDoBlock(Level level, BlockPos pos, BlockState state, FluidStack fluid) {
            if (state.isAir()) return;
            if (borrow == null) {
                borrow = CDPFanProcessingTypes.COLORING.get(color).get();
            }
            if (CDPSEConfig.fluid().fragileFluidTankDyeColorBlock.get()) {
                if (transformingResultCache.containsKey(state.getBlock())) {
                    if (!transformingResultCache.get(state.getBlock()).equals(Blocks.AIR))
                        level.setBlockAndUpdate(pos, transformingResultCache.get(state.getBlock()).defaultBlockState());
                } else {
                    var result = borrow.process(new ItemStack(state.getBlock()), level);
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
        public void onHitDoRest(Level level, AABB area, Vector3d hitPos, FluidStack fluid) {
            double validRange = (double) fluid.getAmount() / CDPSEConfig.fluid().fragileFluidTankCapacity.get() * CDPSEConfig.fluid().fragileFluidTankAffectMaxRadius.get();
            level.getEntitiesOfClass(LivingEntity.class, area, entity -> isEntityInRangeConsideringSubLevel(level, entity, hitPos, validRange))
                    .forEach(entity -> {
                        if (entity instanceof LivingEntity livingEntity)
                            borrow.applyColoring(livingEntity, level);
                        if (entity instanceof EnderMan || entity.getType() == EntityType.SNOW_GOLEM || entity.getType() == EntityType.BLAZE) {
                            entity.hurt(entity.damageSources().drown(), 2);
                        }
                    });
        }
    }
}
