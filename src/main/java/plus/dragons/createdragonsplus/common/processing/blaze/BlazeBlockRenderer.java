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

package plus.dragons.createdragonsplus.common.processing.blaze;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.processing.burner.BlazeBurnerRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.client.renderer.blockentity.PartialModelBlockEntityRenderer;
import plus.dragons.createdragonsplus.util.CodeReference;

@CodeReference(value = BlazeBurnerRenderer.class, source = "create", license = "mit")
public class BlazeBlockRenderer<T extends BlazeBlockEntity> extends SafeBlockEntityRenderer<T> implements PartialModelBlockEntityRenderer {
    public BlazeBlockRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(T blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        HeatLevel heatLevel = blockEntity.getHeatLevelFromBlock();
        if (heatLevel == HeatLevel.NONE)
            return;
        Level level = blockEntity.getLevel();
        assert level != null;
        float renderTime = AnimationTickHolder.getRenderTime(level);
        BlockState blockState = blockEntity.getBlockState();
        float animation = blockEntity.headAnimation.getValue(partialTicks) * .175f;
        float horizontalAngle = AngleHelper.rad(blockEntity.headAngle.getValue(partialTicks));
        boolean active = animation > 0.125f;
        int seed = blockEntity.hashCode();
        PartialModel blazeModel = BlazeBurnerRenderer.getBlazeModel(heatLevel, active);
        PartialModel hatModel = blockEntity.getHatModel(heatLevel);
        PartialModel gogglesModel = blockEntity.getGogglesModel(heatLevel);
        renderBlaze(
                blockState, heatLevel, renderTime,
                poseStack, null, bufferSource,
                light, overlay, seed,
                animation, horizontalAngle,
                active && heatLevel.isAtLeast(HeatLevel.FADING),
                blazeModel, hatModel, gogglesModel);
    }

    protected void renderGoggles(
            BlockState blockState, HeatLevel heatLevel, float renderTime,
            PoseStack poseStack, @Nullable PoseStack transformStack, MultiBufferSource bufferSource,
            int light, int overlay, int seed,
            float animation, float horizontalAngle, float headY,
            PartialModel blazeModel, PartialModel gogglesModel) {
        SuperByteBuffer gogglesBuffer = CachedBuffers.partial(gogglesModel, blockState);
        if (transformStack != null)
            gogglesBuffer.transform(transformStack);
        gogglesBuffer.translate(0, headY + .5f, 0);
        RenderType renderType = getRenderType(blockState, gogglesModel);
        draw(gogglesBuffer, horizontalAngle, poseStack, bufferSource.getBuffer(renderType));
    }

    protected void renderHat(
            BlockState blockState, HeatLevel heatLevel, float renderTime,
            PoseStack poseStack, @Nullable PoseStack transformStack, MultiBufferSource bufferSource,
            int light, int overlay, int seed,
            float animation, float horizontalAngle, float headY,
            PartialModel blazeModel, PartialModel hatModel) {
        SuperByteBuffer hatBuffer = CachedBuffers.partial(hatModel, blockState);
        if (transformStack != null)
            hatBuffer.transform(transformStack);
        hatBuffer.translate(0f, headY + .75f, 0f);
        RenderType renderType = getRenderType(blockState, hatModel);
        drawCentered(hatBuffer, horizontalAngle + Mth.PI, poseStack, bufferSource.getBuffer(renderType));
    }

