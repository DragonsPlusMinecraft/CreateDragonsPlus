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

package plus.dragons.createdragonsplus.common.fluids.dragonBreath;

import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;

public class DragonsBreathOpenPipeEffect implements OpenPipeEffectHandler {
    @Override
    public void apply(Level level, AABB area, FluidStack fluid) {
        if (level.getGameTime() % 5 != 0)
            return;
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAffectedByPotions);
        for (LivingEntity entity : entities) {
            entity.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1, false, false, false));
        }
    }
}
