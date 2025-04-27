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

package plus.dragons.createdragonsplus.mixin.create;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import plus.dragons.createdragonsplus.common.recipe.CustomProcessingRecipe;

@Mixin(ProcessingRecipe.class)
public class ProcessingRecipeMixin {
    @WrapWithCondition(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/recipe/ProcessingRecipe;validate(Lnet/minecraft/resources/ResourceLocation;)V"))
    private boolean init$skipValidateForCustom(ProcessingRecipe<?> instance, ResourceLocation recipeTypeId) {
        //noinspection ConstantValue
        return !((Object) this instanceof CustomProcessingRecipe<?, ?>);
    }
}
