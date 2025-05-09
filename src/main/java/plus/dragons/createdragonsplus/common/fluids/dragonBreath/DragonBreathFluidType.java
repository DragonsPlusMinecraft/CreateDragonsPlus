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

package plus.dragons.createdragonsplus.common.fluids.dragonBreath;

import com.tterrag.registrate.builders.FluidBuilder.FluidTypeFactory;
import java.util.function.Supplier;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import plus.dragons.createdragonsplus.common.fluids.SolidRenderFluidType;
import plus.dragons.createdragonsplus.config.CDPConfig;

public final class DragonBreathFluidType extends SolidRenderFluidType {
    private DragonBreathFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, int tintColor, Vector3f fogColor, Supplier<Float> fogDistanceModifier) {
        super(properties, stillTexture, flowingTexture, tintColor, fogColor, fogDistanceModifier);
    }

    public static FluidTypeFactory create() {
        int tintColor = FastColor.ARGB32.opaque(0xFFFFFF);
        Vector3f fogColor = new Color(0xDE9DC5).asVectorF();
        return (properties, stillTexture, flowingTexture) -> new DragonBreathFluidType(properties,
                stillTexture,
                flowingTexture,
                tintColor,
                fogColor,
                DragonBreathFluidType::getVisibility);
    }

    private static float getVisibility() {
        return CDPConfig.client().dragonBreathVisionMultiplier.getF() / 256;
    }

    @Override
    public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
        boolean falling = entity.getDeltaMovement().y <= 0.0;
        double y = entity.getY();
        entity.moveRelative(0.02F, movementVector);
        entity.move(MoverType.SELF, entity.getDeltaMovement());
        if (entity.getFluidTypeHeight(this) <= entity.getFluidJumpThreshold()) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.5, 0.8F, 0.5));
            Vec3 adjustedMovement = entity.getFluidFallingAdjustedMovement(gravity, falling, entity.getDeltaMovement());
            entity.setDeltaMovement(adjustedMovement);
        } else {
            entity.setDeltaMovement(entity.getDeltaMovement().scale(0.5));
        }
        if (gravity != 0.0) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, -gravity / 4.0, 0.0));
        }
        Vec3 deltaMovement = entity.getDeltaMovement();
        if (entity.horizontalCollision && entity.isFree(deltaMovement.x, deltaMovement.y + 0.6F - entity.getY() + y, deltaMovement.z)) {
            entity.setDeltaMovement(deltaMovement.x, 0.3F, deltaMovement.z);
        }
        return true;
    }

    @Override
    public void setItemMovement(ItemEntity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        entity.setDeltaMovement(vec3.x * (double) 0.95F, vec3.y + (double) (vec3.y < (double) 0.06F ? 5.0E-4F : 0.0F), vec3.z * (double) 0.95F);
    }

    @Override
    public boolean isVaporizedOnPlacement(Level level, BlockPos pos, FluidStack stack) {
        return level.dimensionType().ultraWarm();
    }

    @Override
    public void onVaporize(@Nullable Player player, Level level, BlockPos pos, FluidStack stack) {
        SoundEvent sound = this.getSound(player, level, pos, SoundActions.FLUID_VAPORIZE);
        level.playSound(player, pos, sound != null ? sound : SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

        AreaEffectCloud aoe = new AreaEffectCloud(level, pos.getX(), pos.getY(), pos.getZ());
        aoe.setOwner(player);
        aoe.setParticle(ParticleTypes.DRAGON_BREATH);
        aoe.setRadius(stack.getAmount() / 500F);
        aoe.setDuration(stack.getAmount() / 5);
        aoe.setRadiusPerTick(-0.01F);
        aoe.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
        aoe.getPersistentData().putBoolean("DragonBreath", true);
        level.levelEvent(2006, pos, -1);
        level.addFreshEntity(aoe);
    }
}
