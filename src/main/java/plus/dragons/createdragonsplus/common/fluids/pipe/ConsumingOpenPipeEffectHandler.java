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

package plus.dragons.createdragonsplus.common.fluids.pipe;

import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import com.simibubi.create.content.fluids.OpenEndedPipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface ConsumingOpenPipeEffectHandler extends OpenPipeEffectHandler {
    int consume(Level level, AABB aoe, FluidStack contained);

    @Internal
    static FluidStack getRemainder(ConsumingOpenPipeEffectHandler handler, OpenEndedPipe pipe, FluidStack fluid) {
        int contained = fluid.getAmount();
        int consumed = handler.consume(pipe.getWorld(), pipe.getAOE(), fluid.copy());
        if (consumed < 0) {
            throw new IllegalStateException(
                    "Can not handle open pipe effect at %s %s, "
                            .formatted(pipe.getWorld(), pipe.getPos()) +
                            "[%s] returned illegal negative consumed effect amount: %s"
                                    .formatted(handler.getClass().getName(), consumed));
        } else if (consumed > contained) {
            throw new IllegalStateException(
                    "Can not handle open pipe effect at %s %s, "
                            .formatted(pipe.getWorld(), pipe.getPos()) +
                            "[%s] returned illegal consumed effect amount: %s, "
                                    .formatted(handler.getClass().getName(), consumed)
                            +
                            "exceeding contained effect amount: %s"
                                    .formatted(contained));
        }
        return fluid.copyWithAmount(contained - consumed);
    }
}
