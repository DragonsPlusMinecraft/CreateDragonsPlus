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

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.advancements.criterion.BuiltinTrigger;
import plus.dragons.createdragonsplus.common.advancements.criterion.StatTrigger;
import plus.dragons.createdragonsplus.util.CDPCodecs;
import plus.dragons.createdragonsplus.util.ErrorMessages;

/**
 * {@link BlockEntityBehaviour} for awarding owner player advancements through stats and builtin triggers. <br>
 * The owner player should be set using {@link #setPlacedBy(Level, BlockPos, LivingEntity)} in
 * {@link Block#setPlacedBy(Level, BlockPos, BlockState, LivingEntity, ItemStack)}. <br>
 * Stats will be stored if owner player is not available, and will add to next success award. <br>
 * @see BuiltinTrigger
 * @see StatTrigger
 */
public class AdvancementBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<AdvancementBehaviour> TYPE =
            new BehaviourType<>(CDPCommon.asResource("advancement").toString());
    protected static final String TYPE_KEY = TYPE.getName();
    protected static final String OWNER_KEY = "Owner";
    protected static final String STATS_COUNTER_KEY = "StatsCounter";
    protected static final Codec<Object2IntMap<Stat<?>>> STATS_COUNTER_CODEC = RecordCodecBuilder
            .<Pair<Stat<?>, Integer>>create(instance -> instance.group(
                    CDPCodecs.STAT.forGetter(Pair::getFirst),
                    Codec.INT.fieldOf("count").forGetter(Pair::getSecond)
            ).apply(instance, Pair::of)).listOf().xmap(
                    list -> {
                        Object2IntMap<Stat<?>> map = new Object2IntOpenHashMap<>();
                        list.forEach(entry -> map.put(entry.getFirst(), entry.getSecond().intValue()));
                        return map;
                    },
                    map -> {
                        ImmutableList.Builder<Pair<Stat<?>, Integer>> builder = ImmutableList.builder();
                        map.forEach((stat, count) -> builder.add(Pair.of(stat, count)));
                        return builder.build();
                    }
            );
    private static final Logger LOGGER = LogUtils.getLogger();
    protected @Nullable UUID owner;
    protected Object2IntMap<Stat<?>> statsCounter = new Object2IntOpenHashMap<>();

    public AdvancementBehaviour(SmartBlockEntity be) {
        super(be);
    }

    public static void setPlacedBy(Level level, BlockPos pos, LivingEntity entity) {
        AdvancementBehaviour behaviour = get(level, pos, TYPE);
        if (behaviour != null && entity instanceof ServerPlayer player)
            behaviour.setOwner(player);
    }

    public void setOwner(@Nullable UUID owner) {
        if (owner != this.owner) {
            this.statsCounter.clear();
        }
        this.owner = owner;
    }

    public void setOwner(ServerPlayer player) {
        if (player instanceof FakePlayer)
            return;
        this.setOwner(player.getUUID());
    }

    public @Nullable ServerPlayer getOwner() {
        if (this.owner == null) return null;
        if (this.getWorld().getPlayerByUUID(this.owner) instanceof ServerPlayer player)
            return player instanceof FakePlayer ? null : player;
        return null;
    }

    public void trigger(BuiltinTrigger trigger) {
        ServerPlayer owner = this.getOwner();
        if (owner != null) trigger.trigger(owner);
    }

    public void awardStat(Stat<?> stat, int count) {
        ServerPlayer owner = this.getOwner();
        if (owner == null) {
            if (this.owner != null)
                this.statsCounter.computeInt(stat, (k, v) -> v + count);
            return;
        }
        owner.awardStat(stat, count + this.statsCounter.removeInt(stat));
    }

    public void awardStat(ResourceLocation stat, int count) {
        awardStat(Stats.CUSTOM.get(stat),count);
    }

    public void resetStat(Stat<?> stat) {
        this.statsCounter.removeInt(stat);
        ServerPlayer owner = this.getOwner();
        if (owner != null) owner.resetStat(stat);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Override
    public void read(CompoundTag nbt, Provider registries, boolean clientPacket) {
        if (clientPacket)
            return;

        if (!nbt.contains(OWNER_KEY)) {
            this.setOwner((UUID) null);
            return;
        }
        this.setOwner(nbt.getUUID(OWNER_KEY));

        if (!nbt.contains(STATS_COUNTER_KEY)) {
            this.statsCounter.clear();
            return;
        }
        STATS_COUNTER_CODEC.parse(NbtOps.INSTANCE, nbt.get(STATS_COUNTER_KEY))
                .resultOrPartial(this::errorReading)
                .ifPresent(map -> this.statsCounter = map);
    }

    @Override
    public void write(CompoundTag nbt, Provider registries, boolean clientPacket) {
        if (clientPacket)
            return;

        if (this.owner != null)
            nbt.putUUID(OWNER_KEY, this.owner);

        if (!this.statsCounter.isEmpty()) {
            STATS_COUNTER_CODEC.encodeStart(NbtOps.INSTANCE, this.statsCounter)
                    .resultOrPartial(this::errorWriting)
                    .ifPresent(it -> nbt.put(STATS_COUNTER_KEY, it));
        }
    }

    protected void errorReading(String error) {
        error = "Error reading " + this.getClass().getSimpleName() + " for block entity: " +
                ErrorMessages.blockEntity(this.blockEntity) + ", caused by: " + error;
        LOGGER.error(error);
    }

    protected void errorWriting(String error) {
        error = "Error writing " + this.getClass().getSimpleName() + " for block entity: " +
                ErrorMessages.blockEntity(this.blockEntity) + ", caused by: " + error;
        LOGGER.error(error);
    }
}
