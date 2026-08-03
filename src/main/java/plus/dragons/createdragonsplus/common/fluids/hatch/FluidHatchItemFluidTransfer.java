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
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class FluidHatchItemFluidTransfer {
    public static TransferResult tryDrainItemToTank(ItemStack stack, IFluidHandler tankCapability, FilteringBehaviour filter) {
        IFluidHandlerItem itemCapability = getItemFluidHandler(stack, false);
        if (itemCapability == null)
            return TransferResult.EMPTY;

        for (int i = 0; i < itemCapability.getTanks(); i++) {
            FluidStack storedFluid = itemCapability.getFluidInTank(i);
            if (storedFluid.isEmpty() || !filter.test(storedFluid))
                continue;

            FluidStack fluidToMove = getDrainableFluid(itemCapability, tankCapability, storedFluid);
            if (fluidToMove.isEmpty())
                continue;

            FluidStack movedFluid = FluidUtil.tryFluidTransfer(tankCapability, itemCapability, fluidToMove, true);
            if (movedFluid.isEmpty())
                continue;

            stack.shrink(1);
            return new TransferResult(movedFluid, itemCapability.getContainer().copy());
        }
        return TransferResult.EMPTY;
    }

    public static TransferResult tryFillItemFromTank(ItemStack stack, IFluidHandler tankCapability, FilteringBehaviour filter) {
        IFluidHandlerItem itemCapability = getItemFluidHandler(stack, true);
        if (itemCapability == null)
            return TransferResult.EMPTY;

        for (int i = 0; i < tankCapability.getTanks(); i++) {
            FluidStack storedFluid = tankCapability.getFluidInTank(i);
            if (storedFluid.isEmpty() || !filter.test(storedFluid))
                continue;

            FluidStack fluidToMove = getFillableFluid(itemCapability, tankCapability, storedFluid);
            if (fluidToMove.isEmpty())
                continue;

            FluidStack movedFluid = FluidUtil.tryFluidTransfer(itemCapability, tankCapability, fluidToMove, true);
            if (movedFluid.isEmpty())
                continue;

            stack.shrink(1);
            return new TransferResult(movedFluid, itemCapability.getContainer().copy());
        }
        return TransferResult.EMPTY;
    }

    public static boolean canItemBeFilled(ItemStack stack) {
        IFluidHandlerItem itemCapability = getItemFluidHandler(stack, true);
        if (itemCapability == null)
            return false;
        for (int i = 0; i < itemCapability.getTanks(); i++) {
            if (itemCapability.getFluidInTank(i).getAmount() < itemCapability.getTankCapacity(i))
                return true;
        }
        return false;
    }

    public static boolean canItemBeEmptied(ItemStack stack) {
        IFluidHandlerItem itemCapability = getItemFluidHandler(stack, false);
        if (itemCapability == null)
            return false;
        for (int i = 0; i < itemCapability.getTanks(); i++) {
            if (!itemCapability.getFluidInTank(i).isEmpty())
                return true;
        }
        return false;
    }

    private static FluidStack getDrainableFluid(
            IFluidHandlerItem itemCapability, IFluidHandler tankCapability, FluidStack storedFluid) {
        FluidStack availableFluid = storedFluid.copy();
        int acceptableAmount = tankCapability.fill(availableFluid, FluidAction.SIMULATE);
        if (acceptableAmount <= 0)
            return FluidStack.EMPTY;

        FluidStack requestedFluid = storedFluid.copy();
        requestedFluid.setAmount(acceptableAmount);
        FluidStack drainableFluid = itemCapability.drain(requestedFluid, FluidAction.SIMULATE);
        if (drainableFluid.isEmpty())
            return FluidStack.EMPTY;

        int realAcceptableAmount = tankCapability.fill(drainableFluid.copy(), FluidAction.SIMULATE);
        if (realAcceptableAmount <= 0)
            return FluidStack.EMPTY;
        if (realAcceptableAmount < drainableFluid.getAmount())
            drainableFluid.setAmount(realAcceptableAmount);
        return drainableFluid;
    }

    private static FluidStack getFillableFluid(
            IFluidHandlerItem itemCapability, IFluidHandler tankCapability, FluidStack storedFluid) {
        FluidStack availableFluid = storedFluid.copy();
        int fillableAmount = itemCapability.fill(availableFluid, FluidAction.SIMULATE);
        if (fillableAmount <= 0)
            return FluidStack.EMPTY;

        FluidStack requestedFluid = storedFluid.copy();
        requestedFluid.setAmount(fillableAmount);
        FluidStack drainableFluid = tankCapability.drain(requestedFluid, FluidAction.SIMULATE);
        if (drainableFluid.isEmpty())
            return FluidStack.EMPTY;

        int realFillableAmount = itemCapability.fill(drainableFluid.copy(), FluidAction.SIMULATE);
        if (realFillableAmount <= 0)
            return FluidStack.EMPTY;
        if (realFillableAmount < drainableFluid.getAmount())
            drainableFluid.setAmount(realFillableAmount);
        return drainableFluid;
    }

    private static IFluidHandlerItem getItemFluidHandler(ItemStack stack, boolean forFilling) {
        ItemStack split = stack.copy();
        split.setCount(1);
        IFluidHandlerItem itemCapability = split.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if (itemCapability == null)
            return null;
        if (forFilling && !GenericItemFilling.isFluidHandlerValid(split, itemCapability))
            return null;
        return itemCapability;
    }

    public record TransferResult(FluidStack fluidStack, ItemStack result) {
        public static final TransferResult EMPTY = new TransferResult(FluidStack.EMPTY, ItemStack.EMPTY);

        public boolean isEmpty() {
            return fluidStack.isEmpty();
        }
    }
}
