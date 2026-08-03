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

package plus.dragons.createdragonsplus.common.registry;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.config.FeaturesConfig;
import plus.dragons.createdragonsplus.config.FeaturesConfig.ConfigFeature;

public final class CDPConditions {
    public static final ResourceLocation CONFIG_FEATURE_ID = CDPCommon.asResource("config_feature");
    public static final IConditionSerializer<ConfigFeature> CONFIG_FEATURE = new IConditionSerializer<>() {
        @Override
        public void write(JsonObject json, ConfigFeature value) {
            json.addProperty("feature", value.getFeatureId().toString());
        }

        @Override
        public ConfigFeature read(JsonObject json) {
            ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "feature"));
            ConfigFeature feature = FeaturesConfig.getFeatures().get(id);
            if (feature == null)
                throw new IllegalArgumentException("No config feature with id [" + id + "] exists");
            return feature;
        }

        @Override
        public ResourceLocation getID() {
            return CONFIG_FEATURE_ID;
        }
    };

    private static boolean registered;

    public static void register(IEventBus modBus) {
        if (!registered) {
            CraftingHelper.register(CONFIG_FEATURE);
            registered = true;
        }
    }

    private CDPConditions() {}
}
