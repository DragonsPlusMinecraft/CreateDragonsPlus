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

package plus.dragons.createdragonsplus.common.fluids.dye;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;

public class DyeVariantRegistry {
    private static final AtomicBoolean FROZEN = new AtomicBoolean(false);
    private static volatile List<DyeVariant> all = List.of();
    private static volatile Map<ResourceLocation, DyeVariant> byId = Map.of();
    private static volatile Map<ResourceLocation, Integer> order = Map.of();

    public static void freeze(Collection<DyeVariant> variants) {
        if (!FROZEN.compareAndSet(false, true))
            throw new IllegalStateException("Dye variants have already been frozen");
        all = List.copyOf(variants);
        byId = all.stream().collect(Collectors.toUnmodifiableMap(DyeVariant::id, Function.identity()));
        var index = new LinkedHashMap<ResourceLocation, Integer>();
        for (int i = 0; i < all.size(); i++) {
            index.put(all.get(i).id(), i);
        }
        order = Map.copyOf(index);
    }

    public static List<DyeVariant> all() {
        return all;
    }

    public static Optional<DyeVariant> get(ResourceLocation id) {
        return Optional.ofNullable(byId.get(id));
    }

    public static Comparator<DyeVariant> creativeModeTabOrder() {
        return Comparator.comparingInt(variant -> order.getOrDefault(variant.id(), Integer.MAX_VALUE));
    }

    public static int creativeModeTabIndex(ResourceLocation id) {
        return order.getOrDefault(id, Integer.MAX_VALUE);
    }

    public static boolean isFrozen() {
        return FROZEN.get();
    }

    public static class Builder {
        private final Map<ResourceLocation, DyeVariant> variants = new LinkedHashMap<>();

        public void add(DyeVariant variant) {
            var previous = variants.putIfAbsent(variant.id(), variant);
            if (previous != null)
                throw new IllegalArgumentException("Duplicate dye variant: " + variant.id());
        }

        public Collection<DyeVariant> build() {
            return variants.values();
        }
    }
}
