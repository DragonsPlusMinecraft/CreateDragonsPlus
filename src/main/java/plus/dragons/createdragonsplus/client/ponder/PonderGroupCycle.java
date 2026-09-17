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

import org.jetbrains.annotations.ApiStatus.Internal;

/** UI-tick based selection and animation, independent of the paused game clock. */
@Internal
public final class PonderGroupCycle {
    public static final int CYCLE_TICKS = 40;
    public static final int TRANSITION_TICKS = 8;
    private final int size;
    private int selected;
    private int previous;
    private int cycleTicks;
    private int transitionTicks = TRANSITION_TICKS;

    public PonderGroupCycle(int size) {
        if (size < 2)
            throw new IllegalArgumentException("A carousel needs at least two members");
        this.size = size;
    }

    public void tick(boolean hovered) {
        if (hovered) {
            settle();
            return;
        }
        if (transitionTicks < TRANSITION_TICKS)
            transitionTicks++;
        if (++cycleTicks >= CYCLE_TICKS) {
            previous = selected;
            selected = (selected + 1) % size;
            cycleTicks = 0;
            transitionTicks = 0;
        }
    }

    public void select(int direction) {
        selected = Math.floorMod(selected + direction, size);
        cycleTicks = 0;
        settle();
    }

    public void settle() {
        previous = selected;
        transitionTicks = TRANSITION_TICKS;
    }

    public int selected() {
        return selected;
    }

    public int previous() {
        return previous;
    }

    public float blend(float partialTicks) {
        float progress = Math.clamp((transitionTicks + partialTicks) / TRANSITION_TICKS, 0, 1);
        return progress * progress * (3 - 2 * progress);
    }

    public Frame frame(float partialTicks) {
        float progress = blend(partialTicks);
        // Exchange the departing front card for the new rear card halfway through.
        // This keeps at most three cards visible and never superimposes two icons.
        return new Frame(progress, Math.max(0, 1 - 2 * progress), Math.max(0, 2 * progress - 1));
    }

    public record Frame(float progress, float outgoingAlpha, float incomingAlpha) {}
}
