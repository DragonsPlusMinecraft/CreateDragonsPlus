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

package plus.dragons.createdragonsplus.integration.aether.integration.jei.category;

import com.aetherteam.aether.block.AetherBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class AetherFanCategorySupport {
    public static ItemStack fanCatalyst(String key) {
        var catalyst = AllBlocks.ENCASED_FAN.asStack();
        catalyst.setHoverName(Component.translatable(key).withStyle(style -> style.withItalic(false)));
        return catalyst;
    }

    public static void renderGoldenAercloud(GuiGraphics graphics) {
        GuiGameElement.of(AetherBlocks.GOLDEN_AERCLOUD.get().defaultBlockState())
                .scale(24)
                .atLocal(0, 0, 2)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(graphics);
    }
}
