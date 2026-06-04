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

package plus.dragons.createdragonsplus.common.fluids.dye;

import com.tterrag.registrate.builders.FluidBuilder.FluidTypeFactory;
import java.util.function.Supplier;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3f;
import plus.dragons.createdragonsplus.common.fluids.SolidRenderFluidType;
import plus.dragons.createdragonsplus.config.CDPConfig;

public final class DyeFluidType extends SolidRenderFluidType {
    private final DyeVariant variant;

    private DyeFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, int tintColor, Vector3f fogColor, Supplier<Float> fogDistanceModifier, DyeVariant variant) {
        super(properties, stillTexture, flowingTexture, tintColor, fogColor, fogDistanceModifier);
        this.variant = variant;
    }

    public static FluidTypeFactory create(DyeVariant variant) {
        int rgbColor = variant.color();
        int tintColor = FastColor.ARGB32.opaque(rgbColor);
        Vector3f fogColor = new Color(rgbColor, false).asVectorF();
        return (properties, stillTexture, flowingTexture) -> new DyeFluidType(properties,
                stillTexture,
                flowingTexture,
                tintColor,
                fogColor,
                DyeFluidType::getVisibility,
                variant);
    }

    private static float getVisibility() {
        return CDPConfig.client().dyeVisionMultiplier.getF() / 256;
    }

    public DyeVariant getVariant() {
        return this.variant;
    }

    @Override
    public boolean isVaporizedOnPlacement(Level level, BlockPos pos, FluidStack stack) {
        return level.dimensionType().ultraWarm();
    }
}
