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

package plus.dragons.createdragonsplus.common.kinetics.fan;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface DynamicParticleFanProcessingType<T> extends FanProcessingType {
    @Nullable
    T getParticleDataAt(Level level, BlockPos pos);

    void spawnProcessingParticles(Level level, Vec3 pos, @Nullable T particleData);

    void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random, @Nullable T particleData);

    default void spawnProcessingParticles(Level level, Vec3 pos) {
        spawnProcessingParticles(level, pos, null);
    }

    default void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        morphAirFlow(particleAccess, random, null);
    }
}
