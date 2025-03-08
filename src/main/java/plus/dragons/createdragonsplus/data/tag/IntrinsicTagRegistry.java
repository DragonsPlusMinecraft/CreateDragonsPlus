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

import com.tterrag.registrate.providers.RegistrateTagsProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class IntrinsicTagRegistry<T, P extends RegistrateTagsProvider.IntrinsicImpl<T>> extends TagRegistry<T, P> {
    public IntrinsicTagRegistry(String namespace, ResourceKey<? extends Registry<T>> registry) {
        super(namespace, registry);
    }

    @Override
    protected ProviderEntry getEntry(TagKey<T> tag) {
        return (ProviderEntry) this.providerEntries.computeIfAbsent(tag, ProviderEntry::new);
    }

    public final void add(TagKey<T> tag, T value) {
        this.getEntry(tag).intrinsicValues.add(() -> value);
    }

    public final void add(TagKey<T> tag, Supplier<T> value) {
        this.getEntry(tag).intrinsicValues.add(value);
    }

    protected class ProviderEntry extends TagRegistry<T, P>.ProviderEntry {
        protected final List<Supplier<T>> intrinsicValues = new ArrayList<>();

        public ProviderEntry(TagKey<T> tag) {
            super(tag);
        }

        @Override
        protected void generate(P provider) {
            var tag = provider.addTag(this.tag);
            this.intrinsicValues.stream().map(Supplier::get).forEach(tag::add);
            this.values.forEach(tag::add);
            this.optionalValues.forEach(tag::addOptional);
            this.tagValues.forEach(tag::addTag);
            this.optionalValues.forEach(tag::addOptional);
        }
    }
}
