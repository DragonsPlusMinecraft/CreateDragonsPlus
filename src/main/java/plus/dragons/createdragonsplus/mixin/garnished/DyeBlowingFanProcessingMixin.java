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

package plus.dragons.createdragonsplus.mixin.garnished;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.BlackDyeBlowingFanProcessingType;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.BlueDyeBlowingFanProcessingType;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.BrownDyeBlowingFanProcessingType;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.CyanDyeBlowingFanProcessingType;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.GrayDyeBlowingFanProcessingType;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.GreenDyeBlowingFanProcessingType;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.LightBlueDyeBlowingFanProcessingType;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.LightGrayDyeBlowingFanProcessingType;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.LimeDyeBlowingFanProcessingType;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.MagentaDyeBlowingFanProcessingType;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.OrangeDyeBlowingFanProcessingType;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.PinkDyeBlowingFanProcessingType;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.PurpleDyeBlowingFanProcessingType;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.RedDyeBlowingFanProcessingType;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.WhiteDyeBlowingFanProcessingType;
import net.dakotapride.garnished.recipe.GarnishedFanProcessing.YellowDyeBlowingFanProcessingType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.integration.ModIntegration.Constants;

@Restriction(require = @Condition(Constants.CREATE_GARNISHED))
@Mixin({
        RedDyeBlowingFanProcessingType.class,
        OrangeDyeBlowingFanProcessingType.class,
        YellowDyeBlowingFanProcessingType.class,
        GreenDyeBlowingFanProcessingType.class,
        LimeDyeBlowingFanProcessingType.class,
        BlueDyeBlowingFanProcessingType.class,
        LightBlueDyeBlowingFanProcessingType.class,
        CyanDyeBlowingFanProcessingType.class,
        PurpleDyeBlowingFanProcessingType.class,
        MagentaDyeBlowingFanProcessingType.class,
        PinkDyeBlowingFanProcessingType.class,
        BlackDyeBlowingFanProcessingType.class,
        GrayDyeBlowingFanProcessingType.class,
        LightGrayDyeBlowingFanProcessingType.class,
        WhiteDyeBlowingFanProcessingType.class,
        BrownDyeBlowingFanProcessingType.class
})
public class DyeBlowingFanProcessingMixin {
    @Inject(method = "isValidAt", at = @At("HEAD"), cancellable = true)
    private void disableDyeBlowing(Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (CDPConfig.recipes().enableBulkColoring.get())
            cir.setReturnValue(false);
    }
}
