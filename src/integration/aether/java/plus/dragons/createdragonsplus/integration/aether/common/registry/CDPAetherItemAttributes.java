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

package plus.dragons.createdragonsplus.integration.aether.common.registry;

import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.simibubi.create.content.logistics.item.filter.attribute.SingletonItemAttribute;
import net.minecraft.core.Holder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import plus.dragons.createdragonsplus.common.CDPCommon;

public class CDPAetherItemAttributes {
    private static final DeferredRegister<ItemAttributeType> ITEM_ATTRIBUTES = DeferredRegister
            .create(CreateRegistries.ITEM_ATTRIBUTE_TYPE, CDPCommon.ID);

    public static final Holder<ItemAttributeType> AETHER_ENCHANTABLE = ITEM_ATTRIBUTES.register("aether_enchantable",
            () -> new SingletonItemAttribute.Type(type -> new SingletonItemAttribute(
                    type,
                    CDPAetherFanProcessingTypes.ENCHANTING.get()::canProcess,
                    CDPCommon.ID + ".aether_enchantable")));

    public static void register(IEventBus modBus) {
        CDPCommon.REGISTRATE.addRawLang("create.item_attributes." + CDPCommon.ID + ".aether_enchantable",
                "can be Bulk Enchanted");
        CDPCommon.REGISTRATE.addRawLang("create.item_attributes." + CDPCommon.ID + ".aether_enchantable.inverted",
                "cannot be Bulk Enchanted");
        ITEM_ATTRIBUTES.register(modBus);
    }
}
