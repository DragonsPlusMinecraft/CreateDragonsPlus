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

package plus.dragons.createdragonsplus.common.fluids;

import com.simibubi.create.AllFluids;
import com.tterrag.registrate.builders.FluidBuilder.FluidTypeFactory;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3f;
import plus.dragons.createdragonsplus.util.CodeReference;

@CodeReference(targets = "com.simibubi.create.AllFluids.SolidRenderedPlaceableFluidType", source = "create", license = "mit")
public class SolidRenderFluidType extends AllFluids.TintedFluidType {
    protected static final int ALPHA = 0xff00000;
    protected static final int NO_ALPHA = 0x00ffffff;
    private final int tintColor;
    private final int blockTintColor;
    private final Vector3f fogColor;
    private final Supplier<Float> fogDistanceModifier;

    protected SolidRenderFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, int tintColor, Vector3f fogColor, Supplier<Float> fogDistanceModifier) {
        super(properties, stillTexture, flowingTexture);
        this.tintColor = tintColor;
        this.blockTintColor = tintColor & NO_ALPHA;
        this.fogColor = fogColor;
        this.fogDistanceModifier = fogDistanceModifier;
    }

    public static FluidTypeFactory create(int tintColor, Vector3f fogColor, Supplier<Float> fogDistanceModifier) {
        return (properties, stillTexture, flowingTexture) -> new SolidRenderFluidType(properties,
                stillTexture,
                flowingTexture,
                tintColor,
                fogColor,
                fogDistanceModifier);
    }

    public static FluidTypeFactory create(Vector3f fogColor, Supplier<Float> fogDistanceModifier) {
        return (properties, stillTexture, flowingTexture) -> new SolidRenderFluidType(properties,
                stillTexture,
                flowingTexture,
                NO_TINT,
                fogColor,
                fogDistanceModifier);
    }

    @Override
    protected int getTintColor(FluidStack stack) {
        return this.tintColor;
    }

    @Override
    public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
        return this.blockTintColor;
    }

    @Override
    protected Vector3f getCustomFogColor() {
        return this.fogColor;
    }

    @Override
    protected float getFogDistanceModifier() {
        return this.fogDistanceModifier.get();
    }
}
