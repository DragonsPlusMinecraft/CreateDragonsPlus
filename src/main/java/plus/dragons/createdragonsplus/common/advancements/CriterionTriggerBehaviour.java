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

package plus.dragons.createdragonsplus.common.advancements;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import plus.dragons.createdragonsplus.common.CreateDragonsPlus;
import plus.dragons.createdragonsplus.common.advancements.criterion.BlockEntityBehaviourTrigger;
import plus.dragons.createdragonsplus.util.ErrorMessages;

public class CriterionTriggerBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<CriterionTriggerBehaviour> TYPE =
            new BehaviourType<>(CreateDragonsPlus.asResource("criterion").toString());
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final MapCodec<UUID> OWNER_CODEC = UUIDUtil.CODEC.optionalFieldOf("owner", null);
    private static final MapCodec<Map<BlockEntityBehaviourTrigger<?>, Object>> DATA_CODEC =
            Codec.<BlockEntityBehaviourTrigger<?>, Object>dispatchedMap(
                            BlockEntityBehaviourTrigger.CODEC, BlockEntityBehaviourTrigger::dataCodec)
                    .optionalFieldOf("data")
                    .xmap(
                            optional -> optional.orElse(new IdentityHashMap<>()),
                            map -> map.isEmpty() ? Optional.empty() : Optional.of(map));
    protected @Nullable UUID owner;
    protected final Map<BlockEntityBehaviourTrigger<?>, Object> defaultData;
    protected Map<BlockEntityBehaviourTrigger<?>, Object> data;

    protected CriterionTriggerBehaviour(SmartBlockEntity be, Map<BlockEntityBehaviourTrigger<?>, Object> defaultData) {
        super(be);
        this.defaultData = ImmutableMap.copyOf(defaultData);
        this.data = new IdentityHashMap<>(defaultData);
    }

    public static Builder builder(SmartBlockEntity blockEntity) {
        return new Builder(blockEntity);
    }

    public void setOwner(@Nullable UUID owner) {
        if (this.owner != owner) {
            this.owner = owner;
            this.data = new IdentityHashMap<>(this.defaultData);
            this.blockEntity.setChanged();
        }
    }

    public void setOwner(ServerPlayer player) {
        this.owner = player.getUUID();
        this.setOwner(player.getUUID());
    }

    @Nullable
    public ServerPlayer getOwner() {
        if (this.owner == null) return null;
        Level level = this.getWorld();
        if (level.isClientSide) return null;
        if (level.getPlayerByUUID(this.owner) instanceof ServerPlayer player) return player;
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getData(BlockEntityBehaviourTrigger<T> trigger) {
        if (data.containsKey(trigger)) return (T) data.get(trigger);
        return (T) this.defaultData.get(trigger);
    }

    public <T> void setData(BlockEntityBehaviourTrigger<T> trigger, T data) {
        if (!this.defaultData.containsKey(trigger)) {
            LOGGER.warn("Attempted to set data for unregistered trigger ["
                    + ErrorMessages.registry(BuiltInRegistries.TRIGGER_TYPES, trigger)
                    + "]");
            return;
        }
        this.data.put(trigger, data);
        this.blockEntity.setChanged();
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Override
    public void write(CompoundTag nbtIn, Provider registries, boolean clientPacket) {
        super.write(nbtIn, registries, clientPacket);
        if (clientPacket) return;
        var builder = NbtOps.INSTANCE.mapBuilder();
        OWNER_CODEC.encode(this.owner, NbtOps.INSTANCE, builder);
        DATA_CODEC.encode(this.data, NbtOps.INSTANCE, builder);
        builder.build(new CompoundTag())
                .resultOrPartial(error -> LOGGER.error("Error encoding behavior data ["
                        + getType().getName() + "] for " + ErrorMessages.blockEntity(blockEntity) + ": " + error))
                .ifPresent(nbt -> nbtIn.put(getType().getName(), nbt));
    }

    @Override
    public void read(CompoundTag nbtIn, Provider registries, boolean clientPacket) {
        super.read(nbtIn, registries, clientPacket);
        if (clientPacket) return;
        if (!nbtIn.contains(getType().getName())) return;
        CompoundTag nbt = nbtIn.getCompound(getType().getName());
        MapLike<Tag> mapLike = NbtOps.INSTANCE.getMap(nbt).getOrThrow();
        OWNER_CODEC.decode(NbtOps.INSTANCE, mapLike).ifSuccess(id -> this.owner = id);
        DATA_CODEC.decode(NbtOps.INSTANCE, mapLike).ifSuccess(data -> this.data = data);
        this.data.keySet().removeIf(trigger -> !this.defaultData.containsKey(trigger));
    }

    public static class Builder {
        private final SmartBlockEntity blockEntity;
        private final Map<BlockEntityBehaviourTrigger<?>, Object> data = new IdentityHashMap<>();

        private Builder(SmartBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        public <T> Builder add(BlockEntityBehaviourTrigger<T> trigger, T defaultValue) {
            this.data.put(trigger, defaultValue);
            return this;
        }

        public CriterionTriggerBehaviour build() {
            return new CriterionTriggerBehaviour(this.blockEntity, this.data);
        }
    }
}
