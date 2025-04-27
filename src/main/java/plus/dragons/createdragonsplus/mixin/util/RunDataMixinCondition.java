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

package plus.dragons.createdragonsplus.mixin.util;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import java.util.Optional;
import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester;
import net.neoforged.fml.loading.targets.CommonLaunchHandler;

/**
 * {@link ConditionTester} for testing if the current environment is running datagen.
 */
public class RunDataMixinCondition implements ConditionTester {
    @Override
    public boolean isSatisfied(String mixinClassName) {
        var environment = Launcher.INSTANCE.environment();
        var launchTarget = environment.getProperty(IEnvironment.Keys.LAUNCHTARGET.get()).orElse("MISSING");
        final Optional<ILaunchHandlerService> launchHandler = environment.findLaunchHandler(launchTarget);
        if (launchHandler.isPresent() && launchHandler.get() instanceof CommonLaunchHandler common)
            return common.isData();
        return false;
    }
}
