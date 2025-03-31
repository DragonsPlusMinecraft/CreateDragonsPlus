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

package plus.dragons.createdragonsplus.common.features;

import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlagSet;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import plus.dragons.createdragonsplus.config.FeaturesConfig.ConfigFeature;

public interface ConfigFeatureElement extends FeatureElement {
    ConfigFeature getFeatureConfig();

    @Override
    default FeatureFlagSet requiredFeatures() {
        return FeatureFlagSet.of();
    }

    @Override
    default boolean isEnabled(FeatureFlagSet enabledFeatures) {
        // Configs are not loaded in data gen
        if (DatagenModLoader.isRunningDataGen())
            return FeatureElement.super.isEnabled(enabledFeatures);
        return getFeatureConfig().get() && FeatureElement.super.isEnabled(enabledFeatures);
    }
}
