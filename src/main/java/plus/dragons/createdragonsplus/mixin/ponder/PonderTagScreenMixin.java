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

package plus.dragons.createdragonsplus.mixin.ponder;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.ponder.foundation.PonderTag;
import net.createmod.ponder.foundation.ui.AbstractPonderScreen;
import net.createmod.ponder.foundation.ui.PonderButton;
import net.createmod.ponder.foundation.ui.PonderTagScreen;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import plus.dragons.createdragonsplus.client.ponder.GroupedPonderButton;
import plus.dragons.createdragonsplus.client.ponder.PonderTagGroups;
import plus.dragons.createdragonsplus.util.CodeReference;

@Mixin(value = PonderTagScreen.class, remap = false)
@CodeReference(source = "Ponder", license = "MIT", value = PonderTagScreen.class, targets = "renderWindowForeground")
public abstract class PonderTagScreenMixin extends AbstractPonderScreen {
    @Shadow
    @Final
    private PonderTag tag;
    @Shadow
    @Final
    protected List<PonderTagScreen.ItemEntry> items;
    @Unique
    private Map<ResourceLocation, PonderTagGroups.ResolvedGroup> create_dragons_plus$groups = Map.of();

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0))
    private void init$collapseTagGroups(CallbackInfo ci) {
        var entries = new LinkedHashMap<ResourceLocation, PonderTagScreen.ItemEntry>();
        for (var entry : items) {
            if (entry.item() != null && entry.item().asItem() != Items.AIR)
                entries.putIfAbsent(entry.key(), entry);
        }
        var collapsed = PonderTagGroups.collapse(tag.getId(), List.copyOf(entries.keySet()));
        var groups = new LinkedHashMap<ResourceLocation, PonderTagGroups.ResolvedGroup>();
        items.clear();
        for (var entry : collapsed) {
            items.add(entries.get(entry.id()));
            if (entry.group() != null)
                groups.put(entry.id(), entry.group());
        }
        create_dragons_plus$groups = groups;
    }

    @WrapOperation(method = "init", at = @At(value = "NEW", target = "(II)Lnet/createmod/ponder/foundation/ui/PonderButton;", ordinal = 0))
    private PonderButton init$createGroupButton(int x, int y, Operation<PonderButton> original,
            @Local PonderTagScreen.ItemEntry entry) {
        var group = create_dragons_plus$groups.get(entry.key());
        return group == null ? original.call(x, y) : new GroupedPonderButton(x, y, group);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init$bindCurrentMember(CallbackInfo ci) {
        for (var child : children()) {
            if (child instanceof GroupedPonderButton button) {
                button.withCallback((mouseX, mouseY) -> {
                    if (!button.hasScene())
                        return;
                    centerScalingOn(mouseX, mouseY);
                    var screen = PonderUI.of(button.selectedId());
                    ((PonderUIAccessor) screen).create_dragons_plus$setReferredToByTag(tag);
                    ScreenOpener.transitionTo(screen);
                });
            }
        }
    }

    @Inject(method = "renderWindowForeground", at = @At("HEAD"), cancellable = true)
    private void renderWindowForeground$groupTooltip(GuiGraphics graphics, int mouseX, int mouseY,
            float partialTicks, CallbackInfo ci) {
        for (var child : children()) {
            if (child instanceof GroupedPonderButton button && button.isVisible() && button.isMouseOver(mouseX, mouseY)) {
                graphics.flush();
                RenderSystem.disableDepthTest();
                graphics.pose().pushPose();
                try {
                    graphics.pose().translate(0, 0, 200);
                    graphics.renderComponentTooltip(font, button.getGroupTooltip(), mouseX, mouseY);
                    graphics.flush();
                } finally {
                    graphics.pose().popPose();
                    RenderSystem.enableDepthTest();
                }
                ci.cancel();
                return;
            }
        }
    }
}
