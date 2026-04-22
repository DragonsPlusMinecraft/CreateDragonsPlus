package plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import plus.dragons.createdragonsplus.integration.simulated.api.fluids.tank.FragileFluidTankBreakEffectHandler;
import plus.dragons.createdragonsplus.integration.simulated.config.CDPSEConfig;

import java.util.Set;

public abstract class DefaultRangedEffectHandler implements FragileFluidTankBreakEffectHandler {
    @Override
    public void apply(Level level, BlockPos pos, Vector3d hitPos, FluidStack fluid) {
        double validRange = (double) fluid.getAmount() / CDPSEConfig.fluid().fragileFluidTankCapacity.get() * CDPSEConfig.fluid().fragileFluidTankAffectMaxRadius.get();
        var aabb = new AABB(pos).inflate(validRange);
        onHit(level, aabb, hitPos, fluid);
    }

    protected abstract void onHit(Level level, AABB aabb, Vector3d hitPos, FluidStack fluid);

    /** In most cases we don't need this here. But there is still case that entity is in sublevel such as painting and armor stand. <p>
     * So, a utility method is placed here just in case.
     */
    protected static boolean isEntityInRangeConsideringSubLevel(Level level, Entity entity, Vector3d hitPos, double range) {
        var helper = Sable.HELPER;
        if(helper.isInPlotGrid(entity)){
            return helper.distanceSquaredWithSubLevels(level, entity.position().x, entity.position().y, entity.position().z, hitPos.x, hitPos.y, hitPos.z) <= range * range ;
        } else {
            return entity.distanceToSqr(hitPos.x, hitPos.y, hitPos.z) <= range * range;
        }
    }

    public abstract static class AffectBlock extends DefaultRangedEffectHandler {

        @Override
        protected void onHit(Level level, AABB aabb, Vector3d hitPos, FluidStack fluid){
            double validRange = (double) fluid.getAmount() / CDPSEConfig.fluid().fragileFluidTankCapacity.get() * CDPSEConfig.fluid().fragileFluidTankAffectMaxRadius.get();
            var helper = Sable.HELPER;
            BlockPos.betweenClosedStream(aabb).forEach((pos) -> {
                if(pos.distToCenterSqr(hitPos.x, hitPos.y, hitPos.z) <= validRange * validRange)
                    onHitDoBlock(level,pos,level.getBlockState(pos),fluid);
                var subs = helper.getAllIntersecting(level, new BoundingBox3d(pos));
                subs.forEach((subLevel) -> {
                    var plot = subLevel.getPlot();
                    var embLevel = plot.getEmbeddedLevelAccessor();
                    Pose3dc subLevelPose = subLevel.logicalPose();
                    Vector3d subVec= subLevelPose.transformPositionInverse(new Vector3d(pos.getX(),pos.getY(),pos.getZ()));
                    var subPos = BlockPos.containing(subVec.x, subVec.y, subVec.z);
                    onHitDoBlock(embLevel.getLevel(),subPos,embLevel.getLevel().getBlockState(subPos),fluid);
                });
            });
            onHitDoRest(level,aabb,hitPos,fluid);
        }

        protected abstract void onHitDoBlock(Level level, BlockPos pos, BlockState state, FluidStack fluid);

        protected abstract void onHitDoRest(Level level, AABB aabb, Vector3d hitPos, FluidStack fluid);
    }
}
