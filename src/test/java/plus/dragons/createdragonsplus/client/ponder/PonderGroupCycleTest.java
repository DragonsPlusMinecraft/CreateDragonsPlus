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

import org.junit.jupiter.api.Test;

class PonderGroupCycleTest {
    @Test
    void cyclesAfterFortyUiTicksAndBlendsForEightTicks() {
        var cycle = new PonderGroupCycle(3);
        tick(cycle, 39, false);
        assertEquals(0, cycle.selected());
        cycle.tick(false);
        assertEquals(1, cycle.selected());
        assertEquals(0, cycle.previous());
        assertEquals(0, cycle.blend(0));
        tick(cycle, 4, false);
        assertEquals(.5f, cycle.blend(0));
        tick(cycle, 4, false);
        assertEquals(1, cycle.blend(0));
        tick(cycle, 32, false);
        assertEquals(2, cycle.selected());
    }

    @Test
    void hoverSettlesTransitionAndPausesAutomaticSelection() {
        var cycle = new PonderGroupCycle(3);
        tick(cycle, 42, false);
        tick(cycle, 100, true);
        assertEquals(1, cycle.selected());
        assertEquals(cycle.selected(), cycle.previous());
        assertEquals(1, cycle.blend(0));
    }

    @Test
    void manualSelectionWrapsImmediatelyAndResetsTimer() {
        var cycle = new PonderGroupCycle(3);
        cycle.select(-1);
        assertEquals(2, cycle.selected());
        assertEquals(1, cycle.blend(0));
        cycle.select(1);
        assertEquals(0, cycle.selected());
        tick(cycle, 39, false);
        cycle.select(1);
        tick(cycle, 39, false);
        assertEquals(1, cycle.selected());
        cycle.tick(false);
        assertEquals(2, cycle.selected());
    }

    @Test
    void fastScrollDuringTransitionHasOneUnambiguousSelection() {
        var cycle = new PonderGroupCycle(3);
        tick(cycle, 42, false);
        cycle.select(-1);
        cycle.select(-1);
        cycle.select(1);
        assertEquals(0, cycle.selected());
        assertEquals(0, cycle.previous());
        assertEquals(1, cycle.blend(.5f));
    }

    @Test
    void transitionFadesOutBeforeFadingInAndMovesTheWholeStack() {
        var cycle = new PonderGroupCycle(4);
        tick(cycle, 40, false);
        float previousProgress = -1;
        for (int tick = 0; tick <= PonderGroupCycle.TRANSITION_TICKS; tick++) {
            var frame = cycle.frame(0);
            assertTrue(frame.progress() > previousProgress);
            assertEquals(0, frame.outgoingAlpha() * frame.incomingAlpha());
            // Two advancing cards plus either the departing front or arriving rear.
            int cards = 2 + (frame.outgoingAlpha() > 0 ? 1 : 0) + (frame.incomingAlpha() > 0 ? 1 : 0);
            assertTrue(cards <= 3);
            if (tick == 0) {
                assertEquals(1, frame.outgoingAlpha());
                assertEquals(0, frame.incomingAlpha());
            } else if (tick == PonderGroupCycle.TRANSITION_TICKS) {
                assertEquals(0, frame.outgoingAlpha());
                assertEquals(1, frame.incomingAlpha());
            } else if (tick < PonderGroupCycle.TRANSITION_TICKS / 2) {
                assertTrue(frame.outgoingAlpha() > 0 && frame.outgoingAlpha() < 1);
            }
            previousProgress = frame.progress();
            cycle.tick(false);
        }
    }

    private static void tick(PonderGroupCycle cycle, int count, boolean hovered) {
        for (int i = 0; i < count; i++)
            cycle.tick(hovered);
    }
}
