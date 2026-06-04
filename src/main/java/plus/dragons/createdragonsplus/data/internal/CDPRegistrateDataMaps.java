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

package plus.dragons.createdragonsplus.data.internal;

import com.tterrag.registrate.providers.RegistrateDataMapProvider;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariantRegistry;
import plus.dragons.createdragonsplus.common.registry.CDPDataMaps;
import plus.dragons.createdragonsplus.integration.ModIntegration;

public class CDPRegistrateDataMaps implements NonNullConsumer<RegistrateDataMapProvider> {
    @Override
    public void accept(RegistrateDataMapProvider provider) {
        var fanColoringCatalystFluids = provider.builder(CDPDataMaps.FLUID_FAN_COLORING_CATALYSTS);
        var garnishedLoaded = new ModLoadedCondition("garnished");
        for (var variant : DyeVariantRegistry.all()) {
            if (!variant.isVanilla())
                continue;
            var still = ModIntegration.CREATE_GARNISHED.asResource(variant.id().getPath() + "_mastic_resin");
            var flowing = still.withPrefix("flowing_");
            fanColoringCatalystFluids.add(still, variant.id(), false, garnishedLoaded);
            fanColoringCatalystFluids.add(flowing, variant.id(), false, garnishedLoaded);
        }
    }
}
