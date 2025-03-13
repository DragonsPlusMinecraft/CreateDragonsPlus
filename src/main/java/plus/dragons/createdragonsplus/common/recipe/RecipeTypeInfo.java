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

package plus.dragons.createdragonsplus.common.recipe;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unchecked")
public class RecipeTypeInfo<R extends Recipe<?>> implements IRecipeTypeInfo {
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> serializer;
    private final DeferredHolder<RecipeType<?>, RecipeType<R>> type;

    public RecipeTypeInfo(String name, Supplier<? extends RecipeSerializer<R>> serializer, DeferredRegister<RecipeSerializer<?>> serializerRegister, DeferredRegister<RecipeType<?>> typeRegister) {
        this.serializer = serializerRegister.register(name, serializer);
        this.type = typeRegister.register(name, RecipeType::simple);
    }

    @Override
    public ResourceLocation getId() {
        return serializer.getId();
    }

    @Override
    public RecipeSerializer<R> getSerializer() {
        return serializer.get();
    }

    @Override
    public RecipeType<R> getType() {
        return type.get();
    }
}
