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

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllFluids;
import com.tterrag.registrate.builders.FluidBuilder.FluidTypeFactory;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3f;
import plus.dragons.createdragonsplus.util.CodeReference;

@CodeReference(targets = "com.simibubi.create.AllFluids.SolidRenderedPlaceableFluidType", source = "create", license = "mit")
public class SolidRenderFluidType extends AllFluids.TintedFluidType {
    protected static final int NO_ALPHA = 0x00FFFFFF;
    private static final float BASE_WATER_FOG_DISTANCE = 96.0F;
    private final int tintColor;
    private final int blockTintColor;
    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final Vector3f fogColor;
    private final Supplier<Float> fogDistanceModifier;

    protected SolidRenderFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, int tintColor, Vector3f fogColor, Supplier<Float> fogDistanceModifier) {
        super(properties, stillTexture, flowingTexture);
        this.tintColor = tintColor;
        this.blockTintColor = tintColor & NO_ALPHA;
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
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
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return stillTexture;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return flowingTexture;
            }

            @Override
            public int getTintColor(FluidStack stack) {
                return SolidRenderFluidType.this.getTintColor(stack);
            }

            @Override
            public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                return SolidRenderFluidType.this.getTintColor(state, getter, pos);
            }

            @Override
            public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                Vector3f customFogColor = SolidRenderFluidType.this.getCustomFogColor();
                return customFogColor == null ? fluidFogColor : customFogColor;
            }

            @Override
            public void modifyFogRender(Camera camera, FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                float modifier = SolidRenderFluidType.this.getFogDistanceModifier();
                RenderSystem.setShaderFogShape(FogShape.CYLINDER);
                RenderSystem.setShaderFogStart(-8.0F);
                RenderSystem.setShaderFogEnd(Math.max(0.25F, BASE_WATER_FOG_DISTANCE * modifier));
            }
        });
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