    public void renderBlaze(
            BlockState blockState, HeatLevel heatLevel, float renderTime,
            PoseStack poseStack, @Nullable PoseStack transformStack, MultiBufferSource bufferSource,
            int light, int overlay, int seed,
            float animation, float horizontalAngle, boolean active,
            PartialModel blazeModel, @Nullable PartialModel hatModel, @Nullable PartialModel gogglesModel) {
        float seededRenderTime = renderTime + (seed % 13) * 16f;
        float offsetScale = heatLevel.isAtLeast(HeatLevel.FADING) ? 64 : 16;
        float offset = Mth.sin((seededRenderTime / 16f) % (2 * Mth.PI)) / offsetScale;
        float rodsOffset1 = Mth.sin((seededRenderTime / 16f + Mth.PI) % (2 * Mth.PI)) / offsetScale;
        float rodsOffset2 = Mth.sin((seededRenderTime / 16f + Mth.PI / 2) % (2 * Mth.PI)) / offsetScale;
        float headY = offset - (animation * .75f);

        poseStack.pushPose();
        // Blaze Head
        SuperByteBuffer blazeBuffer = CachedBuffers.partial(blazeModel, blockState);
        if (transformStack != null)
            blazeBuffer.transform(transformStack);
        blazeBuffer.translate(0, headY, 0);
        draw(blazeBuffer, horizontalAngle, poseStack, bufferSource.getBuffer(RenderType.solid()));
        // Goggles
        if (gogglesModel != null)
            renderGoggles(
                    blockState, heatLevel, renderTime,
                    poseStack, transformStack, bufferSource,
                    light, overlay, seed,
                    animation, horizontalAngle, headY,
                    blazeModel, gogglesModel);
        // Hat
        if (hatModel != null)
            renderHat(
                    blockState, heatLevel, renderTime,
                    poseStack, transformStack, bufferSource,
                    light, overlay, seed,
                    animation, horizontalAngle, headY,
                    blazeModel, hatModel);
        // Blaze Rods
        if (heatLevel.isAtLeast(HeatLevel.FADING)) {
            PartialModel rodsModel = heatLevel == HeatLevel.SEETHING ? AllPartialModels.BLAZE_BURNER_SUPER_RODS
                    : AllPartialModels.BLAZE_BURNER_RODS;
            PartialModel rodsModel2 = heatLevel == HeatLevel.SEETHING ? AllPartialModels.BLAZE_BURNER_SUPER_RODS_2
                    : AllPartialModels.BLAZE_BURNER_RODS_2;

            SuperByteBuffer rodsBuffer = CachedBuffers.partial(rodsModel, blockState);
            if (transformStack != null)
                rodsBuffer.transform(transformStack);
            rodsBuffer.translate(0, rodsOffset1 + animation + .125f, 0)
                    .light(LightTexture.FULL_BRIGHT)
                    .renderInto(poseStack, bufferSource.getBuffer(RenderType.solid()));

            SuperByteBuffer rodsBuffer2 = CachedBuffers.partial(rodsModel2, blockState);
            if (transformStack != null)
                rodsBuffer2.transform(transformStack);
            rodsBuffer2.translate(0, rodsOffset2 + animation - 3 / 16f, 0)
                    .light(LightTexture.FULL_BRIGHT)
                    .renderInto(poseStack, bufferSource.getBuffer(RenderType.solid()));
        }
        // Blaze Flame
        if (active) {
            SpriteShiftEntry spriteShift = heatLevel == HeatLevel.SEETHING
                    ? AllSpriteShifts.SUPER_BURNER_FLAME
                    : AllSpriteShifts.BURNER_FLAME;

            float spriteWidth = spriteShift.getTarget().getU1() - spriteShift.getTarget().getU0();

            float spriteHeight = spriteShift.getTarget().getV1() - spriteShift.getTarget().getV0();

            float speed = 1 / 32f + 1 / 64f * heatLevel.ordinal();

            float uScroll = speed * renderTime / 2;
            uScroll -= Mth.floor(uScroll);
            uScroll *= spriteWidth / 2;

            float vScroll = speed * renderTime;
            vScroll -= Mth.floor(vScroll);
            vScroll *= spriteHeight / 2;

            SuperByteBuffer flameBuffer = CachedBuffers.partial(AllPartialModels.BLAZE_BURNER_FLAME, blockState);
            if (transformStack != null)
                flameBuffer.transform(transformStack);
            flameBuffer.shiftUVScrolling(spriteShift, uScroll, vScroll);

            VertexConsumer cutout = bufferSource.getBuffer(RenderType.cutoutMipped());
            draw(flameBuffer, horizontalAngle, poseStack, cutout);
        }

        poseStack.popPose();
    }

    protected static void draw(SuperByteBuffer buffer, float horizontalAngle, PoseStack poseStack, VertexConsumer vertexConsumer) {
        buffer.rotateCentered(horizontalAngle, Direction.UP)
                .light(LightTexture.FULL_BRIGHT)
                .renderInto(poseStack, vertexConsumer);
    }

    protected static void drawCentered(SuperByteBuffer buffer, float horizontalAngle, PoseStack poseStack, VertexConsumer vertexConsumer) {
        buffer.rotateCentered(horizontalAngle, Direction.UP)
                .translate(0.5f, 0, 0.5f)
                .light(LightTexture.FULL_BRIGHT)
                .renderInto(poseStack, vertexConsumer);
    }
}
