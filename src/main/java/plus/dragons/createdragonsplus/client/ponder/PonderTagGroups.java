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

package plus.dragons.createdragonsplus.client.ponder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Opt-in presentation groups for Ponder tags. Call from
 * {@link net.createmod.ponder.api.registration.PonderPlugin#registerTags}.
 * Groups are scoped by both tag and group ID and rebuilt on every Ponder registration.
 * Scenes are still registered with Ponder's scene registration helper.
 */
public final class PonderTagGroups {
    private static final Logger LOGGER = LoggerFactory.getLogger(PonderTagGroups.class);
    private static final ThreadLocal<Collector> REGISTRATION = new ThreadLocal<>();
    private static volatile Map<ResourceLocation, List<Definition>> groups = Map.of();

    private PonderTagGroups() {}

    /** Declare the group's title with priority zero. Contributions may precede this declaration. */
    public static void registerGroup(ResourceLocation tag, ResourceLocation group, Component title) {
        registerGroup(tag, group, title, 0);
    }

    /**
     * Declare a group. Higher priorities claim overlapping members first; ties use
     * the full group ID's lexical order. A fallback group can use priority -100.
     * Repeating an identical declaration is allowed; conflicting metadata is an error.
     */
    public static void registerGroup(ResourceLocation tag, ResourceLocation group, Component title, int priority) {
        var key = new Key(tag, group);
        var metadata = new Metadata(Objects.requireNonNull(title).copy(), priority);
        var contribution = collector().contributions.computeIfAbsent(key, ignored -> new Contribution());
        if (contribution.metadata != null && !contribution.metadata.equals(metadata))
            throw new IllegalArgumentException("Conflicting Ponder group definition: " + key);
        contribution.metadata = metadata;
    }

    /**
     * Append members, including the corresponding native tag registrations.
     * Obtain a fresh helper in each registerTags call; do not retain it across reloads.
     */
    public static MemberRegistration addToGroup(PonderTagRegistrationHelper<ResourceLocation> helper,
            ResourceLocation tag, ResourceLocation group) {
        return new MemberRegistration(collector(), Objects.requireNonNull(helper), new Key(tag, group));
    }

    private static Collector collector() {
        var collector = REGISTRATION.get();
        if (collector == null)
            throw new IllegalStateException("Register Ponder groups from PonderPlugin.registerTags");
        return collector;
    }

    @Internal
    public static void beginRegistration() {
        if (REGISTRATION.get() != null)
            throw new IllegalStateException("Ponder group registration is already running");
        REGISTRATION.set(new Collector());
    }

    @Internal
    public static void endRegistration() {
        var collected = new HashMap<ResourceLocation, List<Definition>>();
        collector().contributions.forEach((key, contribution) -> {
            if (contribution.metadata == null) {
                LOGGER.warn("Ponder group {} in tag {} has members but no definition; keeping ordinary entries",
                        key.group, key.tag);
                return;
            }
            collected.computeIfAbsent(key.tag, ignored -> new ArrayList<>())
                    .add(new Definition(key.group, contribution.metadata,
                            Set.copyOf(contribution.members), Set.copyOf(contribution.icons)));
        });
        var snapshot = new HashMap<ResourceLocation, List<Definition>>();
        collected.forEach((tag, definitions) -> snapshot.put(tag, definitions.stream()
                .sorted(Comparator.<Definition>comparingInt(definition -> definition.metadata.priority).reversed()
                        .thenComparing(definition -> definition.id.toString()))
                .toList()));
        groups = Map.copyOf(snapshot);
        REGISTRATION.remove();
    }

    /** Discard an incomplete registration without replacing the last complete snapshot. */
    @Internal
    public static void abortRegistration() {
        REGISTRATION.remove();
    }

    /**
     * Collapse a tag's valid item IDs, preserving the first matched position of each
     * group. The screen resolves items and removes invalid/AIR entries before calling.
     */
    @Internal
    public static List<DisplayEntry> collapse(ResourceLocation tag, List<ResourceLocation> entries) {
        var claimed = new HashSet<ResourceLocation>();
        var membership = new HashMap<ResourceLocation, ResolvedGroup>();
        for (var definition : groups.getOrDefault(tag, List.of())) {
            var matched = entries.stream()
                    .filter(id -> definition.members.contains(id) && !claimed.contains(id))
                    .distinct().toList();
            claimed.addAll(matched);
            var icons = matched.stream().filter(definition.icons::contains)
                    .sorted(Comparator.comparing(ResourceLocation::toString)).toList();
            if (icons.isEmpty())
                continue;
            var group = new ResolvedGroup(definition.id, definition.metadata.title, icons);
            matched.forEach(id -> membership.put(id, group));
        }

        var emitted = new HashSet<ResourceLocation>();
        var visible = new ArrayList<DisplayEntry>();
        for (var id : new LinkedHashSet<>(entries)) {
            var group = membership.get(id);
            if (group == null) {
                visible.add(new DisplayEntry(id, null));
            } else if (emitted.add(group.id)) {
                visible.add(new DisplayEntry(group.members.getFirst(), group.members.size() > 1 ? group : null));
            }
        }
        return List.copyOf(visible);
    }

    public static final class MemberRegistration {
        private final Collector owner;
        private final PonderTagRegistrationHelper<ResourceLocation> helper;
        private final Key key;

        private MemberRegistration(Collector owner, PonderTagRegistrationHelper<ResourceLocation> helper, Key key) {
            this.owner = owner;
            this.helper = helper;
            this.key = key;
        }

        /** Add selectable carousel members and register them with the native tag. */
        public MemberRegistration add(ResourceLocation... members) {
            return addAll(List.of(members));
        }

        public MemberRegistration addAll(Collection<ResourceLocation> members) {
            return contribute(members, true);
        }

        /**
         * Fold these entries into the group without showing their icons, for example
         * partially consumed food. Their native tags and individual scenes remain available.
         */
        public MemberRegistration addHidden(ResourceLocation... members) {
            return addHiddenAll(List.of(members));
        }

        public MemberRegistration addHiddenAll(Collection<ResourceLocation> members) {
            return contribute(members, false);
        }

        private MemberRegistration contribute(Collection<ResourceLocation> members, boolean selectable) {
            if (collector() != owner)
                throw new IllegalStateException("Ponder group member helper belongs to a previous registration");
            var checked = List.copyOf(members);
            var contribution = owner.contributions.computeIfAbsent(key, ignored -> new Contribution());
            for (var member : checked) {
                helper.addTagToComponent(member, key.tag);
                contribution.members.add(member);
                if (selectable)
                    contribution.icons.add(member);
            }
            return this;
        }
    }

    @Internal
    public record DisplayEntry(ResourceLocation id, @Nullable ResolvedGroup group) {}

    @Internal
    public record ResolvedGroup(ResourceLocation id, Component title, List<ResourceLocation> members) {
        public ResolvedGroup {
            title = title.copy();
            members = List.copyOf(members);
        }

        @Override
        public Component title() {
            return title.copy();
        }
    }

    private record Key(ResourceLocation tag, ResourceLocation group) {
        private Key {
            Objects.requireNonNull(tag);
            Objects.requireNonNull(group);
        }
    }

    private record Metadata(Component title, int priority) {}

    private record Definition(ResourceLocation id, Metadata metadata, Set<ResourceLocation> members,
            Set<ResourceLocation> icons) {}

    private static final class Contribution {
        private @Nullable Metadata metadata;
        private final Set<ResourceLocation> members = new HashSet<>();
        private final Set<ResourceLocation> icons = new HashSet<>();
    }

    private static final class Collector {
        private final Map<Key, Contribution> contributions = new HashMap<>();
    }
}
