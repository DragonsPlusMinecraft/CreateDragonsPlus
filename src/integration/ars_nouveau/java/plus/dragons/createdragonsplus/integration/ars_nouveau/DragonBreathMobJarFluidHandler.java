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

package plus.dragons.createdragonsplus.integration.ars_nouveau;

import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.config.CDPConfig;

public class DragonBreathMobJarFluidHandler implements IFluidHandler {
    public static final int CAPACITY = 10_000;
    private static final String AMOUNT_KEY = CDPCommon.ID + ":dragon_breath_amount";
    private final MobJarTile tile;

    public DragonBreathMobJarFluidHandler(MobJarTile tile) {
        this.tile = tile;
    }

    public boolean isActive() {
        return CDPConfig.features().dragonBreathFluid.get() && tile.getEntity() instanceof EnderDragon;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        int amount = tank == 0 && isActive() ? getAmount() : 0;
        return amount == 0 ? FluidStack.EMPTY : new FluidStack((Fluid) CDPFluids.DRAGON_BREATH.getSource(), amount);
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? CAPACITY : 0;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return tank == 0 && isActive() && isDragonBreath(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (!isActive() || !isDragonBreath(resource))
            return 0;
        int stored = getAmount();
        int filled = Math.min(resource.getAmount(), CAPACITY - stored);
        if (filled > 0 && action.execute())
            setAmount(stored + filled);
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (!isActive() || !isDragonBreath(resource))
            return FluidStack.EMPTY;
        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (!isActive() || maxDrain <= 0)
            return FluidStack.EMPTY;
        int stored = getAmount();
        int drained = Math.min(maxDrain, stored);
        if (drained <= 0)
            return FluidStack.EMPTY;
        if (action.execute())
            setAmount(stored - drained);
        return new FluidStack((Fluid) CDPFluids.DRAGON_BREATH.getSource(), drained);
    }

    private int getAmount() {
        return Mth.clamp(tile.getExtraDataTag().getInt(AMOUNT_KEY), 0, CAPACITY);
    }

    private void setAmount(int amount) {
        var tag = tile.getExtraDataTag().copy();
        int clamped = Mth.clamp(amount, 0, CAPACITY);
        if (clamped == 0)
            tag.remove(AMOUNT_KEY);
        else
            tag.putInt(AMOUNT_KEY, clamped);
        tile.setExtraDataTag(tag);
        tile.updateBlock();
    }

    private static boolean isDragonBreath(FluidStack stack) {
        return !stack.isEmpty() && stack.getAmount() > 0 && stack.getFluid() == CDPFluids.DRAGON_BREATH.getSource();
    }
}
