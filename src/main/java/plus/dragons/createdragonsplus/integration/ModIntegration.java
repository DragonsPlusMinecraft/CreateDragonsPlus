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

package plus.dragons.createdragonsplus.integration;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.fml.ModList;

public enum ModIntegration {
    CREATE_GARNISHED(Constants.CREATE_GARNISHED),
    IMMERSIVE_ENGINEERING(Constants.IMMERSIVE_ENGINEERING),
    DYE_DEPOT(Constants.DYE_DEPOT),
    DYENAMICS(Constants.DYENAMICS),
    ARTS_AND_CRAFTS(Constants.ARTS_AND_CRAFTS);

    private final String id;

    ModIntegration(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public boolean enabled() {
        return ModList.get().isLoaded(id);
    }

    public ResourceLocation asResource(String path) {
        return new ResourceLocation(id, path);
    }

    public ModLoadedCondition condition() {
        return new ModLoadedCondition(id);
    }

    public static class Constants {
        public static final String CREATE_GARNISHED = "garnished";
        public static final String IMMERSIVE_ENGINEERING = "immersiveengineering";
        public static final String DYE_DEPOT = "dye_depot";
        public static final String DYENAMICS = "dyenamics";
        public static final String ARTS_AND_CRAFTS = "arts_and_crafts";
    }
}
