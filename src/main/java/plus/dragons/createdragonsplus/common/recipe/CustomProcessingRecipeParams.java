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

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import java.util.function.Function;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

public abstract class CustomProcessingRecipeParams extends ProcessingRecipeParams {
    protected CustomProcessingRecipeParams(ResourceLocation id) {
        super(id);
    }

    public ResourceLocation getId() {
        return id;
    }

    protected abstract int getMaxInputCount();

    protected abstract int getMaxOutputCount();

    protected int getMaxFluidInputCount() {
        return 0;
    }

    protected int getMaxFluidOutputCount() {
        return 0;
    }

    protected boolean canRequireHeat() {
        return false;
    }

    protected boolean canSpecifyDuration() {
        return false;
    }

    protected boolean canKeepHeldItem() {
        return false;
    }

    protected void toNetwork(RegistryFriendlyByteBuf buffer) {
        ResourceLocation.STREAM_CODEC.encode(buffer, id);
        CatnipStreamCodecBuilders.nonNullList(Ingredient.CONTENTS_STREAM_CODEC).encode(buffer, ingredients);
        CatnipStreamCodecBuilders.nonNullList(ProcessingOutput.STREAM_CODEC).encode(buffer, results);
        if (getMaxFluidInputCount() > 0)
            CatnipStreamCodecBuilders.nonNullList(FluidIngredient.STREAM_CODEC).encode(buffer, fluidIngredients);
        if (getMaxFluidOutputCount() > 0)
            CatnipStreamCodecBuilders.nonNullList(FluidStack.STREAM_CODEC).encode(buffer, fluidResults);
        if (canSpecifyDuration())
            ByteBufCodecs.VAR_INT.encode(buffer, processingDuration);
        if (canRequireHeat())
            HeatCondition.STREAM_CODEC.encode(buffer, requiredHeat);
        if (canKeepHeldItem())
            ByteBufCodecs.BOOL.encode(buffer, keepHeldItem);
    }

    protected void fromNetwork(RegistryFriendlyByteBuf buffer) {
        ingredients = CatnipStreamCodecBuilders.nonNullList(Ingredient.CONTENTS_STREAM_CODEC).decode(buffer);
        results = CatnipStreamCodecBuilders.nonNullList(ProcessingOutput.STREAM_CODEC).decode(buffer);
        if (getMaxFluidInputCount() > 0)
            fluidIngredients = CatnipStreamCodecBuilders.nonNullList(FluidIngredient.STREAM_CODEC).decode(buffer);
        if (getMaxFluidOutputCount() > 0)
            fluidResults = CatnipStreamCodecBuilders.nonNullList(FluidStack.STREAM_CODEC).decode(buffer);
        if (canSpecifyDuration())
            processingDuration = ByteBufCodecs.VAR_INT.decode(buffer);
        if (canRequireHeat())
            requiredHeat = HeatCondition.STREAM_CODEC.decode(buffer);
        if (canKeepHeldItem())
            keepHeldItem = ByteBufCodecs.BOOL.decode(buffer);
    }

