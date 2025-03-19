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

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

public class CustomProcessingRecipeParams extends ProcessingRecipeParams {
    protected static final ResourceLocation DESERIALIZATION_UNKNOWN =
            ResourceLocation.withDefaultNamespace("deserialization_unknown");
    public static final MapCodec<CustomProcessingRecipeParams> CODEC = codec(CustomProcessingRecipeParams::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, CustomProcessingRecipeParams> STREAM_CODEC =
            streamCodec(CustomProcessingRecipeParams::new);

    public CustomProcessingRecipeParams(ResourceLocation id) {
        super(id);
    }

    protected static <P extends CustomProcessingRecipeParams> MapCodec<P> codec(Function<ResourceLocation, P> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.either(FluidIngredient.CODEC, Ingredient.CODEC).listOf().fieldOf("ingredients")
                        .forGetter(CustomProcessingRecipeParams::ingredients),
                Codec.either(FluidStack.CODEC, ProcessingOutput.CODEC).listOf().fieldOf("results")
                        .forGetter(CustomProcessingRecipeParams::results),
                Codec.INT.optionalFieldOf("processing_time", 0)
                        .forGetter(CustomProcessingRecipeParams::processingDuration),
                HeatCondition.CODEC.optionalFieldOf("heat_requirement", HeatCondition.NONE)
                        .forGetter(CustomProcessingRecipeParams::requiredHeat)
        ).apply(instance, (ingredients, results, processingDuration, requiredHeat) -> {
            P params = factory.apply(DESERIALIZATION_UNKNOWN);
            ingredients.forEach(either -> either
                    .ifRight(params.ingredients::add)
                    .ifLeft(params.fluidIngredients::add));
            results.forEach(either -> either
                    .ifRight(params.results::add)
                    .ifLeft(params.fluidResults::add));
            params.processingDuration = processingDuration;
            params.requiredHeat = requiredHeat;
            return params;
        }));
    }

    protected static <P extends CustomProcessingRecipeParams> StreamCodec<RegistryFriendlyByteBuf, P> streamCodec(Function<ResourceLocation, P> constructor) {
        return StreamCodec.of(
                (buffer, params) -> params.encode(buffer),
                buffer -> Util.make(constructor.apply(DESERIALIZATION_UNKNOWN), params -> params.decode(buffer))
        );
    }

    protected final List<Either<FluidIngredient, Ingredient>> ingredients() {
        List<Either<FluidIngredient, Ingredient>> ingredients =
                new ArrayList<>(this.ingredients.size() + this.fluidIngredients.size());
        this.ingredients.forEach(ingredient -> ingredients.add(Either.right(ingredient)));
        this.fluidIngredients.forEach(ingredient -> ingredients.add(Either.left(ingredient)));
        return ingredients;
    }

    protected final List<Either<FluidStack, ProcessingOutput>> results() {
        List<Either<FluidStack, ProcessingOutput>> results =
                new ArrayList<>(this.results.size() + this.fluidResults.size());
        this.results.forEach(result -> results.add(Either.right(result)));
        this.fluidResults.forEach(result -> results.add(Either.left(result)));
        return results;
    }

    protected final int processingDuration() {
        return processingDuration;
    }

    protected final HeatCondition requiredHeat() {
        return requiredHeat;
    }

    protected void encode(RegistryFriendlyByteBuf buffer) {
        CatnipStreamCodecBuilders.nonNullList(Ingredient.CONTENTS_STREAM_CODEC).encode(buffer, ingredients);
        CatnipStreamCodecBuilders.nonNullList(ProcessingOutput.STREAM_CODEC).encode(buffer, results);
        CatnipStreamCodecBuilders.nonNullList(FluidIngredient.STREAM_CODEC).encode(buffer, fluidIngredients);
        CatnipStreamCodecBuilders.nonNullList(FluidStack.STREAM_CODEC).encode(buffer, fluidResults);
        ByteBufCodecs.VAR_INT.encode(buffer, processingDuration);
        HeatCondition.STREAM_CODEC.encode(buffer, requiredHeat);
    }

    protected void decode(RegistryFriendlyByteBuf buffer) {
        ingredients = CatnipStreamCodecBuilders.nonNullList(Ingredient.CONTENTS_STREAM_CODEC).decode(buffer);
        results = CatnipStreamCodecBuilders.nonNullList(ProcessingOutput.STREAM_CODEC).decode(buffer);
        fluidIngredients = CatnipStreamCodecBuilders.nonNullList(FluidIngredient.STREAM_CODEC).decode(buffer);
        fluidResults = CatnipStreamCodecBuilders.nonNullList(FluidStack.STREAM_CODEC).decode(buffer);
        processingDuration = ByteBufCodecs.VAR_INT.decode(buffer);
        requiredHeat = HeatCondition.STREAM_CODEC.decode(buffer);
    }
}
