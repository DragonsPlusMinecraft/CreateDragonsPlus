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

package plus.dragons.createdragonsplus.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import plus.dragons.createdragonsplus.data.recipe.integration.IntegrationIngredient;

@Mixin(Ingredient.Value.class)
public interface IngredientValueMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/MapCodec;xmap(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/MapCodec;"))
    private static MapCodec<Ingredient.Value> clinit$addIntegrationValueCodec(MapCodec<Ingredient.Value> codec) {
        if (DatagenModLoader.isRunningDataGen()) {
            return Codec.mapEither(IntegrationIngredient.Value.MAP_CODEC, codec).xmap(
                    either -> either.map(Function.identity(), Function.identity()),
                    value -> value instanceof IntegrationIngredient.Value integrationValue
                            ? Either.left(integrationValue)
                            : Either.right(value)
            );
        }
        return codec;
    }
}
