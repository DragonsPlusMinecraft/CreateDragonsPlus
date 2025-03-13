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
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import plus.dragons.createdragonsplus.mixin.accessor.ProcessingRecipeAccessor;

public abstract class CustomProcessingRecipe<I extends RecipeInput, P extends CustomProcessingRecipeParams> extends ProcessingRecipe<I> {
    protected final P params;

    public CustomProcessingRecipe(IRecipeTypeInfo typeInfo, P params) {
        super(typeInfo, params);
        this.params = params;
        ((ProcessingRecipeAccessor) this).invokeValidate(typeInfo.getId());
    }

    public static <P extends CustomProcessingRecipeParams, R extends CustomProcessingRecipe<?, P>> RecipeSerializer<R> serializer(Function<P, R> constructor, MapCodec<P> paramsCodec, StreamCodec<RegistryFriendlyByteBuf, P> paramsStreamCodec) {
        return new RecipeSerializer<>() {
            private final MapCodec<R> codec = paramsCodec
                    .xmap(constructor, CustomProcessingRecipe::getParams);
            private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec = paramsStreamCodec
                    .map(constructor, CustomProcessingRecipe::getParams);

            @Override
            public MapCodec<R> codec() {
                return codec;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
                return streamCodec;
            }
        };
    }

    protected P getParams() {
        return params;
    }

    @Override
    protected int getMaxInputCount() {
        return params.getMaxInputCount();
    }

    @Override
    protected int getMaxOutputCount() {
        return params.getMaxOutputCount();
    }

    @Override
    protected int getMaxFluidInputCount() {
        return params.getMaxFluidInputCount();
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return params.getMaxFluidOutputCount();
    }

    @Override
    protected boolean canSpecifyDuration() {
        return params.canSpecifyDuration();
    }

    @Override
    protected boolean canRequireHeat() {
        return params.canRequireHeat();
    }

    @Override
    @Deprecated
    public final AllRecipeTypes getRecipeType() {
        throw new UnsupportedOperationException("Not a Create recipe");
    }

    @Override
    @Deprecated
    public final void writeAdditional(FriendlyByteBuf buffer) {}

    @Override
    @Deprecated
    public final void readAdditional(FriendlyByteBuf buffer) {}
}
