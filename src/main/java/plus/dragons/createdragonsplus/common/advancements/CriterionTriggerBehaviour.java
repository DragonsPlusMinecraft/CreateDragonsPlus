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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import plus.dragons.createdragonsplus.common.CreateDragonsPlus;
import plus.dragons.createdragonsplus.util.ErrorMessages;

public class CriterionTriggerBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<CriterionTriggerBehaviour> TYPE =
            new BehaviourType<>(CreateDragonsPlus.asResource("criterion").toString());
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final MapCodec<UUID> UUID_CODEC = UUIDUtil.CODEC.optionalFieldOf("owner", null);
    private static final MapCodec<Map<BlockEntityBehaviourTrigger<?>, Object>> DATA_CODEC =
            Codec.<BlockEntityBehaviourTrigger<?>, Object>dispatchedMap(
                            BlockEntityBehaviourTrigger.CODEC, BlockEntityBehaviourTrigger::dataCodec)
                    .optionalFieldOf("data")
                    .xmap(
                            optional -> optional.orElse(new IdentityHashMap<>()),
                            map -> map.isEmpty() ? Optional.empty() : Optional.of(map));
    protected @Nullable UUID ownerId;
    protected Map<BlockEntityBehaviourTrigger<?>, Object> data;

    public CriterionTriggerBehaviour(SmartBlockEntity be) {
        super(be);
        this.data = new IdentityHashMap<>();
    }

    public void setOwner(@Nullable UUID ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwner(ServerPlayer player) {
        this.ownerId = player.getUUID();
    }

    @Nullable
    public ServerPlayer getOwner() {
        if (this.ownerId == null) return null;
        Level level = this.getWorld();
        if (level.isClientSide) return null;
        if (level.getPlayerByUUID(this.ownerId) instanceof ServerPlayer player) return player;
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getData(BlockEntityBehaviourTrigger<T> trigger) {
        return (T) data.get(trigger);
    }

    public <T> void setData(BlockEntityBehaviourTrigger<T> trigger, T data) {
        this.data.put(trigger, data);
    }

    protected void setData(Map<BlockEntityBehaviourTrigger<?>, Object> data) {
        this.data = data;
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
        UUID_CODEC.encode(this.ownerId, NbtOps.INSTANCE, builder);
        DATA_CODEC.encode(this.data, NbtOps.INSTANCE, builder);
        builder.build(new CompoundTag())
                .resultOrPartial(error -> LOGGER.error(
                        "Error encoding behavior data [" + getType().getName() + "] for " + ErrorMessages.blockEntity(blockEntity) + ": " + error))
                .ifPresent(nbt -> nbtIn.put(getType().getName(), nbt));
    }

    @Override
    public void read(CompoundTag nbtIn, Provider registries, boolean clientPacket) {
        super.read(nbtIn, registries, clientPacket);
        if (clientPacket) return;
        if (!nbtIn.contains(getType().getName())) return;
        CompoundTag nbt = nbtIn.getCompound(getType().getName());
        MapLike<Tag> mapLike = NbtOps.INSTANCE.getMap(nbt).getOrThrow();
        UUID_CODEC.decode(NbtOps.INSTANCE, mapLike).ifSuccess(this::setOwner);
        DATA_CODEC.decode(NbtOps.INSTANCE, mapLike).ifSuccess(this::setData);
    }
}
