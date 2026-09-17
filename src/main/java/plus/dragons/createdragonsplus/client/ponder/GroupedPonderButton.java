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

import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.gui.element.BoxElement;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.createmod.ponder.foundation.ui.PonderButton;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import plus.dragons.createdragonsplus.util.CodeReference;

/** A single interactive front card backed by a stack of decorative cards. */
@Internal
@CodeReference(source = "Create: Central Kitchen", license = "LGPL-3.0-or-later", targets = "plus.dragons.createcentralkitchen.client.ponder.GroupedPonderButton")
public class GroupedPonderButton extends PonderButton {
    private static final int LAYER_OFFSET = 3;
    private static final PonderItemTexture ITEM_TEXTURE = new PonderItemTexture();
    private final PonderTagGroups.ResolvedGroup group;
    private final List<ItemStack> stacks;
    private final PonderGroupCycle cycle;
    private final BoxElement card = new BoxElement();

    public GroupedPonderButton(int x, int y, PonderTagGroups.ResolvedGroup group) {
        super(x, y);
        this.group = group;
        this.stacks = group.members().stream()
                .map(id -> new ItemStack(RegisteredObjectsHelper.getItemOrBlock(id))).toList();
        this.cycle = new PonderGroupCycle(stacks.size());
        updateSelection();
    }

    public ResourceLocation selectedId() {
        return group.members().get(cycle.selected());
    }

    public boolean hasScene() {
        return PonderIndex.getSceneAccess().doScenesExistForId(selectedId());
    }

    private void updateSelection() {
        item = stacks.get(cycle.selected());
        customBorder = hasScene() ? null
                : selectedId().getNamespace().equals("minecraft")
                        ? PonderUI.MISSING_VANILLA_ENTRY
                        : PonderUI.MISSING_MODDED_ENTRY;
        animateColors = hasScene();
        updateGradientFromState();
        setMessage(item.getHoverName());
    }

    @Override
    public void tick() {
        super.tick();
        int before = cycle.selected();
        cycle.tick(isHovered);
        if (before != cycle.selected())
            updateSelection();
    }

    @Override
    protected void beforeRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.beforeRender(graphics, mouseX, mouseY, partialTicks);
        if (isHovered)
            cycle.settle();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (!isVisible() || !isMouseOver(mouseX, mouseY) || deltaY == 0)
            return false;
        cycle.select(deltaY > 0 ? -1 : 1);
        updateSelection();
        return true;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return button == 0 && isVisible() && hasScene();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        cycle.settle();
        super.onClick(mouseX, mouseY);
    }

    @Override
    public void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!isVisible())
            return;
        float opacity = fade.getValue(partialTicks);
        var frame = cycle.frame(partialTicks);
        float progress = frame.progress();
        int layers = Math.min(3, stacks.size());
        float rearOffset = (layers - progress) * LAYER_OFFSET;
        renderCard(graphics, getX() + rearOffset, getY() - rearOffset,
                z - layers * 10, opacity * frame.incomingAlpha(), false);
        for (int layer = layers - 1; layer > 0; layer--) {
            float offset = (layer - progress) * LAYER_OFFSET;
            renderCard(graphics, getX() + offset, getY() - offset,
                    z - layer * 10, opacity, layer == 1);
        }
        if (frame.outgoingAlpha() > 0) {
            float offset = -progress * LAYER_OFFSET;
            renderCard(graphics, getX() + offset, getY() - offset, z,
                    opacity * frame.outgoingAlpha(), true);
            renderItem(graphics, stacks.get(cycle.previous()), getX() + offset, getY() - offset,
                    opacity * frame.outgoingAlpha());
        }
        float offset = (1 - progress) * LAYER_OFFSET;
        renderItem(graphics, stacks.get(cycle.selected()), getX() + offset, getY() - offset,
                opacity * frame.incomingAlpha());
        renderCount(graphics, opacity);
        wasHovered = isHovered;
    }

    private void renderCard(GuiGraphics graphics, float x, float y, float depth, float alpha, boolean front) {
        if (alpha <= 0)
            return;
        graphics.flush();
        card.withBackground(front ? BoxElement.COLOR_BACKGROUND_TRANSPARENT : BoxElement.COLOR_BACKGROUND_FLAT)
                .gradientBorder(front ? gradientColor : COLOR_IDLE)
                .at(x, y, depth)
                .withBounds(width, height)
                .withAlpha(alpha)
                .render(graphics);
    }

    private void renderItem(GuiGraphics graphics, ItemStack stack, float x, float y, float alpha) {
        if (alpha <= 0)
            return;
        ITEM_TEXTURE.render(graphics, stack, x - 2, y - 2, z + 150, alpha);
    }

    private void renderCount(GuiGraphics graphics, float opacity) {
        int alpha = Math.clamp((int) (opacity * 255), 0, 255);
        if (alpha < 4)
            return;
        var font = Minecraft.getInstance().font;
        var count = Integer.toString(stacks.size());
        int x = getX() + width - font.width(count) + 1;
        int y = getY() + height - 6;
        graphics.pose().pushPose();
        try {
            graphics.pose().translate(0, 0, z + 170);
            graphics.fill(x - 1, y - 1, getX() + width + 2, y + 8, ((int) (alpha * .75f) << 24) | 0x101010);
            graphics.drawString(font, count, x, y, (alpha << 24) | 0xFFFFFF);
        } finally {
            graphics.pose().popPose();
        }
    }

    public List<Component> getGroupTooltip() {
        cycle.settle();
        var tooltip = new ArrayList<>(Screen.getTooltipFromItem(Minecraft.getInstance(), stacks.get(cycle.selected())));
        tooltip.add(Component.empty());
        tooltip.add(group.title().copy().withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("create_dragons_plus.ponder.group.position",
                cycle.selected() + 1, stacks.size()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("create_dragons_plus.ponder.group.scroll").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable(hasScene() ? "create_dragons_plus.ponder.group.open"
                : "create_dragons_plus.ponder.group.no_scene").withStyle(ChatFormatting.DARK_GRAY));
        return tooltip;
    }
}
