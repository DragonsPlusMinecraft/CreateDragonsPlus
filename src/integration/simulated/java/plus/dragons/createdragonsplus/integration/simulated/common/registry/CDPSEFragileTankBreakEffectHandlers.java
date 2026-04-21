package plus.dragons.createdragonsplus.integration.simulated.common.registry;

import com.simibubi.create.AllFluids;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.impl.effect.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import plus.dragons.createdragonsplus.integration.simulated.api.fluids.tank.FragileFluidTankBreakEffectHandler;
import plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank.DefaultRangedEffectHandler;
import plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank.OpenEndedPipeEffectHandlerWarper;
import plus.dragons.createdragonsplus.util.CodeReference;

import java.util.List;

public class CDPSEFragileTankBreakEffectHandlers {
    public static void registerDefaults() {
        FragileFluidTankBreakEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(Tags.Fluids.MILK, OpenEndedPipeEffectHandlerWarper.of(new MilkEffectHandler())));
        FragileFluidTankBreakEffectHandler.REGISTRY.register(AllFluids.POTION.getSource(), new PotionHandler());
        FragileFluidTankBreakEffectHandler.REGISTRY.register(AllFluids.TEA.getSource(), new TeaHandler());
    }

    @CodeReference(source = "com.simibubi.create.impl.effect.TeaEffectHandler", license = "mit")
    private static class TeaHandler extends DefaultRangedEffectHandler {
        @Override
        public void onHit(Level level, AABB area, FluidStack fluid) {
            var duration = fluid.getAmount() * 1.2;
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAffectedByPotions);
            for (LivingEntity entity : entities) {
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, (int) duration, 0, false, false, false));
            }
        }
    }

    @CodeReference(source = "com.simibubi.create.impl.effect.PotionEffectHandler", license = "mit")
    public static class PotionHandler extends DefaultRangedEffectHandler {
        @Override
        public void onHit(Level level, AABB area, FluidStack fluid) {
            var amplifier = fluid.getAmount() / 500;
            PotionContents contents = getContents(fluid);
            if (contents == PotionContents.EMPTY)
                return;

            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAffectedByPotions);
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
