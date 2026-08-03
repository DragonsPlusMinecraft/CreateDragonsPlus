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

package plus.dragons.createdragonsplus.common.fluids.hatch;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.FluidHelper.FluidExchange;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.fluids.hatch.FluidHatchItemFluidTransfer.TransferResult;
import plus.dragons.createdragonsplus.common.registry.CDPBlockEntities;

public class FluidHatchBlock extends HorizontalDirectionalBlock implements IBE<FluidHatchBlockEntity>, IWrenchable, ProperWaterloggedBlock {
    public FluidHatchBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, WATERLOGGED));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null)
            return null;
        if (context.getClickedFace().getAxis().isVertical())
            return null;
        return withWater(state.setValue(FACING, context.getClickedFace().getOpposite()), context);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return fluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        updateWater(level, state, pos);
        return state;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty())
            return super.use(state, level, pos, player, hand, hitResult);
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        if (player instanceof FakePlayer)
            return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(pos.relative(state.getValue(FACING)));
        if (blockEntity == null)
            return InteractionResult.FAIL;

        IFluidHandler tankCapability = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
        if (tankCapability == null)
            return InteractionResult.FAIL;

        FilteringBehaviour filter = BlockEntityBehaviour.get(level, pos, FilteringBehaviour.TYPE);
        if (filter == null)
            return InteractionResult.FAIL;

        FluidExchange exchange;
        FluidStack fluidStack;
        if (player.isSecondaryUseActive()) {
            if (!(fluidStack = tryFillItem(level, player, hand, stack, blockEntity, tankCapability, filter)).isEmpty()) {
                exchange = FluidExchange.TANK_TO_ITEM;
            } else if (!(fluidStack = tryEmptyItem(level, player, hand, stack, blockEntity, tankCapability, filter)).isEmpty()) {
                exchange = FluidExchange.ITEM_TO_TANK;
            } else {
                exchange = null;
            }
        } else {
            if (!(fluidStack = tryEmptyItem(level, player, hand, stack, blockEntity, tankCapability, filter)).isEmpty()) {
                exchange = FluidExchange.ITEM_TO_TANK;
            } else if (!(fluidStack = tryFillItem(level, player, hand, stack, blockEntity, tankCapability, filter)).isEmpty()) {
                exchange = FluidExchange.TANK_TO_ITEM;
            } else {
                exchange = null;
            }
        }
        if (exchange == null) {
            if (canItemBeEmptied(level, stack) || canItemBeFilled(level, stack))
                return InteractionResult.SUCCESS;
            return InteractionResult.FAIL;
        }

        SoundEvent soundevent = switch (exchange) {
            case ITEM_TO_TANK -> FluidHelper.getEmptySound(fluidStack);
            case TANK_TO_ITEM -> FluidHelper.getFillSound(fluidStack);
        };
        if (soundevent != null && !level.isClientSide) {
            float pitch = Mth.clamp(1 - (fluidStack.getAmount() / (FluidTankBlockEntity.getCapacityMultiplier() * 16f)), 0, 1);
            pitch /= 1.5f;
            pitch += .5f;
            pitch += (level.random.nextFloat() - .5f) / 4f;
            level.playSound(null, pos, soundevent, SoundSource.BLOCKS, .5f, pitch);
        }

        return InteractionResult.SUCCESS;
    }

    public FluidStack tryEmptyItem(
            Level level, Player player, InteractionHand hand, ItemStack stack,
            BlockEntity blockEntity, IFluidHandler capability, FilteringBehaviour filter) {
        ItemStack transferredStack = stack.copy();
        TransferResult transfer = FluidHatchItemFluidTransfer.tryDrainItemToTank(transferredStack, capability, filter);
        if (!transfer.isEmpty()) {
            blockEntity.setChanged();
            if (level instanceof ServerLevel serverLevel)
                serverLevel.getChunkSource().blockChanged(blockEntity.getBlockPos());

            if (!player.isCreative() && !(blockEntity instanceof CreativeFluidTankBlockEntity))
                replaceItem(player, hand, transferredStack, transfer.result());
            return transfer.fluidStack();
        }

        if (!GenericItemEmptying.canItemBeEmptied(level, stack))
            return FluidStack.EMPTY;

        Pair<FluidStack, ItemStack> emptying = GenericItemEmptying.emptyItem(level, stack, true);
        FluidStack fluidStack = emptying.getFirst();

        if (!filter.test(fluidStack))
            return FluidStack.EMPTY;

        if (fluidStack.getAmount() != capability.fill(fluidStack, FluidAction.SIMULATE))
            return FluidStack.EMPTY;
        if (level.isClientSide)
            return fluidStack;

        ItemStack copy = stack.copy();
        emptying = GenericItemEmptying.emptyItem(level, copy, false);

        // Prevent special cap behavior interrupting insert fluid.
        int realFill = capability.fill(fluidStack.copy(), FluidAction.SIMULATE);
        if (realFill == 0) return fluidStack;
        capability.fill(fluidStack.copy(), FluidAction.EXECUTE);
        blockEntity.setChanged();

        if (level instanceof ServerLevel serverLevel)
            serverLevel.getChunkSource().blockChanged(blockEntity.getBlockPos());

        if (!player.isCreative() && !(blockEntity instanceof CreativeFluidTankBlockEntity)) {
            replaceItem(player, hand, copy, emptying.getSecond());
        }
        return fluidStack;
    }

    public FluidStack tryFillItem(Level level, Player player, InteractionHand hand, ItemStack stack, BlockEntity blockEntity, IFluidHandler capability, FilteringBehaviour filter) {
        FluidStack fluidStack = tryFillItemWithExtraHandler(level, player, hand, stack, blockEntity, capability, filter);
        if (!fluidStack.isEmpty())
            return fluidStack;

        fluidStack = tryFillItemWithFillingRecipe(level, player, hand, stack, blockEntity, capability, filter);
        if (!fluidStack.isEmpty())
            return fluidStack;

        ItemStack transferredStack = stack.copy();
        TransferResult transfer = FluidHatchItemFluidTransfer.tryFillItemFromTank(transferredStack, capability, filter);
        if (!transfer.isEmpty()) {
            blockEntity.setChanged();
            if (level instanceof ServerLevel serverLevel)
                serverLevel.getChunkSource().blockChanged(blockEntity.getBlockPos());

            if (!player.isCreative())
                replaceItem(player, hand, transferredStack, transfer.result());
            return transfer.fluidStack();
        }

        if (!GenericItemFilling.canItemBeFilled(level, stack))
            return FluidStack.EMPTY;

        for (int i = 0; i < capability.getTanks(); i++) {
            fluidStack = capability.getFluidInTank(i);
            if (fluidStack.isEmpty() || !filter.test(fluidStack))
                continue;
            int requiredAmountForItem = FluidHatchItemFilling.getRequiredAmountForItem(level, stack, fluidStack.copy());
            if (requiredAmountForItem == -1)
                continue;
            if (requiredAmountForItem > fluidStack.getAmount())
                continue;

            FluidStack fluidCopy = fluidStack.copy();
            fluidCopy.setAmount(requiredAmountForItem);

            FluidStack realDraw = capability.drain(fluidCopy, FluidAction.SIMULATE);
            if (realDraw.isEmpty() || realDraw.getAmount() != requiredAmountForItem)
                continue;

            if (level.isClientSide)
                return fluidCopy;

            ItemStack workingStack = player.isCreative() || blockEntity instanceof CreativeFluidTankBlockEntity
                    ? stack.copy()
                    : stack;
            ItemStack result = FluidHatchItemFilling.fillItem(level, requiredAmountForItem, workingStack, fluidStack.copy());
            if (result.isEmpty())
                continue;
            capability.drain(fluidCopy, FluidAction.EXECUTE);

            if (!player.isCreative())
                replaceItem(player, hand, workingStack, result);
            blockEntity.setChanged();
            if (level instanceof ServerLevel serverLevel)
                serverLevel.getChunkSource().blockChanged(blockEntity.getBlockPos());
            return fluidCopy;
        }
        return FluidStack.EMPTY;
    }

    private FluidStack tryFillItemWithFillingRecipe(
            Level level, Player player, InteractionHand hand, ItemStack stack,
            BlockEntity blockEntity, IFluidHandler capability, FilteringBehaviour filter) {
        for (int i = 0; i < capability.getTanks(); i++) {
            FluidStack fluidStack = capability.getFluidInTank(i);
            if (fluidStack.isEmpty() || !filter.test(fluidStack))
                continue;

            var requiredAmount = FluidHatchFillingRecipeTransfer.getRequiredAmountForItem(level, stack, fluidStack.copy());
            if (requiredAmount.isEmpty())
                continue;
            int requiredAmountForItem = requiredAmount.getAsInt();
            if (requiredAmountForItem > fluidStack.getAmount())
                continue;

            FluidStack fluidCopy = fluidStack.copy();
            fluidCopy.setAmount(requiredAmountForItem);

            FluidStack realDraw = capability.drain(fluidCopy, FluidAction.SIMULATE);
            if (realDraw.isEmpty() || realDraw.getAmount() != requiredAmountForItem)
                continue;

            if (level.isClientSide)
                return fluidCopy;

            ItemStack workingStack = player.isCreative() || blockEntity instanceof CreativeFluidTankBlockEntity
                    ? stack.copy()
                    : stack;
            var result = FluidHatchFillingRecipeTransfer.fillItem(level, requiredAmountForItem, workingStack, fluidStack.copy());
            if (result.isEmpty())
                continue;

            capability.drain(fluidCopy, FluidAction.EXECUTE);

            if (!player.isCreative())
                replaceItem(player, hand, workingStack, result.get());
            blockEntity.setChanged();
            if (level instanceof ServerLevel serverLevel)
                serverLevel.getChunkSource().blockChanged(blockEntity.getBlockPos());
            return fluidCopy;
        }
        return FluidStack.EMPTY;
    }

    private FluidStack tryFillItemWithExtraHandler(
            Level level, Player player, InteractionHand hand, ItemStack stack,
            BlockEntity blockEntity, IFluidHandler capability, FilteringBehaviour filter) {
        for (int i = 0; i < capability.getTanks(); i++) {
            FluidStack fluidStack = capability.getFluidInTank(i);
            if (fluidStack.isEmpty() || !filter.test(fluidStack))
                continue;

            var requiredAmount = FluidHatchItemFilling.getRequiredAmountForExtraHandler(stack, fluidStack.copy());
            if (requiredAmount.isEmpty())
                continue;
            int requiredAmountForItem = requiredAmount.getAsInt();
            if (requiredAmountForItem > fluidStack.getAmount())
                continue;

            FluidStack fluidCopy = fluidStack.copy();
            fluidCopy.setAmount(requiredAmountForItem);

            FluidStack realDraw = capability.drain(fluidCopy, FluidAction.SIMULATE);
            if (realDraw.isEmpty() || realDraw.getAmount() != requiredAmountForItem)
                continue;

            if (level.isClientSide)
                return fluidCopy;

            ItemStack workingStack = player.isCreative() || blockEntity instanceof CreativeFluidTankBlockEntity
                    ? stack.copy()
                    : stack;
            var result = FluidHatchItemFilling.fillItemWithExtraHandler(requiredAmountForItem, workingStack, fluidStack.copy());
            if (result.isEmpty())
                continue;
            capability.drain(fluidCopy, FluidAction.EXECUTE);

            if (!player.isCreative())
                replaceItem(player, hand, workingStack, result.get());
            blockEntity.setChanged();
            if (level instanceof ServerLevel serverLevel)
                serverLevel.getChunkSource().blockChanged(blockEntity.getBlockPos());
            return fluidCopy;
        }
        return FluidStack.EMPTY;
    }

    private static void replaceItem(Player player, InteractionHand hand, ItemStack stack, ItemStack result) {
        if (stack.isEmpty()) {
            player.setItemInHand(hand, result);
        } else {
            player.setItemInHand(hand, stack);
            player.getInventory().placeItemBackInInventory(result);
        }
    }

    private static boolean canItemBeEmptied(Level level, ItemStack stack) {
        return GenericItemEmptying.canItemBeEmptied(level, stack)
                || FluidHatchItemFluidTransfer.canItemBeEmptied(stack);
    }

    private static boolean canItemBeFilled(Level level, ItemStack stack) {
        return FluidHatchFillingRecipeTransfer.canItemBeFilled(level, stack)
                || FluidHatchItemFluidTransfer.canItemBeFilled(stack)
                || GenericItemFilling.canItemBeFilled(level, stack);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AllShapes.ITEM_HATCH.get(state.getValue(FACING).getOpposite());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    public Class<FluidHatchBlockEntity> getBlockEntityClass() {
        return FluidHatchBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FluidHatchBlockEntity> getBlockEntityType() {
        return CDPBlockEntities.FLUID_HATCH.get();
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType pathComputationType) {
        return false;
    }
}
