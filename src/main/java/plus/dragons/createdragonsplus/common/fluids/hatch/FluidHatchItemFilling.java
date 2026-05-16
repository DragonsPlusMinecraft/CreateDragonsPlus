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

package plus.dragons.createdragonsplus.common.fluids.hatch;

import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidHatchItemFilling {
    private static final List<Handler> EXTRA_HANDLERS = new ArrayList<>();

    public static void register(Handler handler) {
        EXTRA_HANDLERS.add(handler);
    }

    public static int getRequiredAmountForItem(Level level, ItemStack stack, FluidStack availableFluid) {
        for (var handler : EXTRA_HANDLERS) {
            var requiredAmount = handler.getRequiredAmountForItem(stack, availableFluid);
            if (requiredAmount.isPresent())
                return requiredAmount.getAsInt();
        }
        return GenericItemFilling.getRequiredAmountForItem(level, stack, availableFluid);
    }

    public static ItemStack fillItem(Level level, int requiredAmount, ItemStack stack, FluidStack availableFluid) {
        for (var handler : EXTRA_HANDLERS) {
            var result = handler.fillItem(requiredAmount, stack, availableFluid);
            if (result.isPresent())
                return result.get();
        }
        return GenericItemFilling.fillItem(level, requiredAmount, stack, availableFluid);
    }

    public interface Handler {
        OptionalInt getRequiredAmountForItem(ItemStack stack, FluidStack availableFluid);

        Optional<ItemStack> fillItem(int requiredAmount, ItemStack stack, FluidStack availableFluid);
    }
}
