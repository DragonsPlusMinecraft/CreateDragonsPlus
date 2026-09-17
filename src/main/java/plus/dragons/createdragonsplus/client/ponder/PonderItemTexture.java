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

package plus.dragons.createdragonsplus.client.ponder;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

/**
 * Composites arbitrary item render types, including opaque block-entity models, with GUI alpha.
 * One small scratch target is shared by all cards. Ponder also renders screens from
 * its navigation history after removed(), so the target follows the client lifetime.
 */
final class PonderItemTexture {
    private static final int SIZE = 32;
    private static final int PADDING = 4;
    private @Nullable TextureTarget target;

    void render(GuiGraphics graphics, ItemStack stack, float x, float y, float z, float alpha) {
        graphics.flush();
        int framebuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        boolean scissor = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        int srcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        int dstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        int srcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        int dstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        var color = RenderSystem.getShaderColor().clone();
        var shader = RenderSystem.getShader();
        int texture = RenderSystem.getShaderTexture(0);
        var projection = new Matrix4f(RenderSystem.getProjectionMatrix());
        var sorting = RenderSystem.getVertexSorting();
        var modelView = RenderSystem.getModelViewStack();
        try {
            modelView.pushMatrix();
            try {
                int resolution = SIZE * (int) Math.ceil(Minecraft.getInstance().getWindow().getGuiScale());
                if (target == null || target.width != resolution) {
                    close();
                    target = new TextureTarget(resolution, resolution, true, Minecraft.ON_OSX);
                    target.setClearColor(0, 0, 0, 0);
                }
                GlStateManager._disableScissorTest();
                RenderSystem.depthMask(true);
                target.clear(Minecraft.ON_OSX);
                target.bindWrite(true);
                modelView.identity();
                RenderSystem.applyModelViewMatrix();
                RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, SIZE, SIZE, 0, -1000, 1000),
                        VertexSorting.ORTHOGRAPHIC_Z);
                RenderSystem.setShaderColor(1, 1, 1, 1);
                var isolated = new GuiGraphics(Minecraft.getInstance(), graphics.bufferSource());
                isolated.pose().translate(PADDING, PADDING, 0);
                isolated.pose().scale(1.5f, 1.5f, 1.5f);
                isolated.renderFakeItem(stack, 0, 0);
                isolated.flush();
            } finally {
                modelView.popMatrix();
                RenderSystem.applyModelViewMatrix();
                RenderSystem.setProjectionMatrix(projection, sorting);
                GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
                RenderSystem.viewport(viewport[0], viewport[1], viewport[2], viewport[3]);
                if (scissor)
                    GlStateManager._enableScissorTest();
            }
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, target.getColorTextureId());
            // The transparent render target contains premultiplied colour. Fade RGB
            // together with alpha, then composite once instead of fading model faces.
            RenderSystem.setShaderColor(alpha, alpha, alpha, alpha);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(false);
            var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            var pose = graphics.pose().last().pose();
            float left = x - PADDING;
            float top = y - PADDING;
            buffer.addVertex(pose, left, top, z).setUv(0, 1).setColor(-1);
            buffer.addVertex(pose, left, top + SIZE, z).setUv(0, 0).setColor(-1);
            buffer.addVertex(pose, left + SIZE, top + SIZE, z).setUv(1, 0).setColor(-1);
            buffer.addVertex(pose, left + SIZE, top, z).setUv(1, 1).setColor(-1);
            BufferUploader.drawWithShader(buffer.buildOrThrow());
        } finally {
            RenderSystem.depthMask(depthMask);
            RenderSystem.blendFuncSeparate(srcRgb, dstRgb, srcAlpha, dstAlpha);
            if (!blend)
                RenderSystem.disableBlend();
            RenderSystem.setShaderColor(color[0], color[1], color[2], color[3]);
            RenderSystem.setShader(() -> shader);
            RenderSystem.setShaderTexture(0, texture);
        }
    }

    private void close() {
        if (target != null) {
            target.destroyBuffers();
            target = null;
        }
    }
}
