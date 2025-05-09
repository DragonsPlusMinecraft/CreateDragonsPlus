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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.fan.AirFlowParticle;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType.AirFlowParticleAccess;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import plus.dragons.createdragonsplus.common.kinetics.fan.AirCurrentAccess;
import plus.dragons.createdragonsplus.common.kinetics.fan.DynamicParticleFanProcessingType;

@Mixin(AirFlowParticle.class)
public class AirFlowParticleMixin {
    @Shadow
    @Final
    private IAirCurrentSource source;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;morphAirFlow(Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType$AirFlowParticleAccess;Lnet/minecraft/util/RandomSource;)V"))
    private void tick$morphAirFlowWithParticleData(FanProcessingType type, AirFlowParticleAccess particleAccess, RandomSource random, Operation<Void> original, @Local(name = "distance") double distance) {
        if (type instanceof DynamicParticleFanProcessingType dynamicType) {
            AirCurrentAccess airCurrent = (AirCurrentAccess) this.source.getAirCurrent();
            Object particleData;
            if (airCurrent != null) {
                var segment = airCurrent.getSegmentAccessAt((float) distance);
                if (segment != null)
                    particleData = segment.getParticleData(dynamicType);
                else
                    particleData = null;
            } else
                particleData = null;
            dynamicType.morphAirFlow(particleAccess, random, particleData);
        } else {
            original.call(type, particleAccess, random);
        }
    }
}
