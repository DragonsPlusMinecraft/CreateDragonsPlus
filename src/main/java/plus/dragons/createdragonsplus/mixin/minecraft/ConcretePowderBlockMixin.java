package plus.dragons.createdragonsplus.mixin.minecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;

import javax.annotation.Nullable;


@Mixin(ConcretePowderBlock.class)
public abstract class ConcretePowderBlockMixin extends FallingBlock {

    public ConcretePowderBlockMixin(Properties properties) {
        super(properties);
    }

    @Shadow @Final
    private Block concrete;

    @Inject(method = "updateShape", at = @At("HEAD"), cancellable = true)
    private void updateShape$handleDyeLiquidInteraction(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos, CallbackInfoReturnable<BlockState> cir) {
        var result = createDragonsPlus$getProperSolidified(level,pos,concrete);
        if(result != null) {
            cir.setReturnValue(result);
        }
    }

    @Inject(method = "onLand", at = @At("HEAD"), cancellable = true)
    private void onLand$handleDyeLiquidInteraction(Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock, CallbackInfo ci) {
        var result = createDragonsPlus$getProperSolidified(level,pos,concrete);
        if(result != null) {
            level.setBlock(pos, result, 3);
            ci.cancel();
        }
    }

    @Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true)
    private void getStateForPlacement$handleDyeLiquidInteraction(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        var result = createDragonsPlus$getProperSolidified(context.getLevel(),context.getClickedPos(),concrete);
        if(result != null) {
            cir.setReturnValue(result);
        }
    }

    @Unique
    @Nullable
    private static BlockState createDragonsPlus$getProperSolidified(LevelAccessor level, BlockPos pos, Block concrete) {
        BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();
        for(Direction direction : Direction.values()) {
            if(direction == Direction.DOWN) continue;
            mutableBlockPos.setWithOffset(pos, direction);
            var fluid = level.getBlockState(mutableBlockPos).getFluidState();
            if(fluid.is(CDPFluids.COMMON_TAGS.dyes)){
                var coloredConcrete = BuiltInRegistries.BLOCK.getOptional(
                        ResourceLocation.withDefaultNamespace(BuiltInRegistries.FLUID.getKey(fluid.getType()).getPath().replace("_dye","_concrete").replace("flowing_","")));
                return coloredConcrete.orElse(concrete).defaultBlockState();
            }
        }
        return null;
    }

    @Override
    public boolean canBeHydrated(BlockState state, BlockGetter getter, BlockPos pos, FluidState fluid, BlockPos fluidPos) {
        if(fluid.is(CDPFluids.COMMON_TAGS.dyes)) return true;
        return fluid.canHydrate(getter, fluidPos, state, pos);
    }
}
