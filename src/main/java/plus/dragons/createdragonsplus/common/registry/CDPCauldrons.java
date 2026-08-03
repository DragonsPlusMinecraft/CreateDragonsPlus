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

package plus.dragons.createdragonsplus.common.registry;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;

import com.tterrag.registrate.util.entry.BlockEntry;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import plus.dragons.createdragonsplus.common.fluids.dragonBreath.DragonBreathCauldronBlock;
import plus.dragons.createdragonsplus.config.CDPConfig;

public class CDPCauldrons {
    public static final Map<Item, CauldronInteraction> DRAGON_BREATH = CauldronInteraction.newInteractionMap();

    public static final BlockEntry<DragonBreathCauldronBlock> DRAGON_BREATH_CAULDRON = REGISTRATE
            .block("dragon_breath_cauldron", DragonBreathCauldronBlock::new)
            .initialProperties(() -> Blocks.CAULDRON)
            .transform(pickaxeOnly())
            .blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.get())
                    .partialState().with(DragonBreathCauldronBlock.LEVEL, 1)
                    .modelForState().modelFile(prov.models().getExistingFile(prov.modLoc("block/dragon_breath_cauldron_level1"))).addModel()
                    .partialState().with(DragonBreathCauldronBlock.LEVEL, 2)
                    .modelForState().modelFile(prov.models().getExistingFile(prov.modLoc("block/dragon_breath_cauldron_level2"))).addModel()
                    .partialState().with(DragonBreathCauldronBlock.LEVEL, 3)
                    .modelForState().modelFile(prov.models().getExistingFile(prov.modLoc("block/dragon_breath_cauldron_level3"))).addModel()
                    .partialState().with(DragonBreathCauldronBlock.LEVEL, 4)
                    .modelForState().modelFile(prov.models().getExistingFile(prov.modLoc("block/dragon_breath_cauldron_full"))).addModel())
            .loot((provider, block) -> provider.dropOther(block, Blocks.CAULDRON))
            .lang("Dragon's Breath Cauldron")
            .register();

    public static void register(IEventBus modBus) {
        modBus.register(CDPCauldrons.class);
    }

    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CDPCauldrons::registerInteractions);
    }

    private static void registerInteractions() {
        CauldronInteraction.EMPTY.put(CDPFluids.DRAGON_BREATH.getBucket().get(), CDPCauldrons::emptyDragonBreathBucketIntoEmptyCauldron);
        CauldronInteraction.EMPTY.put(Items.DRAGON_BREATH, CDPCauldrons::emptyDragonBreathBottleIntoEmptyCauldron);

        DRAGON_BREATH.put(Items.BUCKET, CDPCauldrons::fillDragonBreathBucket);
        DRAGON_BREATH.put(Items.GLASS_BOTTLE, CDPCauldrons::fillDragonBreathBottle);
        DRAGON_BREATH.put(Items.DRAGON_BREATH, CDPCauldrons::emptyDragonBreathBottleIntoDragonBreathCauldron);
    }

    private static InteractionResult emptyDragonBreathBucketIntoEmptyCauldron(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!isDragonBreathFluidEnabled())
            return InteractionResult.PASS;
        return emptyContainer(level,
                pos,
                player,
                hand,
                stack,
                new ItemStack(Items.BUCKET),
                DRAGON_BREATH_CAULDRON.get().defaultBlockState().setValue(DragonBreathCauldronBlock.LEVEL, DragonBreathCauldronBlock.MAX_LEVEL),
                SoundEvents.BUCKET_EMPTY_LAVA);
    }

    private static InteractionResult emptyDragonBreathBottleIntoEmptyCauldron(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!isDragonBreathFluidEnabled())
            return InteractionResult.PASS;
        return emptyContainer(level,
                pos,
                player,
                hand,
                stack,
                new ItemStack(Items.GLASS_BOTTLE),
                DRAGON_BREATH_CAULDRON.get().defaultBlockState(),
                SoundEvents.BOTTLE_EMPTY);
    }

    private static InteractionResult emptyDragonBreathBottleIntoDragonBreathCauldron(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!isDragonBreathFluidEnabled())
            return InteractionResult.PASS;
        if (state.getValue(DragonBreathCauldronBlock.LEVEL) == DragonBreathCauldronBlock.MAX_LEVEL)
            return InteractionResult.PASS;
        return emptyContainer(level,
                pos,
                player,
                hand,
                stack,
                new ItemStack(Items.GLASS_BOTTLE),
                DragonBreathCauldronBlock.raiseFillLevel(state),
                SoundEvents.BOTTLE_EMPTY);
    }

    private static InteractionResult fillDragonBreathBucket(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!isDragonBreathFluidEnabled())
            return InteractionResult.PASS;
        if (state.getValue(DragonBreathCauldronBlock.LEVEL) != DragonBreathCauldronBlock.MAX_LEVEL)
            return InteractionResult.PASS;
        return fillContainer(level,
                pos,
                player,
                hand,
                stack,
                new ItemStack(CDPFluids.DRAGON_BREATH.getBucket().get()),
                Blocks.CAULDRON.defaultBlockState(),
                SoundEvents.BUCKET_FILL_LAVA);
    }

    private static InteractionResult fillDragonBreathBottle(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!isDragonBreathFluidEnabled())
            return InteractionResult.PASS;
        return fillContainer(level,
                pos,
                player,
                hand,
                stack,
                new ItemStack(Items.DRAGON_BREATH),
                DragonBreathCauldronBlock.lowerFillLevel(state),
                SoundEvents.BOTTLE_FILL_DRAGONBREATH);
    }

    private static InteractionResult fillContainer(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack emptyStack, ItemStack filledStack, BlockState newState, SoundEvent sound) {
        if (!level.isClientSide) {
            Item item = emptyStack.getItem();
            player.setItemInHand(hand, ItemUtils.createFilledResult(emptyStack, player, filledStack));
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(item));
            level.setBlockAndUpdate(pos, newState);
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static InteractionResult emptyContainer(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack filledStack, ItemStack emptyStack, BlockState newState, SoundEvent sound) {
        if (!level.isClientSide) {
            Item item = filledStack.getItem();
            player.setItemInHand(hand, ItemUtils.createFilledResult(filledStack, player, emptyStack));
            player.awardStat(Stats.FILL_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(item));
            level.setBlockAndUpdate(pos, newState);
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static boolean isDragonBreathFluidEnabled() {
        return CDPConfig.features().dragonBreathFluid.get();
    }
}
