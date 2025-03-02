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
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import plus.dragons.createdragonsplus.common.advancements.CriterionTriggerBehaviour;
import plus.dragons.createdragonsplus.util.ErrorMessages;

public abstract class BlockEntityBehaviourTrigger<T>
        implements CriterionTrigger<BlockEntityBehaviourTrigger<T>>, CriterionTriggerInstance {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<BlockEntityBehaviourTrigger<?>> CODEC = BuiltInRegistries.TRIGGER_TYPES
            .byNameCodec()
            .comapFlatMap(
                    trigger -> trigger instanceof BlockEntityBehaviourTrigger<?>
                            ? DataResult.success((BlockEntityBehaviourTrigger<?>) trigger)
                            : DataResult.error(() -> "Unsupported trigger type: "
                                    + ErrorMessages.registry(BuiltInRegistries.TRIGGER_TYPES, trigger)),
                    Function.identity());
    private final Map<PlayerAdvancements, Set<Listener<BlockEntityBehaviourTrigger<T>>>> listeners =
            Maps.newIdentityHashMap();
    private final Codec<T> dataCodec;

    public BlockEntityBehaviourTrigger(Codec<T> dataCodec) {
        this.dataCodec = dataCodec;
    }

    protected abstract boolean test(ServerPlayer player, SmartBlockEntity blockEntity, T data);

    public final Codec<T> dataCodec() {
        return this.dataCodec;
    }

    @SuppressWarnings("unchecked")
    public final DataResult<Tag> serialize(Object data) {
        return this.dataCodec().encodeStart(NbtOps.INSTANCE, (T) data);
    }

    public final DataResult<T> deserialize(Tag nbt) {
        return this.dataCodec().parse(NbtOps.INSTANCE, nbt);
    }

    public final void trigger(SmartBlockEntity blockEntity) {
        CriterionTriggerBehaviour behaviour = blockEntity.getBehaviour(CriterionTriggerBehaviour.TYPE);
        if (behaviour == null) return;
        ServerPlayer player = behaviour.getOwner();
        if (player == null) return;
        PlayerAdvancements advancements = player.getAdvancements();
        var listeners = this.listeners.get(advancements);
        if (listeners == null) return;
        T data = behaviour.getData(this);
        if (data == null) return;
        if (this.test(player, blockEntity, data)) {
            for (var listener : listeners) {
                listener.run(advancements);
            }
        }
    }

    @Override
    public final void addPlayerListener(
            PlayerAdvancements advancements, Listener<BlockEntityBehaviourTrigger<T>> listener) {
        if (listener.trigger() != this) throw new IllegalStateException("Unsupported listener " + listener);
        // This trigger is designed to be primarily used by single unique advancement.
        // Thus expecting only 1 listener here by default.
        this.listeners
                .computeIfAbsent(advancements, ignored -> new ObjectArraySet<>(1))
                .add(listener);
    }

    @Override
    public final void removePlayerListener(
            PlayerAdvancements advancements, Listener<BlockEntityBehaviourTrigger<T>> listener) {
        var listeners = this.listeners.get(advancements);
        if (listeners == null) return;
        if (listeners.size() == 1) {
            this.listeners.remove(advancements);
            return;
        }
        listeners.remove(listener);
    }

    @Override
    public final void removePlayerListeners(PlayerAdvancements advancements) {
        this.listeners.remove(advancements);
    }

    @Override
    public final Codec<BlockEntityBehaviourTrigger<T>> codec() {
        return Codec.unit(this);
    }

    @Override
    public final void validate(CriterionValidator validator) {}

    public final String toString() {
        return this.getClass().getSimpleName() + "[" + BuiltInRegistries.TRIGGER_TYPES.getId(this) + "]";
    }
}
