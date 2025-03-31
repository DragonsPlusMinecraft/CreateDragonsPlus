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

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.SmithingTemplateItem;
import plus.dragons.createdragonsplus.config.FeaturesConfig.ConfigFeature;

public class ConfigFeatureSmithingTemplateItem extends SmithingTemplateItem implements ConfigFeatureElement {
    private final ConfigFeature config;

    public ConfigFeatureSmithingTemplateItem(
            Component appliesTo, Component ingredients, Component upgradeDescription,
            Component baseSlotDescription, Component additionsSlotDescription,
            List<ResourceLocation> baseSlotEmptyIcons, List<ResourceLocation> additionalSlotEmptyIcons,
            ConfigFeature config, FeatureFlag... requiredFeatures
    ) {
        super(
                appliesTo,
                ingredients,
                upgradeDescription,
                baseSlotDescription,
                additionsSlotDescription,
                baseSlotEmptyIcons,
                additionalSlotEmptyIcons,
                requiredFeatures
        );
        this.config = config;
    }

    @Override
    public ConfigFeature getFeatureConfig() {
        return config;
    }
}
