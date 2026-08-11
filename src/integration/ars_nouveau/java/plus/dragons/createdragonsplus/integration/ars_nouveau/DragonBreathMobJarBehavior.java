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

package plus.dragons.createdragonsplus.integration.ars_nouveau;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.api.registry.JarBehaviorRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.config.CDPConfig;

public class DragonBreathMobJarBehavior extends JarBehavior<EnderDragon> {
    public static void register() {
        JarBehaviorRegistry.register(EntityType.ENDER_DRAGON, new DragonBreathMobJarBehavior());
    }

    @Override
    public void tick(MobJarTile tile) {
        var level = tile.getLevel();
        if (level == null || level.isClientSide || level.getGameTime() % 20 != 0 || !isEnabled())
            return;
        var handler = level.getCapability(Capabilities.FluidHandler.BLOCK, tile.getBlockPos(), null);
        if (handler == null || handler.getTanks() == 0)
            return;
        int stored = handler.getFluidInTank(0).getAmount();
        int generated = Math.min(CDPConfig.dragonBreath().mobJarGenerationAmount.get(),
                DragonBreathMobJarFluidHandler.CAPACITY - stored);
        if (generated > 0)
            handler.fill(new FluidStack((Fluid) CDPFluids.DRAGON_BREATH.getSource(), generated), FluidAction.EXECUTE);
    }

    @Override
    public void use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit, MobJarTile tile) {
        ItemStack emptyBucket = player.getItemInHand(hand);
        if (level.isClientSide || !emptyBucket.is(Items.BUCKET) || !isEnabled())
            return;
        var handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
        if (handler == null)
            return;
        int bucketVolume = FluidType.BUCKET_VOLUME;
        var requested = new FluidStack((Fluid) CDPFluids.DRAGON_BREATH.getSource(), bucketVolume);
        var simulated = handler.drain(requested, FluidAction.SIMULATE);
        if (!isFullBucket(simulated, bucketVolume))
            return;
        var drained = handler.drain(requested, FluidAction.EXECUTE);
        if (!isFullBucket(drained, bucketVolume))
            return;
        player.setItemInHand(hand, ItemUtils.createFilledResult(
                emptyBucket,
                player,
                new ItemStack(CDPFluids.DRAGON_BREATH.getBucket().get())));
        player.awardStat(Stats.ITEM_USED.get(Items.BUCKET));
        level.playSound(null, pos, SoundEvents.BUCKET_FILL_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
    }

    private static boolean isFullBucket(FluidStack stack, int bucketVolume) {
        return stack.getFluid() == CDPFluids.DRAGON_BREATH.getSource() && stack.getAmount() == bucketVolume;
    }

    private static boolean isEnabled() {
        return CDPConfig.features().dragonBreathFluid.get();
    }
}
