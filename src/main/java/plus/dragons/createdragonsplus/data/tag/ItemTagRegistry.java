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

package plus.dragons.createdragonsplus.data.tag;

import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public class ItemTagRegistry extends IntrinsicTagRegistry<Item, RegistrateItemTagsProvider> {
    public ItemTagRegistry(String namespace) {
        super(namespace, Registries.ITEM);
    }

    public void addItemLike(TagKey<Item> tag, ItemLike itemLike) {
        this.add(tag, itemLike.asItem());
    }

    public void addItemLike(TagKey<Item> tag, Supplier<ItemLike> itemLike) {
        this.add(tag, () -> itemLike.get().asItem());
    }
}
