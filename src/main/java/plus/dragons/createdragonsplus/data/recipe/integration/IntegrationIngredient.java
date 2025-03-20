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

package plus.dragons.createdragonsplus.data.recipe.integration;

import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class IntegrationIngredient {
    public static Ingredient of(String mod, String name) {
        return of(ResourceLocation.fromNamespaceAndPath(mod, name));
    }

    public static Ingredient of(ResourceLocation location) {
        return Ingredient.fromValues(Stream.of(new Value(location)));
    }

    public static Ingredient of(ResourceLocation... locations) {
        return Ingredient.fromValues(Arrays.stream(locations).map(Value::new));
    }

    public record Value(ResourceLocation location) implements Ingredient.Value {
        public static final MapCodec<Value> MAP_CODEC = ResourceLocation.CODEC.fieldOf("item")
                .xmap(Value::new, Value::location);

        @Override
        public Collection<ItemStack> getItems() {
            return List.of();
        }
    }
}
