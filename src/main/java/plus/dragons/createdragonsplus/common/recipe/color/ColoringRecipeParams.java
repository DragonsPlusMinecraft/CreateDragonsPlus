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

package plus.dragons.createdragonsplus.common.recipe.color;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import plus.dragons.createdragonsplus.common.recipe.CustomProcessingRecipeParams;
import plus.dragons.createdragonsplus.util.FieldsNullabilityUnknownByDefault;

@FieldsNullabilityUnknownByDefault
public class ColoringRecipeParams extends CustomProcessingRecipeParams {
    public static final MapCodec<ColoringRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(ColoringRecipeParams::new).forGetter(Function.identity()),
            DyeColor.CODEC.fieldOf("color").forGetter(ColoringRecipeParams::getColor)
    ).apply(instance, ColoringRecipeParams::setColor));
    public static final StreamCodec<RegistryFriendlyByteBuf, ColoringRecipeParams> STREAM_CODEC =
            streamCodec(ColoringRecipeParams::new);
    protected DyeColor color;

    protected ColoringRecipeParams(ResourceLocation id) {
        super(id);
    }

    public ColoringRecipeParams(ResourceLocation id, DyeColor color) {
        super(id);
        this.color = color;
    }

    protected DyeColor getColor() {
        return color;
    }

    protected ColoringRecipeParams setColor(DyeColor color) {
        this.color = color;
        return this;
    }

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        DyeColor.STREAM_CODEC.encode(buffer, color);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        color = DyeColor.STREAM_CODEC.decode(buffer);
    }
}
