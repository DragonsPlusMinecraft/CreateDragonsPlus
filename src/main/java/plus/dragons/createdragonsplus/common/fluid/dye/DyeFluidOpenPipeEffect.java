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

package plus.dragons.createdragonsplus.common.fluid.dye;

import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import plus.dragons.createdragonsplus.common.registry.CDPFanProcessingTypes;
import plus.dragons.createdragonsplus.mixin.accessor.FanProcessingAccessor;

public class DyeFluidOpenPipeEffect implements OpenPipeEffectHandler {
    private final DyeColor color;

    public DyeFluidOpenPipeEffect(DyeColor color) {
        this.color = color;
    }

    @Override
    public void apply(Level level, AABB area, FluidStack fluid) {
        var type = CDPFanProcessingTypes.COLORING.get(this.color).get();
        var entities = level.getEntities((Entity) null, area,
                entity -> entity instanceof ItemEntity || entity instanceof LivingEntity
        );
        for (var entity : entities) {
            if (entity instanceof ItemEntity itemEntity) {
                FanProcessingAccessor.invokeApplyProcessing(itemEntity, type);
            } else if (entity instanceof LivingEntity livingEntity) {
                type.applyColoring(livingEntity, level);
            }
        }
    }
}
