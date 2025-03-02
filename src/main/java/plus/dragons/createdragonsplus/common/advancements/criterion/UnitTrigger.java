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

package plus.dragons.createdragonsplus.common.advancements.criterion;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;

public class UnitTrigger extends BlockEntityBehaviourTrigger<Unit> {
    protected UnitTrigger() {
        super(Unit.CODEC);
    }

    public static UnitTrigger of() {
        return new UnitTrigger();
    }

    @Override
    protected boolean test(ServerPlayer player, SmartBlockEntity blockEntity, Unit data) {
        return true;
    }
}
