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

package plus.dragons.createdragonsplus.mixin.immersive_engineering;

import java.util.Arrays;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import plus.dragons.createdragonsplus.integration.ModIntegration;

/**
 * Keeps Immersive Engineering's logic-unit register count stable when Dye Depot extends the
 * vanilla dye-color enum. IE uses the number of dye colors to split its 16 color registers from
 * its eight general-purpose registers, so the additional colors would otherwise produce a
 * negative array size during registry baking.
 */
@Restriction(require = {
        @Condition(ModIntegration.Constants.DYE_DEPOT),
        @Condition(ModIntegration.Constants.IMMERSIVE_ENGINEERING)
})
@Mixin(targets = "blusunrize.immersiveengineering.common.blocks.wooden.LogicUnitBlockEntity", remap = false)
public class LogicUnitBlockEntityMixin {
    private static final int VANILLA_DYE_COLOR_COUNT = 16;

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/DyeColor;values()[Lnet/minecraft/world/item/DyeColor;"))
    private static DyeColor[] createDragonsPlus$useVanillaDyeColorCount() {
        DyeColor[] colors = DyeColor.values();
        return colors.length > VANILLA_DYE_COLOR_COUNT ? Arrays.copyOf(colors, VANILLA_DYE_COLOR_COUNT) : colors;
    }
}
