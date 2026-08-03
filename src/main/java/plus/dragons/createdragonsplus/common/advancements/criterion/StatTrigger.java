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
import com.google.gson.JsonObject;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.util.GsonHelper;
import plus.dragons.createdragonsplus.common.registry.CDPCriterions;

public class StatTrigger implements CriterionTrigger<StatTrigger.Instance> {
    private final ResourceLocation id;
    private final Table<PlayerAdvancements, Stat<?>, Set<Listener<Instance>>> listeners = Tables
            .newCustomTable(new IdentityHashMap<>(), IdentityHashMap::new);

    public StatTrigger(ResourceLocation id) {
        this.id = id;
    }

    public void trigger(ServerPlayer player, Stat<?> stat, int value) {
        PlayerAdvancements advancements = player.getAdvancements();
        Set<Listener<Instance>> statListeners = listeners.get(advancements, stat);
        if (statListeners == null || statListeners.isEmpty())
            return;
        for (Listener<Instance> listener : Set.copyOf(statListeners)) {
            if (listener.getTriggerInstance().bounds().matches(value))
                listener.run(advancements);
        }
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public Instance createInstance(JsonObject json, DeserializationContext context) {
        ResourceLocation typeId = new ResourceLocation(GsonHelper.getAsString(json, "type"));
        ResourceLocation valueId = new ResourceLocation(GsonHelper.getAsString(json, "value"));
        StatType<?> type = BuiltInRegistries.STAT_TYPE.get(typeId);
        if (type == null)
            throw new IllegalArgumentException("Unknown statistic type " + typeId);
        return new Instance(getStat(type, valueId), MinMaxBounds.Ints.fromJson(json.get("bounds")));
    }

    private static <T> Stat<T> getStat(StatType<T> type, ResourceLocation valueId) {
        T value = type.getRegistry().get(valueId);
        if (value == null)
            throw new IllegalArgumentException("Unknown statistic value " + valueId);
        return type.get(value);
    }

    @Override
    public void addPlayerListener(PlayerAdvancements advancements, Listener<Instance> listener) {
        Stat<?> stat = listener.getTriggerInstance().stat();
        Set<Listener<Instance>> statListeners = listeners.get(advancements, stat);
        if (statListeners == null) {
            statListeners = new HashSet<>();
            listeners.put(advancements, stat, statListeners);
        }
        statListeners.add(listener);
    }

    @Override
    public void removePlayerListener(PlayerAdvancements advancements, Listener<Instance> listener) {
        Stat<?> stat = listener.getTriggerInstance().stat();
        Set<Listener<Instance>> statListeners = listeners.get(advancements, stat);
        if (statListeners == null)
            return;
        statListeners.remove(listener);
        if (statListeners.isEmpty())
            listeners.remove(advancements, stat);
    }

    @Override
    public void removePlayerListeners(PlayerAdvancements advancements) {
        listeners.rowMap().remove(advancements);
    }

    public record Instance(Stat<?> stat, MinMaxBounds.Ints bounds) implements CriterionTriggerInstance {
        public static Instance of(Stat<?> stat, MinMaxBounds.Ints bounds) {
            return new Instance(stat, bounds);
        }

        public static Instance of(ResourceLocation stat, MinMaxBounds.Ints bounds) {
            return new Instance(Stats.CUSTOM.get(stat), bounds);
        }

        @Override
        public ResourceLocation getCriterion() {
            return CDPCriterions.STAT.getId();
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("type", BuiltInRegistries.STAT_TYPE.getKey(stat.getType()).toString());
            json.addProperty("value", getValueId(stat).toString());
            json.add("bounds", bounds.serializeToJson());
            return json;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static ResourceLocation getValueId(Stat<?> stat) {
            Registry registry = stat.getType().getRegistry();
            return registry.getKey(stat.getValue());
        }
    }
}
