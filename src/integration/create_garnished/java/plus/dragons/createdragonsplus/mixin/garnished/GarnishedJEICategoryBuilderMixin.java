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

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import java.util.List;
import java.util.function.Consumer;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.integration.ModIntegration.Constants;

@Restriction(require = @Condition(Constants.CREATE_GARNISHED))
@Mixin(targets = "net.dakotapride.garnished.registry.JEI.GarnishedJEI$CategoryBuilder", remap = false)
public abstract class GarnishedJEICategoryBuilderMixin<T extends Recipe<?>> {
    @Shadow
    @Final
    private List<Consumer<List<T>>> recipeListConsumers;

    @Inject(method = "build", at = @At("HEAD"))
    private void removeDuplicatedFanRecipes(String name, CreateRecipeCategory.Factory<T> factory,
            CallbackInfoReturnable<CreateRecipeCategory<T>> cir) {
        boolean freezing = name.equals("garnished.fan_freezing") && CDPConfig.recipes().enableBulkFreezing.get();
        boolean coloring = name.endsWith("_dye_blowing") && CDPConfig.recipes().enableBulkColoring.get();
        if (freezing || coloring)
            recipeListConsumers.clear();
    }
}
