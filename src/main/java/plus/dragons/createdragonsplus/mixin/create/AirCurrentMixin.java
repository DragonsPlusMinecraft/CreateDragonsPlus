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

package plus.dragons.createdragonsplus.mixin.create;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import plus.dragons.createdragonsplus.common.kinetics.fan.AirCurrentAccess;
import plus.dragons.createdragonsplus.common.kinetics.fan.AirCurrentSegmentAccess;
import plus.dragons.createdragonsplus.common.kinetics.fan.DynamicParticleFanProcessingType;
import plus.dragons.createdragonsplus.common.registry.CDPFanProcessingTypes;

@Mixin(AirCurrent.class)
public class AirCurrentMixin implements AirCurrentAccess {
    @Shadow
    public float maxDistance;
    @Shadow
    public List<AirCurrentSegmentAccess> segments;
    @Shadow
    @Final
    public IAirCurrentSource source;
    @Shadow
    public boolean pushing;

    @ModifyExpressionValue(method = "rebuild", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;getAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;"))
    private @Nullable FanProcessingType rebuild$checkDragonHead(@Nullable FanProcessingType original, @Local(name = "world") Level world, @Local(name = "currentPos") BlockPos currentPos) {
        var state = world.getBlockState(currentPos);
        var direction = source.getAirFlowDirection();
        if (state.is(Blocks.DRAGON_HEAD)) {
            var facing = RotationSegment.convertToDirection(state.getValue(SkullBlock.ROTATION)).orElse(null);
            if (direction == facing)
                return CDPFanProcessingTypes.ENDING.get();
        } else if (state.is(Blocks.DRAGON_WALL_HEAD) && state.getValue(WallSkullBlock.FACING) == direction) {
            return CDPFanProcessingTypes.ENDING.get();
        }
        return original;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @WrapOperation(method = "rebuild", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lcom/simibubi/create/content/kinetics/fan/AirCurrent$AirCurrentSegment;type:Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;"))
    private void rebuild$setParticleData(@Coerce Object segment, FanProcessingType value, Operation<Void> original, @Local(name = "world") Level world, @Local(name = "currentPos") BlockPos currentPos) {
        original.call(segment, value);
        var access = (AirCurrentSegmentAccess) segment;
        if (access.getType() instanceof DynamicParticleFanProcessingType type)
            access.setParticleData(type, type.getParticleDataAt(world, currentPos));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @WrapOperation(method = "tickAffectedEntities", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;spawnProcessingParticles(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;)V"))
    private void tickAffectedEntities$spawnProcessingParticlesWithParticleData(FanProcessingType type, Level level, Vec3 pos, Operation<Void> original, @Local(name = "entityDistance") double distance) {
        if (type instanceof DynamicParticleFanProcessingType dynamicType) {
            var segment = this.getSegmentAccessAt((float) distance);
            Object particleData = segment == null ? null : segment.getParticleData(dynamicType);
            dynamicType.spawnProcessingParticles(level, pos, particleData);
        } else {
            original.call(type, level, pos);
        }
    }

    @Override
    public @Nullable AirCurrentSegmentAccess getSegmentAccessAt(float offset) {
        if (offset >= 0 && offset <= this.maxDistance) {
            if (this.pushing) {
                for (AirCurrentSegmentAccess segment : this.segments) {
                    if (offset <= segment.getEndOffset()) {
                        return segment;
                    }
                }
            } else {
                for (AirCurrentSegmentAccess segment : segments) {
                    if (offset >= segment.getEndOffset()) {
                        return segment;
                    }
                }
            }
        }
        return null;
    }

    @Mixin(targets = "com.simibubi.create.content.kinetics.fan.AirCurrent$AirCurrentSegment")
    public static abstract class AirCurrentSegmentMixin implements AirCurrentSegmentAccess {
        @Shadow
        private @Nullable FanProcessingType type;
        @Unique
        @Nullable
        private Object particleData;

        @Override
        @Accessor
        public abstract @Nullable FanProcessingType getType();

        @Override
        @Accessor
        public abstract int getStartOffset();

        @Override
        @Accessor
        public abstract int getEndOffset();

        @SuppressWarnings("unchecked")
        @Override
        public <T> @Nullable T getParticleData(DynamicParticleFanProcessingType<T> type) {
            if (this.type == type)
                return (T) particleData;
            return null;
        }

        @Override
        public <T> void setParticleData(DynamicParticleFanProcessingType<T> type, @Nullable T particleData) {
            if (this.type == type)
                this.particleData = particleData;
        }
    }
}
