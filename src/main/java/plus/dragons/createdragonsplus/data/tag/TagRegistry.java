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

import com.google.common.collect.Maps;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class TagRegistry<T, P extends RegistrateTagsProvider<T>> {
    protected final String namespace;
    protected final ResourceKey<? extends Registry<T>> registry;
    protected final Map<TagKey<T>, ProviderEntry> providerEntries;
    protected final Map<TagKey<T>, String> localizations;

    public TagRegistry(String namespace, ResourceKey<? extends Registry<T>> registry) {
        this.namespace = namespace;
        this.registry = registry;
        this.providerEntries = Maps.newConcurrentMap();
        this.localizations = new HashMap<>();
    }

    public final TagKey<T> tag(String path) {
        return TagKey.create(this.registry, ResourceLocation.fromNamespaceAndPath(this.namespace, path));
    }

    public final TagKey<T> tag(String path, String localization) {
        TagKey<T> tag = tag(path);
        this.localizations.put(tag, localization);
        return tag;
    }

    protected ProviderEntry getEntry(TagKey<T> tag) {
        return this.providerEntries.computeIfAbsent(tag, ProviderEntry::new);
    }

    public final void add(TagKey<T> tag, ResourceKey<T> value) {
        this.getEntry(tag).values.add(value);
    }

    public final void addOptional(TagKey<T> tag, ResourceLocation value) {
        this.getEntry(tag).optionalValues.add(value);
    }

    public final void addTag(TagKey<T> tag, TagKey<T> value) {
        this.getEntry(tag).tagValues.add(value);
    }

    public final void addOptionalTag(TagKey<T> tag, ResourceLocation value) {
        this.getEntry(tag).optionalTagValues.add(value);
    }

    public void generate(P provider) {
        this.providerEntries.values().forEach(entry -> entry.generate(provider));
    }

    public void generate(RegistrateLangProvider provider) {
        this.localizations.forEach(provider::add);
    }

    protected class ProviderEntry {
        protected final TagKey<T> tag;
        protected final List<ResourceKey<T>> values = new ArrayList<>();
        protected final List<ResourceLocation> optionalValues = new ArrayList<>();
        protected final List<TagKey<T>> tagValues = new ArrayList<>();
        protected final List<ResourceLocation> optionalTagValues = new ArrayList<>();

        public ProviderEntry(TagKey<T> tag) {
            this.tag = tag;
        }

        protected void generate(P provider) {
            var tag = provider.addTag(this.tag);
            this.values.forEach(tag::add);
            this.optionalValues.forEach(tag::addOptional);
            this.tagValues.forEach(tag::addTag);
            this.optionalValues.forEach(tag::addOptional);
        }
    }
}
