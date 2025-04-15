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

package plus.dragons.createdragonsplus.common.registrate.builder;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CustomStatBuilder<P> extends AbstractBuilder<ResourceLocation, ResourceLocation, P, CustomStatBuilder<P>> {
    private final Supplier<ResourceLocation> factory;

    public CustomStatBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, Supplier<ResourceLocation> factory) {
        super(owner, parent, name, callback, BuiltInRegistries.CUSTOM_STAT.key());
        this.factory = factory;
    }

    public CustomStatBuilder<P> lang(String name) {
        return lang(resourceLocation -> "stat." + resourceLocation.toLanguageKey(), name);
    }

    @Override
    protected @NotNull ResourceLocation createEntry() {
        return factory.get();
    }
}
