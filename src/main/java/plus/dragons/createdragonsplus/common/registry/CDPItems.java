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

import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;

import java.util.EnumMap;
import net.minecraft.Util;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import plus.dragons.createdragonsplus.common.recipe.color.DyeColors;
import plus.dragons.createdragonsplus.data.tag.ItemTagRegistry;

public class CDPItems {
    public static final CommonTags COMMON_TAGS = new CommonTags();

    public static void register(IEventBus modBus) {
        REGISTRATE.registerItemTags(COMMON_TAGS);
    }

    public static class CommonTags extends ItemTagRegistry {
        public final TagKey<Item> dyeBuckets = tag("buckets/dye", "Dye Buckets");
        public final EnumMap<DyeColor, TagKey<Item>> dyeBucketsByColor = Util.make(new EnumMap<>(DyeColor.class), map -> {
            for (var color : DyeColors.ALL) {
                var tag = tag("buckets/dye/" + color.getName(), DyeColors.LOCALIZATION.get(color) + " Dye Buckets");
                map.put(color, tag);
                addTag(this.dyeBuckets, tag);
            }
        });

        protected CommonTags() {
            super("c");
            addTag(Tags.Items.BUCKETS, dyeBuckets);
        }
    }
}
