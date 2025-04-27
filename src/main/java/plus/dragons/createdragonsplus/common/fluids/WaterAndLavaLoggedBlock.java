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

package plus.dragons.createdragonsplus.common.fluids;

import java.util.Optional;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public interface WaterAndLavaLoggedBlock extends BucketPickup, LiquidBlockContainer {
    EnumProperty<ContainedFluid> FLUID = EnumProperty.create("fluid", ContainedFluid.class);

    @Override
    default boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return fluid == Fluids.WATER || fluid == Fluids.LAVA;
    }

    @Override
    default boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        var containedFluid = state.getValue(FLUID);
        if (containedFluid != ContainedFluid.EMPTY)
            return false;
        var placedFluid = fluidState.getType();
        if (placedFluid == Fluids.WATER) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(FLUID, ContainedFluid.WATER), 3);
                level.scheduleTick(pos, placedFluid, placedFluid.getTickDelay(level));
            }
            return true;
        }
        if (placedFluid == Fluids.LAVA) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(FLUID, ContainedFluid.LAVA), 3);
                level.scheduleTick(pos, placedFluid, placedFluid.getTickDelay(level));
            }
            return true;
        }
        return false;
    }

    @Override
    default ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state) {
        var containedFluid = state.getValue(FLUID);
        if (containedFluid == ContainedFluid.EMPTY)
            return ItemStack.EMPTY;
        level.setBlock(pos, state.setValue(FLUID, ContainedFluid.EMPTY), 3);
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
        return containedFluid == ContainedFluid.WATER ? new ItemStack(Items.WATER_BUCKET) : new ItemStack(Items.LAVA_BUCKET);
    }

    @Override
    default Optional<SoundEvent> getPickupSound() {
        return Optional.empty();
    }

    @Override
    default Optional<SoundEvent> getPickupSound(BlockState state) {
        return switch (state.getValue(FLUID)) {
            case EMPTY -> Optional.empty();
            case WATER -> Fluids.WATER.getPickupSound();
            case LAVA -> Fluids.LAVA.getPickupSound();
        };
    }

    default FluidState fluidState(BlockState state) {
        return switch (state.getValue(FLUID)) {
            case EMPTY -> Fluids.EMPTY.defaultFluidState();
            case WATER -> Fluids.WATER.getSource(false);
            case LAVA -> Fluids.LAVA.getSource(false);
        };
    }

    default void updateFluid(LevelAccessor level, BlockState state, BlockPos pos) {
        switch (state.getValue(FLUID)) {
            case WATER -> level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            case LAVA -> level.scheduleTick(pos, Fluids.LAVA, Fluids.LAVA.getTickDelay(level));
        }
    }

    default BlockState withFluid(BlockState placementState, BlockPlaceContext ctx) {
        return withFluid(ctx.getLevel(), placementState, ctx.getClickedPos());
    }

    static BlockState withFluid(LevelAccessor level, BlockState placementState, BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos);
        if (placementState.isAir()) {
            return fluidState.isEmpty() ? placementState : fluidState.createLegacyBlock();
        }
        if (!(placementState.getBlock() instanceof WaterAndLavaLoggedBlock))
            return placementState;
        ContainedFluid containedFluid;
        if (fluidState.getType() == Fluids.WATER)
            containedFluid = ContainedFluid.WATER;
        else if (fluidState.getType() == Fluids.LAVA)
            containedFluid = ContainedFluid.LAVA;
        else containedFluid = ContainedFluid.EMPTY;
        return placementState.setValue(FLUID, containedFluid);
    }

    enum ContainedFluid implements StringRepresentable {
        EMPTY,
        WATER,
        LAVA;

        @Override
        public String getSerializedName() {
            return Lang.asId(name());
        }
    }
}
