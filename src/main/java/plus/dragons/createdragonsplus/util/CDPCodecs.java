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

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;

public class CDPCodecs {
    public static final MapCodec<Stat<?>> STAT = BuiltInRegistries.STAT_TYPE.byNameCodec()
            .dispatchMap(Stat::getType, CDPCodecs::stat);

    public static <T> MapCodec<Stat<T>> stat(StatType<T> type) {
        return type.getRegistry().byNameCodec().xmap(type::get, Stat::getValue).fieldOf("value");
    }
}
