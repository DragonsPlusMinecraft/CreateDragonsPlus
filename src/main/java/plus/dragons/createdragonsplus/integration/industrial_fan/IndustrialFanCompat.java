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

package plus.dragons.createdragonsplus.integration.industrial_fan;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import plus.dragons.createdragonsplus.integration.ModIntegration;

public class IndustrialFanCompat {
    public static Optional<Item> INDUSTRIAL_FAN;

    public static List<Supplier<? extends ItemStack>> catalystWithIndustryFan(ItemStack fan) {
        if (INDUSTRIAL_FAN == null) {
            INDUSTRIAL_FAN = DeferredHolder.create(Registries.ITEM, ModIntegration.CREATE_DND.asResource("industrial_fan")).asOptional();
        }
        return INDUSTRIAL_FAN.<List<Supplier<? extends ItemStack>>>map(item -> List.of(() -> fan, () -> new ItemStack(item))).orElseGet(() -> List.of(() -> fan));
    }
}
