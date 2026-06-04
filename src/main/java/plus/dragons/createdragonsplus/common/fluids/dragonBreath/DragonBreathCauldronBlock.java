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

package plus.dragons.createdragonsplus.common.fluids.dragonBreath;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import plus.dragons.createdragonsplus.common.registry.CDPCauldrons;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.config.CDPConfig;

public class DragonBreathCauldronBlock extends AbstractCauldronBlock {
    public static final MapCodec<DragonBreathCauldronBlock> CODEC = simpleCodec(DragonBreathCauldronBlock::new);
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 1, 4);
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 4;
    private static final double CONTENT_HEIGHT_PER_LEVEL = 11.0 / 4.0;

    public DragonBreathCauldronBlock(Properties properties) {
        super(properties, CDPCauldrons.DRAGON_BREATH);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, MIN_LEVEL));
    }

    @Override
    protected MapCodec<? extends AbstractCauldronBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean isFull(BlockState state) {
        return state.getValue(LEVEL) == MAX_LEVEL;
    }

    @Override
    protected double getContentHeight(BlockState state) {
        return (4.0 + state.getValue(LEVEL) * CONTENT_HEIGHT_PER_LEVEL) / 16.0;
    }

    @Override
    protected boolean canReceiveStalactiteDrip(Fluid fluid) {
        return CDPConfig.features().dragonBreathFluid.get()
                && CDPConfig.features().dragonBreathFluidDripstoneDuplication.get()
                && fluid == CDPFluids.DRAGON_BREATH.getSource();
    }

    @Override
    protected void receiveStalactiteDrip(BlockState state, Level level, BlockPos pos, Fluid fluid) {
        if (!isFull(state)) {
            BlockState newState = state.setValue(LEVEL, state.getValue(LEVEL) + 1);
            level.setBlockAndUpdate(pos, newState);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
            level.levelEvent(1047, pos, 0);
        }
    }

    public static BlockState lowerFillLevel(BlockState state) {
        int level = state.getValue(LEVEL) - 1;
        return level == 0
                ? Blocks.CAULDRON.defaultBlockState()
                : state.setValue(LEVEL, level);
    }

    public static BlockState raiseFillLevel(BlockState state) {
        return state.setValue(LEVEL, Math.min(MAX_LEVEL, state.getValue(LEVEL) + 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(LEVEL);
    }
}
