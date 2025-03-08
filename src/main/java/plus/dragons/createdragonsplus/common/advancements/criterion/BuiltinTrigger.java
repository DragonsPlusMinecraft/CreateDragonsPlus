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

package plus.dragons.createdragonsplus.common.advancements.criterion;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

public class BuiltinTrigger implements CriterionTrigger<BuiltinTrigger>, CriterionTriggerInstance {
    private final Map<PlayerAdvancements, Set<Listener<BuiltinTrigger>>> listeners = Maps.newIdentityHashMap();
    private final Codec<BuiltinTrigger> codec = Codec.unit(this);

    public void trigger(ServerPlayer player) {
        var advancements = player.getAdvancements();
        if (this.listeners.containsKey(advancements)) {
            this.listeners.get(advancements).forEach(listener -> listener.run(advancements));
        }
    }

    @Override
    public final void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener<BuiltinTrigger> listener) {
        this.listeners.computeIfAbsent(playerAdvancements, it -> Sets.newHashSet()).add(listener);
    }

    @Override
    public final void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener<BuiltinTrigger> listener) {
        Set<CriterionTrigger.Listener<BuiltinTrigger>> set = this.listeners.get(playerAdvancements);
        if (set != null) {
            set.remove(listener);
            if (set.isEmpty()) {
                this.listeners.remove(playerAdvancements);
            }
        }
    }

    @Override
    public final void removePlayerListeners(PlayerAdvancements playerAdvancements) {
        this.listeners.remove(playerAdvancements);
    }

    @Override
    public Codec<BuiltinTrigger> codec() {
        return this.codec;
    }

    @Override
    public void validate(CriterionValidator validator) {}
}
