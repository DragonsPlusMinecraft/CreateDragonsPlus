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

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.StatAwardEvent;
import plus.dragons.createdragonsplus.common.advancements.criterion.StatTrigger.Instance;
import plus.dragons.createdragonsplus.common.registry.CDPCriterions;
import plus.dragons.createdragonsplus.util.CDPCodecs;

public class StatTrigger implements CriterionTrigger<Instance> {
    private final Table<PlayerAdvancements, Stat<?>, Set<Listener<Instance>>> listeners = Tables
            .newCustomTable(new IdentityHashMap<>(), IdentityHashMap::new);

    public StatTrigger() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public final void onStatAwardEvent(final StatAwardEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Stat<?> stat = event.getStat();
            PlayerAdvancements advancements = player.getAdvancements();
            var listeners = this.listeners.get(advancements, stat);
            if (listeners == null || listeners.isEmpty())
                return;
            int value = event.getValue();
            for (var listener : listeners) {
                var trigger = listener.trigger();
                if (trigger.bounds().matches(value)) {
                    listener.run(advancements);
                }
            }
        }
    }

    @Override
    public final void addPlayerListener(PlayerAdvancements advancements, CriterionTrigger.Listener<Instance> listener) {
        var stat = listener.trigger().stat;
        var set = this.listeners.get(advancements, stat);
        if (set == null) {
            set = new HashSet<>();
            this.listeners.put(advancements, stat, set);
        }
        set.add(listener);
    }

    @Override
    public final void removePlayerListener(PlayerAdvancements advancements, CriterionTrigger.Listener<Instance> listener) {
        var stat = listener.trigger().stat;
        var set = this.listeners.get(advancements, stat);
        if (set != null) {
            set.remove(listener);
            if (set.isEmpty())
                this.listeners.remove(advancements, stat);
        }
    }

    @Override
    public final void removePlayerListeners(PlayerAdvancements advancements) {
        this.listeners.rowMap().remove(advancements);
    }

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public record Instance(Stat<?> stat, MinMaxBounds.Ints bounds) implements CriterionTriggerInstance {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CDPCodecs.STAT.forGetter(Instance::stat),
                MinMaxBounds.Ints.CODEC.fieldOf("bounds").forGetter(Instance::bounds)).apply(instance, Instance::new));

        public static Criterion<Instance> of(Stat<?> stat, MinMaxBounds.Ints bounds){
            return CDPCriterions.STAT.get().createCriterion(new Instance(stat,bounds));
        }

        public static Criterion<Instance> of(ResourceLocation stat, MinMaxBounds.Ints bounds){
            return CDPCriterions.STAT.get().createCriterion(new Instance(Stats.CUSTOM.get(stat),bounds));
        }

        @Override
        public void validate(CriterionValidator validator) {}
    }
}
