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

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.simibubi.create.content.logistics.item.filter.attribute.SingletonItemAttribute;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.ColoringFanProcessingType;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.util.ItemStackKey;

public class CDPItemAttributes {
    private static final Map<ItemStackKey, Boolean> STAINABLE_CACHE = new ConcurrentHashMap<>();

    public static final Holder<ItemAttributeType> FREEZABLE = fanProcessing("freezable",
            "can be Frozen",
            "cannot be Frozen",
            CDPFanProcessingTypes.FREEZING);
    public static final Holder<ItemAttributeType> SANDABLE = fanProcessing("sandable",
            "can be Sanded",
            "cannot be Sanded",
            CDPFanProcessingTypes.SANDING);
    public static final Holder<ItemAttributeType> ENDABLE = fanProcessing("endable",
            "can be Ended",
            "cannot be Ended",
            CDPFanProcessingTypes.ENDING);
    public static final Holder<ItemAttributeType> STAINABLE = colorFanProcessing(
            CDPFanProcessingTypes.COLORING.values());

    private static Holder<ItemAttributeType> fanProcessing(String name, String description, String invertedDescription, Supplier<? extends FanProcessingType> processingType) {
        String descriptionKey = "create.item_attributes." + CDPCommon.ID + "." + name;
        String invertedDescriptionKey = descriptionKey + ".inverted";
        REGISTRATE.addRawLang(descriptionKey, description);
        REGISTRATE.addRawLang(invertedDescriptionKey, invertedDescription);
        ItemAttributeType type = new SingletonItemAttribute.Type(attributeType -> new SingletonItemAttribute(attributeType,
                processingType.get()::canProcess, CDPCommon.ID + "." + name));
        return Registry.registerForHolder(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE, CDPCommon.asResource(name), type);
    }

    private static Holder<ItemAttributeType> colorFanProcessing(Collection<Supplier<ColoringFanProcessingType>> processingTypes) {
        String descriptionKey = "create.item_attributes." + CDPCommon.ID + ".stainable";
        String invertedDescriptionKey = descriptionKey + ".inverted";
        REGISTRATE.addRawLang(descriptionKey, "can be Stained");
        REGISTRATE.addRawLang(invertedDescriptionKey, "cannot be Stained");
        ItemAttributeType type = new SingletonItemAttribute.Type(attributeType -> new SingletonItemAttribute(attributeType,
                (itemStack, level) -> canProcessByColoring(itemStack, level, processingTypes), CDPCommon.ID + ".stainable"));
        return Registry.registerForHolder(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE, CDPCommon.asResource("stainable"), type);
    }

    private static boolean canProcessByColoring(ItemStack stack, Level level, Collection<Supplier<ColoringFanProcessingType>> processingTypes) {
        if (!CDPConfig.recipes().enableBulkColoring.get())
            return false;
        return STAINABLE_CACHE.computeIfAbsent(ItemStackKey.of(stack), key -> processingTypes.stream()
                .anyMatch(s -> s.get().canProcess(stack, level)));
    }

    public static void recreateCache() {
        STAINABLE_CACHE.clear();
    }

    public static void register(IEventBus modBus) {
        // Create-owned registries are populated directly before their freeze callback runs.
    }
}
