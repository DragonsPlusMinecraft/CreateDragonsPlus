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

package plus.dragons.createdragonsplus.common.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class StandardDispenserBehaviour extends DefaultDispenseItemBehavior {
    public static StandardDispenserBehaviour INSTANCE = new StandardDispenserBehaviour();

    private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

    public ItemStack execute(BlockSource source, ItemStack itemStack) {
        DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem) itemStack.getItem();
        BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
        Level level = source.level();
        if (dispensiblecontaineritem.emptyContents(null, level, blockpos, null, itemStack)) {
            dispensiblecontaineritem.checkExtraContent(null, level, itemStack, blockpos);
            return this.consumeWithRemainder(source, itemStack, new ItemStack(Items.BUCKET));
        } else {
            return this.defaultDispenseItemBehavior.dispense(source, itemStack);
        }
    }
}
