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

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidType;
import plus.dragons.createdragonsplus.util.FieldsNullabilityUnknownByDefault;

@FieldsNullabilityUnknownByDefault
public class ColoringRecipeParams extends ProcessingRecipeParams {
    public static final int DYE_ITEM_FLUID_AMOUNT = 250;
    public static final int AUTOMATIC_BULK_RECIPE_INPUTS = 8;
    public static final int DEFAULT_DYE_FLUID_AMOUNT = Math.ceilDiv(DYE_ITEM_FLUID_AMOUNT, AUTOMATIC_BULK_RECIPE_INPUTS);
    public static final int MAX_DYE_FLUID_AMOUNT = FluidType.BUCKET_VOLUME;
    public static final MapCodec<ColoringRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(ColoringRecipeParams::new).forGetter(Function.identity()),
            ResourceLocation.CODEC.fieldOf("color").forGetter(ColoringRecipeParams::getColor),
            Codec.intRange(1, MAX_DYE_FLUID_AMOUNT)
                    .optionalFieldOf("dye_fluid_amount", DEFAULT_DYE_FLUID_AMOUNT)
                    .forGetter(ColoringRecipeParams::getDyeFluidAmount))
            .apply(instance, (params, color, dyeFluidAmount) -> params
                    .setColor(color)
                    .setDyeFluidAmount(dyeFluidAmount)));
    public static final StreamCodec<RegistryFriendlyByteBuf, ColoringRecipeParams> STREAM_CODEC = streamCodec(ColoringRecipeParams::new);
    protected ResourceLocation color;
    protected int dyeFluidAmount = DEFAULT_DYE_FLUID_AMOUNT;

    protected ColoringRecipeParams() {
        super();
    }

    public ColoringRecipeParams(ResourceLocation color) {
        this.color = color;
    }

    protected ResourceLocation getColor() {
        return color;
    }

    protected ColoringRecipeParams setColor(ResourceLocation color) {
        this.color = color;
        return this;
    }

    protected int getDyeFluidAmount() {
        return dyeFluidAmount;
    }

    protected ColoringRecipeParams setDyeFluidAmount(int dyeFluidAmount) {
        this.dyeFluidAmount = dyeFluidAmount;
        return this;
    }

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        ResourceLocation.STREAM_CODEC.encode(buffer, color);
        ByteBufCodecs.VAR_INT.encode(buffer, dyeFluidAmount);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        color = ResourceLocation.STREAM_CODEC.decode(buffer);
        dyeFluidAmount = ByteBufCodecs.VAR_INT.decode(buffer);
    }
}
