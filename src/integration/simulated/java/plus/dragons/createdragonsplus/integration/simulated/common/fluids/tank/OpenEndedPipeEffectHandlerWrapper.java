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

package plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank;

import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3d;

/**
 * Wrap OpenEndedPipeEffectHandler to FragileFluidTankBreakEffectHandler. <p>
 * Only suitable for specific type of fluid. Proceed with caution.
 */
public class OpenEndedPipeEffectHandlerWrapper extends DefaultRangedEffectHandler {
    private final OpenPipeEffectHandler handler;

    public static OpenEndedPipeEffectHandlerWrapper of(OpenPipeEffectHandler handler) {
        return new OpenEndedPipeEffectHandlerWrapper(handler);
    }

    private OpenEndedPipeEffectHandlerWrapper(OpenPipeEffectHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onHit(Level level, AABB aabb, Vector3d hitPos, FluidStack fluid) {
        handler.apply(level, aabb, fluid);
    }
}
