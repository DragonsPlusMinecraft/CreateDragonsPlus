package plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockWithSubLevelCollisionCallback;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.physics.callback.FragileBlockCallback;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import plus.dragons.createdragonsplus.common.advancements.AdvancementBehaviour;
import plus.dragons.createdragonsplus.integration.simulated.api.fluids.tank.FragileFluidTankBreakEffectHandler;
import plus.dragons.createdragonsplus.integration.simulated.common.registry.CDPSEBlockEntities;

public class FragileFluidTankBlock extends Block implements IWrenchable, IBE<FragileFluidTankBlockEntity>, BlockWithSubLevelCollisionCallback {

    public FragileFluidTankBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(FragileFluidTankBlock::new);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        AdvancementBehaviour.setPlacedBy(level, pos, placer);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AllShapes.SPOUT;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return ComparatorUtil.levelOfSmartFluidTank(level, pos);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public Class<FragileFluidTankBlockEntity> getBlockEntityClass() {
        return FragileFluidTankBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FragileFluidTankBlockEntity> getBlockEntityType() {
        return CDPSEBlockEntities.FRAGILE_FLUID_TANK.get();
    }

    @Override
    public BlockSubLevelCollisionCallback sable$getCallback() {
        return new ImpactCallback();
    }

    private class ImpactCallback extends FragileBlockCallback {
        public CollisionResult onHit(final ServerLevel level, final BlockPos pos, final BlockState state, final Vector3d hitPos) {
            withBlockEntityDo(level, pos, (be) -> {
                if(!be.getFluidInTank().isEmpty()) {
                    var handler = FragileFluidTankBreakEffectHandler.REGISTRY.get(be.getFluidInTank().getFluid());
                    if(handler != null) {
                        var helper = Sable.HELPER;
                        var p = BlockPos.containing(helper.projectOutOfSubLevel(level, pos.getCenter()));
                        var hp = helper.projectOutOfSubLevel(level, hitPos);
                        handler.apply(level, p, hp, be.getFluidInTank());
                    }
                }
            });
            level.destroyBlock(pos, false);
            return new CollisionResult(JOMLConversion.ZERO, true);
        }
    }
}
