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

package plus.dragons.createdragonsplus.common.kinetics.fan.coloring;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;

public class ColoringRecipe extends ProcessingRecipe<ColoringRecipeInput, ColoringRecipeParams> {
    public ColoringRecipe(ColoringRecipeParams params) {
        super(CDPRecipes.COLORING, params);
    }

    public static Builder builder(ResourceLocation id, DyeColor color) {
        return new Builder(id, color);
    }

    public DyeColor getColor() {
        return params.color;
    }

    @Override
    public boolean matches(ColoringRecipeInput input, Level level) {
        return params.color == input.color() && this.ingredients.getFirst().test(input.item());
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 12;
    }

    public static class Builder extends ProcessingRecipeBuilder<ColoringRecipeParams, ColoringRecipe, Builder> {
        protected Builder(ResourceLocation recipeId, DyeColor color) {
            super(ColoringRecipe::new, recipeId);
            this.params.color = color;
        }

        @Override
        protected ColoringRecipeParams createParams() {
            return new ColoringRecipeParams();
        }

        @Override
        public Builder self() {
            return this;
        }
    }

    public static class Serializer<R extends ColoringRecipe> implements RecipeSerializer<R> {
        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

        public Serializer(ProcessingRecipe.Factory<ColoringRecipeParams, R> factory) {
            this.codec = ProcessingRecipe.codec(factory, ColoringRecipeParams.CODEC);
            this.streamCodec = ProcessingRecipe.streamCodec(factory, ColoringRecipeParams.STREAM_CODEC);
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
}
