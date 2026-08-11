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
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class DragonBreathMobJarCapabilityProvider implements ICapabilityProvider {
    private final DragonBreathMobJarFluidHandler handler;
    private final LazyOptional<IFluidHandler> capability;

    private DragonBreathMobJarCapabilityProvider(MobJarTile tile) {
        handler = new DragonBreathMobJarFluidHandler(tile);
        capability = LazyOptional.of(() -> handler);
    }

    public static @Nullable DragonBreathMobJarCapabilityProvider create(BlockEntity blockEntity) {
        return blockEntity instanceof MobJarTile tile ? new DragonBreathMobJarCapabilityProvider(tile) : null;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.FLUID_HANDLER && handler.isActive())
            return this.capability.cast();
        return LazyOptional.empty();
    }

    public void invalidate() {
        capability.invalidate();
    }
}
