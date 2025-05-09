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

package plus.dragons.createdragonsplus.mixin.garnished;

import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.dakotapride.garnished.registry.JEI.FreezingFanCategory;
import org.spongepowered.asm.mixin.Mixin;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.integration.ModIntegration.Constants;

@Restriction(require = @Condition(Constants.CREATE_GARNISHED))
@Mixin(FreezingFanCategory.class)
public abstract class FreezingFanCategoryMixin<T extends ProcessingRecipe<?>> extends ProcessingViaFanCategory.MultiOutput<T> {
    private FreezingFanCategoryMixin(Info<T> info) {
        super(info);
    }

    @Override
    public boolean isHandled(T recipe) {
        return !CDPConfig.recipes().enableBulkFreezing.get();
    }
}
