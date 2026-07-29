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

import dev.ryanhcode.sable.platform.SableEventPlatform;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import plus.dragons.createdragonsplus.integration.simulated.api.fluids.tank.FragileFluidTankBreakEffectHandler;
import plus.dragons.createdragonsplus.integration.simulated.api.fluids.tank.FragileFluidTankImpactContext;

public final class FragileFluidTankImpactDispatcher {
    private static final Map<ServerLevel, Deque<PendingImpact>> PENDING = new IdentityHashMap<>();
    private static boolean registered;

    private FragileFluidTankImpactDispatcher() {}

    public static void register() {
        if (registered)
            return;
        registered = true;
        SableEventPlatform.INSTANCE.onPostPhysicsTick(FragileFluidTankImpactDispatcher::postPhysicsTick);
    }

    static void enqueue(ServerLevel level, FragileFluidTankBreakEffectHandler handler, FragileFluidTankImpactContext context) {
        PENDING.computeIfAbsent(level, $ -> new ArrayDeque<>()).addLast(new PendingImpact(handler, context));
    }

    private static void postPhysicsTick(SubLevelPhysicsSystem physicsSystem, double timeStep) {
        var pending = PENDING.remove(physicsSystem.getLevel());
        if (pending == null)
            return;
        pending.forEach(PendingImpact::apply);
    }

    private record PendingImpact(FragileFluidTankBreakEffectHandler handler, FragileFluidTankImpactContext context) {
        private void apply() {
            handler.apply(context);
        }
    }
}
