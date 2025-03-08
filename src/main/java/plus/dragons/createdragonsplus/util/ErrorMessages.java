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

package plus.dragons.createdragonsplus.util;

import com.google.common.base.MoreObjects;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class ErrorMessages {
    public static String notNull(String name) {
        return name + " must not be null";
    }

    public static String level(@Nullable Level level) {
        return level == null ? "unknown" : level.dimension().toString();
    }

    public static String pos(BlockPos blockPos) {
        return "(" + blockPos.toShortString() + ")";
    }

    public static String blockEntity(BlockEntity blockEntity) {
        return MoreObjects.toStringHelper(blockEntity)
                .add("type", Util.getRegisteredName(BuiltInRegistries.BLOCK_ENTITY_TYPE, blockEntity.getType()))
                .add("level", level(blockEntity.getLevel()))
                .add("pos", pos(blockEntity.getBlockPos()))
                .omitNullValues()
                .toString();
    }
}
