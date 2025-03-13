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

package plus.dragons.createdragonsplus.common.recipe;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CustomProcessingRecipeSerializer<P extends CustomProcessingRecipeParams, R extends CustomProcessingRecipe<?, P>> implements RecipeSerializer<R> {
    private final MapCodec<R> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

    public CustomProcessingRecipeSerializer(Function<P, R> constructor, MapCodec<P> paramsCodec, StreamCodec<RegistryFriendlyByteBuf, P> paramsStreamCodec) {
        this.codec = paramsCodec.xmap(constructor, CustomProcessingRecipe::getParams);
        this.streamCodec = paramsStreamCodec.map(constructor, CustomProcessingRecipe::getParams);
    }

    @Override
    public MapCodec<R> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
        return streamCodec;
    }
}
