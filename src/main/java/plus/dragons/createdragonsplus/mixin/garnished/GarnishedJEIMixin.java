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

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.dakotapride.garnished.registry.JEI.DyeBlowingFanCategory;
import net.dakotapride.garnished.registry.JEI.FreezingFanCategory;
import net.dakotapride.garnished.registry.JEI.GarnishedJEI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.integration.ModIntegration.Constants;

@Restriction(require = @Condition(Constants.CREATE_GARNISHED))
@Mixin(GarnishedJEI.class)
public class GarnishedJEIMixin {
    @ModifyReceiver(method = "registerCategories", at = @At(value = "INVOKE", target = "Ljava/util/List;toArray(Ljava/util/function/IntFunction;)[Ljava/lang/Object;"))
    private List<CreateRecipeCategory<?>> disableCategories(List<CreateRecipeCategory<?>> categories, IntFunction<CreateRecipeCategory<?>[]> intFunction) {
        return categories.stream()
                .filter(category -> {
                    if (category instanceof FreezingFanCategory)
                        return !CDPConfig.recipes().enableBulkFreezing.get();
                    return true;
                })
                .filter(category -> {
                    if (category instanceof DyeBlowingFanCategory<?>)
                        return !CDPConfig.recipes().enableBulkColoring.get();
                    return true;
                })
                .collect(Collectors.toList());
    }
}