    protected static <P extends CustomProcessingRecipeParams> MapCodec<P> itemCodec(Function<ResourceLocation, P> constructor) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(params -> params.id),
                NonNullList.codecOf(Ingredient.CODEC).fieldOf("ingredients").forGetter(params -> params.ingredients),
                NonNullList.codecOf(ProcessingOutput.CODEC).fieldOf("results").forGetter(params -> params.results)
        ).apply(instance, (id, ingredients, results) -> {
            var params = constructor.apply(id);
            params.ingredients = ingredients;
            params.results = results;
            return params;
        }));
    }

    protected static <P extends CustomProcessingRecipeParams> MapCodec<P> fluidInputCodec(Function<ResourceLocation, P> constructor) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(params -> params.id),
                Codec.either(Ingredient.CODEC, FluidIngredient.CODEC).listOf().fieldOf("ingredients").forGetter(params -> {
                    ImmutableList.Builder<Either<Ingredient, FluidIngredient>> builder = ImmutableList.builder();
                    params.ingredients.forEach(it -> builder.add(Either.left(it)));
                    params.fluidIngredients.forEach(it -> builder.add(Either.right(it)));
                    return builder.build();
                }),
                NonNullList.codecOf(ProcessingOutput.CODEC).fieldOf("results").forGetter(params -> params.results)
        ).apply(instance, (id, ingredients, results) -> {
            var params = constructor.apply(id);
            for (var ingredient : ingredients)
                ingredient.ifLeft(params.ingredients::add).ifRight(params.fluidIngredients::add);
            params.results = results;
            return params;
        }));
    }

    protected static <P extends CustomProcessingRecipeParams> MapCodec<P> fluidOutputCodec(Function<ResourceLocation, P> constructor) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(params -> params.id),
                NonNullList.codecOf(Ingredient.CODEC).fieldOf("ingredients").forGetter(params -> params.ingredients),
                NonNullList.codecOf(ProcessingOutput.CODEC).fieldOf("results").forGetter(params -> params.results)
        ).apply(instance, (id, ingredients, results) -> {
            var params = constructor.apply(id);
            params.ingredients = ingredients;
            params.results = results;
            return params;
        }));
    }

    protected static <P extends CustomProcessingRecipeParams> MapCodec<P> fluidCodec(Function<ResourceLocation, P> constructor) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(params -> params.id),
                Codec.either(Ingredient.CODEC, FluidIngredient.CODEC).listOf().fieldOf("ingredients").forGetter(params -> {
                    ImmutableList.Builder<Either<Ingredient, FluidIngredient>> builder = ImmutableList.builder();
                    params.ingredients.forEach(it -> builder.add(Either.left(it)));
                    params.fluidIngredients.forEach(it -> builder.add(Either.right(it)));
                    return builder.build();
                }),
                Codec.either(ProcessingOutput.CODEC, FluidStack.CODEC).listOf().fieldOf("results").forGetter(params -> {
                    ImmutableList.Builder<Either<ProcessingOutput, FluidStack>> builder = ImmutableList.builder();
                    params.results.forEach(it -> builder.add(Either.left(it)));
                    params.fluidResults.forEach(it -> builder.add(Either.right(it)));
                    return builder.build();
                })
        ).apply(instance, (id, ingredients, results) -> {
            var params = constructor.apply(id);
            for (var ingredient : ingredients)
                ingredient.ifLeft(params.ingredients::add).ifRight(params.fluidIngredients::add);
            for (var result : results)
                result.ifLeft(params.results::add).ifRight(params.fluidResults::add);
            return params;
        }));
    }

    protected static <P extends CustomProcessingRecipeParams> MapCodec<P> completeCodec(Function<ResourceLocation, P> constructor) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(params -> params.id),
                Codec.either(Ingredient.CODEC, FluidIngredient.CODEC).listOf().fieldOf("ingredients").forGetter(params -> {
                    ImmutableList.Builder<Either<Ingredient, FluidIngredient>> builder = ImmutableList.builder();
                    params.ingredients.forEach(it -> builder.add(Either.left(it)));
                    params.fluidIngredients.forEach(it -> builder.add(Either.right(it)));
                    return builder.build();
                }),
                Codec.either(ProcessingOutput.CODEC, FluidStack.CODEC).listOf().fieldOf("results").forGetter(params -> {
                    ImmutableList.Builder<Either<ProcessingOutput, FluidStack>> builder = ImmutableList.builder();
                    params.results.forEach(it -> builder.add(Either.left(it)));
                    params.fluidResults.forEach(it -> builder.add(Either.right(it)));
                    return builder.build();
                }),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("processing_time", 0)
                        .forGetter(params -> params.processingDuration),
                HeatCondition.CODEC.optionalFieldOf("heat_requirement", HeatCondition.NONE)
                        .forGetter(params -> params.requiredHeat),
                Codec.BOOL.optionalFieldOf("keep_held_item", false).forGetter(params -> params.keepHeldItem)
        ).apply(instance, (id, ingredients, results, processingDuration, requiredHeat, keepHeldItem) -> {
            var params = constructor.apply(id);
            for (var ingredient : ingredients)
                ingredient.ifLeft(params.ingredients::add).ifRight(params.fluidIngredients::add);
            for (var result : results)
                result.ifLeft(params.results::add).ifRight(params.fluidResults::add);
            params.processingDuration = processingDuration;
            params.requiredHeat = requiredHeat;
            params.keepHeldItem = keepHeldItem;
            return params;
        }));
    }

    protected static <P extends CustomProcessingRecipeParams> StreamCodec<RegistryFriendlyByteBuf, P> streamCodec(Function<ResourceLocation, P> constructor) {
        return StreamCodec.of(
                (buffer, params) -> params.toNetwork(buffer),
                buffer -> Util.make(constructor.apply(ResourceLocation.STREAM_CODEC.decode(buffer)), params -> params.fromNetwork(buffer))
        );
    }
}
