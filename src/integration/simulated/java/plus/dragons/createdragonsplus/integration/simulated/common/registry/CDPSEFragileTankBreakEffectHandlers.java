package plus.dragons.createdragonsplus.integration.simulated.common.registry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllFluids;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.impl.effect.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3d;
import plus.dragons.createdragonsplus.integration.simulated.api.fluids.tank.FragileFluidTankBreakEffectHandler;
import plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank.DefaultRangedEffectHandler;
import plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank.OpenEndedPipeEffectHandlerWrapper;
import plus.dragons.createdragonsplus.integration.simulated.config.CDPSEConfig;
import plus.dragons.createdragonsplus.util.CodeReference;

import java.util.Arrays;
import java.util.List;

public class CDPSEFragileTankBreakEffectHandlers {
    public static void registerDefaults() {
        FragileFluidTankBreakEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(Tags.Fluids.MILK, OpenEndedPipeEffectHandlerWrapper.of(new MilkEffectHandler())));
        FragileFluidTankBreakEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(Tags.Fluids.LAVA, new LavaHandler()));
        FragileFluidTankBreakEffectHandler.REGISTRY.register(AllFluids.POTION.getSource(), new PotionHandler());
        FragileFluidTankBreakEffectHandler.REGISTRY.register(AllFluids.TEA.getSource(), new TeaHandler());

    }

    private static class LavaHandler extends DefaultRangedEffectHandler.AffectBlock {
        @Override
        protected void onHitDoBlock(Level level, BlockPos pos, BlockState state, FluidStack fluid) {
            if (state.isAir()) return;
            if (CDPSEConfig.fluid().fragileFluidTankLavaIgniteBlock.get()){
                if ((state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE)) && !state.getValue(CampfireBlock.LIT) && level.getFluidState(pos).isEmpty()) {
                    level.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT,true));
                } else if (state.is(AllBlocks.BLAZE_BURNER) && state.getValue(BlazeBurnerBlock.HEAT_LEVEL) == BlazeBurnerBlock.HeatLevel.NONE) {
                    level.setBlockAndUpdate(pos, AllBlocks.LIT_BLAZE_BURNER.getDefaultState());
                }
            }
            if(CDPSEConfig.fluid().fragileFluidTankLavaSpreadFire.get()){
                Arrays.stream(Direction.values()).forEach(direction -> {
                    var p = pos.relative(direction);
                    if(level.getBlockState(p).isAir()){
                       if(state.ignitedByLava(level, p, direction)){
                           level.setBlockAndUpdate(p, EventHooks.fireFluidPlaceBlockEvent(level, p, p, BaseFireBlock.getState(level, p)));
                       }
                    }
                });
            }
        }

        @Override
        public void onHitDoRest(Level level, AABB area, Vector3d hitPos, FluidStack fluid) {
            var duration = fluid.getAmount() / CDPSEConfig.fluid().fragileFluidTankEffectAmplifiedUnit.get() * 100;
            level.getEntitiesOfClass(Entity.class, area, entity ->
                            isEntityInRangeConsideringSubLevel(level, entity, hitPos, CDPSEConfig.fluid().fragileFluidTankAffectMaxRadius.get()) && !entity.isInWater())
                    .forEach(livingEntity -> livingEntity.setRemainingFireTicks(duration));

        }
    }

    private static class TeaHandler extends DefaultRangedEffectHandler {
        @Override
        public void onHit(Level level, AABB area, Vector3d hitPos, FluidStack fluid) {
            var duration = fluid.getAmount() / CDPSEConfig.fluid().fragileFluidTankEffectAmplifiedUnit.get() * 300;
            level.getEntitiesOfClass(LivingEntity.class, area, (livingEntity) -> isEntityInRangeConsideringSubLevel(level, livingEntity, hitPos, CDPSEConfig.fluid().fragileFluidTankAffectMaxRadius.get()))
                    .forEach(livingEntity -> livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, (int) duration, 0, false, false, false)));
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

            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, livingEntity->
                    isEntityInRangeConsideringSubLevel(level, livingEntity, hitPos, CDPSEConfig.fluid().fragileFluidTankAffectMaxRadius.get()) && livingEntity.isAffectedByPotions());
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
}
