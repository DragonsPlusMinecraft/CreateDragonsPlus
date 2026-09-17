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

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PonderTagGroupsTest {
    private static final ResourceLocation TAG = id("test:tag");
    private static final ResourceLocation GROUP = id("test:foods");
    private static final ResourceLocation A = id("test:apple");
    private static final ResourceLocation B = id("addon:bread");
    private static final ResourceLocation C = id("third:carrot");
    private static final ResourceLocation HIDDEN = id("test:leftovers");
    private final Map<ResourceLocation, Set<ResourceLocation>> nativeTags = new HashMap<>();
    private PonderTagRegistrationHelper<ResourceLocation> helper;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void begin() {
        helper = (PonderTagRegistrationHelper<ResourceLocation>) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class<?>[] { PonderTagRegistrationHelper.class },
                (proxy, method, args) -> {
                    if (!method.getName().equals("addTagToComponent"))
                        throw new UnsupportedOperationException(method.getName());
                    nativeTags.computeIfAbsent((ResourceLocation) args[1], ignored -> new HashSet<>())
                            .add((ResourceLocation) args[0]);
                    return null;
                });
        PonderTagGroups.beginRegistration();
    }

    @AfterEach
    void cleanup() {
        PonderTagGroups.abortRegistration();
    }

    @Test
    void independentContributorsMergeBeforeOrAfterDefinition() {
        List<PonderTagGroups.DisplayEntry> first = null;
        for (boolean reverse : List.of(false, true)) {
            if (reverse)
                PonderTagGroups.beginRegistration();
            if (!reverse)
                define(GROUP, 0);
            var firstContributor = PonderTagGroups.addToGroup(helper, TAG, GROUP);
            var secondContributor = PonderTagGroups.addToGroup(helper, TAG, GROUP);
            firstContributor.add(reverse ? B : A).addHidden(HIDDEN);
            secondContributor.add(reverse ? A : B).add(C, A);
            if (reverse)
                define(GROUP, 0);
            PonderTagGroups.endRegistration();
            var result = PonderTagGroups.collapse(TAG, List.of(HIDDEN, C, B, A));
            assertEquals(1, result.size());
            assertEquals(List.of(B, A, C), result.getFirst().group().members());
            assertEquals(Set.of(A, B, C, HIDDEN), nativeTags.get(TAG));
            if (first != null)
                assertEquals(first, result);
            first = result;
        }
    }

    @Test
    void keepsGroupAtFirstMatchedPositionAndUnrelatedOrder() {
        define(GROUP, 0);
        PonderTagGroups.addToGroup(helper, TAG, GROUP).add(A, B).addHidden(HIDDEN);
        PonderTagGroups.endRegistration();
        var before = id("test:before");
        var after = id("test:after");
        assertEquals(List.of(before, B, after),
                visibleIds(TAG, List.of(before, HIDDEN, after, A, B)));
    }

    @Test
    void visibleMembershipWinsOverHiddenMembershipInEitherOrder() {
        define(GROUP, 0);
        PonderTagGroups.addToGroup(helper, TAG, GROUP).addHidden(A).add(A, B).addHidden(B);
        PonderTagGroups.endRegistration();
        assertEquals(List.of(B, A), PonderTagGroups.collapse(TAG, List.of(A, B)).getFirst().group().members());
    }

    @Test
    void conditionalContributionsDisappearOnReload() {
        define(GROUP, 0);
        PonderTagGroups.addToGroup(helper, TAG, GROUP).add(A, B);
        PonderTagGroups.endRegistration();
        assertNotNull(PonderTagGroups.collapse(TAG, List.of(A, B)).getFirst().group());

        PonderTagGroups.beginRegistration();
        define(GROUP, 0);
        PonderTagGroups.addToGroup(helper, TAG, GROUP).add(A);
        PonderTagGroups.endRegistration();
        assertEquals(List.of(A, B), visibleIds(TAG, List.of(A, B)));
        assertNull(PonderTagGroups.collapse(TAG, List.of(A)).getFirst().group());
    }

    @Test
    void sameGroupIdIsScopedToItsTag() {
        var otherTag = id("test:other_tag");
        define(GROUP, 0);
        PonderTagGroups.registerGroup(otherTag, GROUP, Component.literal("Other title"));
        PonderTagGroups.addToGroup(helper, TAG, GROUP).add(A, B);
        PonderTagGroups.addToGroup(helper, otherTag, GROUP).add(B, C);
        PonderTagGroups.endRegistration();
        assertEquals(List.of(B, C), visibleIds(TAG, List.of(A, B, C)));
        assertEquals(List.of(A, B), visibleIds(otherTag, List.of(A, B, C)));
        assertEquals("Other title", PonderTagGroups.collapse(otherTag, List.of(B, C)).getFirst().group().title().getString());
    }

    @Test
    void specificGroupsClaimHiddenAndSingleMembersBeforeFallback() {
        var fallback = id("test:fallback");
        define(fallback, -100);
        define(GROUP, 0);
        PonderTagGroups.addToGroup(helper, TAG, fallback).add(A, B, C, HIDDEN);
        PonderTagGroups.addToGroup(helper, TAG, GROUP).add(A).addHidden(HIDDEN);
        PonderTagGroups.endRegistration();
        var result = PonderTagGroups.collapse(TAG, List.of(HIDDEN, C, B, A));
        assertEquals(List.of(A, B), result.stream().map(PonderTagGroups.DisplayEntry::id).toList());
        assertNull(result.getFirst().group());
        assertEquals(List.of(B, C), result.get(1).group().members());
    }

    @Test
    void equalPriorityUsesFullGroupId() {
        var later = id("z:first");
        var earlier = id("a:last");
        define(later, 0);
        define(earlier, 0);
        PonderTagGroups.addToGroup(helper, TAG, later).add(A, B, C);
        PonderTagGroups.addToGroup(helper, TAG, earlier).add(A, B);
        PonderTagGroups.endRegistration();
        var result = PonderTagGroups.collapse(TAG, List.of(A, B, C));
        assertEquals(earlier, result.getFirst().group().id());
        assertEquals(C, result.get(1).id());
    }

    @Test
    void undefinedGroupKeepsOrdinaryEntries() {
        PonderTagGroups.addToGroup(helper, TAG, GROUP).add(A).addHidden(HIDDEN);
        PonderTagGroups.endRegistration();
        assertEquals(List.of(HIDDEN, A), visibleIds(TAG, List.of(HIDDEN, A)));
    }

    @Test
    void noAvailableIconsKeepsEntriesAndEmptyTagsStayEmpty() {
        define(GROUP, 0);
        PonderTagGroups.addToGroup(helper, TAG, GROUP).add(A).addHidden(HIDDEN);
        PonderTagGroups.endRegistration();
        assertEquals(List.of(HIDDEN), visibleIds(TAG, List.of(HIDDEN)));
        assertEquals(List.of(), visibleIds(TAG, List.of()));
    }

    @Test
    void aSingleIconUsesAnOrdinaryCardAndStillHidesStages() {
        define(GROUP, 0);
        PonderTagGroups.addToGroup(helper, TAG, GROUP).add(A).addHidden(HIDDEN);
        PonderTagGroups.endRegistration();
        var result = PonderTagGroups.collapse(TAG, List.of(HIDDEN, A));
        assertEquals(1, result.size());
        assertEquals(A, result.getFirst().id());
        assertNull(result.getFirst().group());
    }

    @Test
    void duplicateDefinitionIsIdempotentButConflictingMetadataFails() {
        define(GROUP, 0);
        define(GROUP, 0);
        assertThrows(IllegalArgumentException.class, () -> define(GROUP, 1));
        assertThrows(IllegalArgumentException.class,
                () -> PonderTagGroups.registerGroup(TAG, GROUP, Component.literal("Different")));
    }

    @Test
    void registrationHelpersCannotEscapeTheirRegistrationCycle() {
        var old = PonderTagGroups.addToGroup(helper, TAG, GROUP);
        PonderTagGroups.endRegistration();
        assertThrows(IllegalStateException.class, () -> old.add(A));
        assertThrows(IllegalStateException.class, () -> define(GROUP, 0));
        PonderTagGroups.beginRegistration();
        assertThrows(IllegalStateException.class, () -> old.add(B));
        assertTrue(nativeTags.isEmpty());
    }

    @Test
    void abortedRegistrationPreservesLastCompleteSnapshot() {
        define(GROUP, 0);
        PonderTagGroups.addToGroup(helper, TAG, GROUP).add(A, B);
        PonderTagGroups.endRegistration();
        PonderTagGroups.beginRegistration();
        PonderTagGroups.abortRegistration();
        assertEquals(1, visibleIds(TAG, List.of(A, B)).size());
    }

    @Test
    void callerCannotMutatePublishedTitlesOrMembers() {
        var title = Component.literal("Original");
        var members = new ArrayList<>(List.of(A, B));
        PonderTagGroups.registerGroup(TAG, GROUP, title);
        PonderTagGroups.addToGroup(helper, TAG, GROUP).addAll(members);
        members.clear();
        title.append(" changed");
        PonderTagGroups.endRegistration();
        var group = PonderTagGroups.collapse(TAG, List.of(A, B)).getFirst().group();
        group.title().copy().append(" changed again");
        assertEquals("Original", group.title().getString());
        assertEquals(2, group.members().size());
        assertThrows(UnsupportedOperationException.class, () -> group.members().clear());
    }

    private static ResourceLocation id(String id) {
        return ResourceLocation.parse(id);
    }

    private static void define(ResourceLocation group, int priority) {
        PonderTagGroups.registerGroup(TAG, group, Component.literal(group.toString()), priority);
    }

    private static List<ResourceLocation> visibleIds(ResourceLocation tag, List<ResourceLocation> entries) {
        return PonderTagGroups.collapse(tag, entries).stream().map(PonderTagGroups.DisplayEntry::id).toList();
    }
}
