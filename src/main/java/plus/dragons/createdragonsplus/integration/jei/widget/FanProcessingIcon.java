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

package plus.dragons.createdragonsplus.integration.jei.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllItems;
import mezz.jei.api.gui.drawable.IDrawable;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import plus.dragons.createdragonsplus.util.FieldsNullabilityUnknownByDefault;

@FieldsNullabilityUnknownByDefault
public abstract class FanProcessingIcon implements IDrawable {
    private ItemStack stack;

    protected abstract ItemStack getCatalyst();

    @Override
    public int getWidth() {
        return 18;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        if (stack == null) {
            stack = AllItems.PROPELLER.asStack();
        }

        RenderSystem.enableDepthTest();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 0);

        matrixStack.pushPose();
        matrixStack.translate(1, 1, 0);
        GuiGameElement.of(stack).render(graphics);
        matrixStack.popPose();

        var catalyst = this.getCatalyst();
        if (!catalyst.isEmpty()) {
            matrixStack.pushPose();
            matrixStack.translate(10, 10, 100);
            matrixStack.scale(.5f, .5f, .5f);
            GuiGameElement.of(catalyst).render(graphics);
            matrixStack.popPose();
        }

        matrixStack.popPose();
    }
}
