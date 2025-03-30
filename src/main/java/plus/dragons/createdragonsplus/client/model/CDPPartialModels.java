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

package plus.dragons.createdragonsplus.client.model;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.Create;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;
import plus.dragons.createdragonsplus.common.registry.CDPItems;

public class CDPPartialModels {
    public static void register() {
        registerRarePackageModel(CDPItems.RARE_BLAZE_PACKAGE.getId(), 12, 10);
        registerRarePackageModel(CDPItems.RARE_MARBLE_GATE_PACKAGE.getId(), 12, 10);
    }

    public static void registerRarePackageModel(ResourceLocation id, int width, int height) {
        AllPartialModels.PACKAGES.put(id, PartialModel.of(id.withPrefix("item/")));
        AllPartialModels.PACKAGE_RIGGING.put(id, PartialModel.of(Create.asResource("item/package/rigging_" + width + "x" + height)));
    }
}
