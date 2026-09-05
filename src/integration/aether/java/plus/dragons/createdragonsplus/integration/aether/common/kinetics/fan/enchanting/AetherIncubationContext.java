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

package plus.dragons.createdragonsplus.integration.aether.common.kinetics.fan.enchanting;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class AetherIncubationContext {
    private static final ThreadLocal<Vec3> TRANSPORTED_ITEM_POSITION = new ThreadLocal<>();

    public static void setTransportedItemPosition(Vec3 position) {
        TRANSPORTED_ITEM_POSITION.set(position);
    }

    public static @Nullable Vec3 transportedItemPosition() {
        return TRANSPORTED_ITEM_POSITION.get();
    }

    public static void clearTransportedItemPosition() {
        TRANSPORTED_ITEM_POSITION.remove();
    }
}
