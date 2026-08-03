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

package plus.dragons.createdragonsplus.common.fluids.dye;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import plus.dragons.createdragonsplus.common.registry.CDPFanProcessingTypes;
import plus.dragons.createdragonsplus.config.CDPConfig;

public class DyeLiquidBlock extends LiquidBlock {
    private final DyeVariant variant;

    public DyeLiquidBlock(DyeVariant variant, Supplier<? extends FlowingFluid> fluid, Properties properties) {
        super(fluid, properties);
        this.variant = variant;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !level.getFluidState(pos).isSource())
            return;

        var config = CDPConfig.dyeFluid();
        var type = CDPFanProcessingTypes.COLORING.get(this.variant.id()).get();
        boolean colored = false;
        if (entity instanceof ItemEntity itemEntity) {
            if (config.dyeFluidBlockContactColorsItems.get())
                colored = type.applyContactColoring(itemEntity, level);
        } else if (entity instanceof LivingEntity livingEntity) {
            if (config.dyeFluidBlockContactColorsLivingEntities.get())
                colored = type.applyContactColoring(livingEntity, level);
        }
        if (colored && config.dyeFluidBlockContactConsumesSource.get())
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }
}
