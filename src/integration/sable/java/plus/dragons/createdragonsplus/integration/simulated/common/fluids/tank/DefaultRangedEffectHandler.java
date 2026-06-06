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

package plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3d;
import plus.dragons.createdragonsplus.integration.simulated.api.fluids.tank.FragileFluidTankBreakEffectHandler;
import plus.dragons.createdragonsplus.integration.simulated.api.fluids.tank.FragileFluidTankImpactContext;

public abstract class DefaultRangedEffectHandler implements FragileFluidTankBreakEffectHandler {
    @Override
    public void apply(FragileFluidTankImpactContext context) {
        onHit(context);
    }

    protected abstract void onHit(FragileFluidTankImpactContext context);

    /**
     * In most cases we don't need this here. But there is still case that entity is in sublevel such as painting and armor stand. <p>
     * So, a utility method is placed here just in case.
     */
    protected static boolean isEntityInRangeConsideringSubLevel(FragileFluidTankImpactContext context, Entity entity) {
        var helper = Sable.HELPER;
        if (helper.isInPlotGrid(entity)) {
            return helper.distanceSquaredWithSubLevels(context.level(), entity.position().x, entity.position().y, entity.position().z, context.hitPos().x, context.hitPos().y, context.hitPos().z) <= context.radius() * context.radius();
        } else {
            return entity.distanceToSqr(context.hitPos().x, context.hitPos().y, context.hitPos().z) <= context.radius() * context.radius();
        }
    }

    protected static <T extends Entity> void forEntitiesInRange(FragileFluidTankImpactContext context, Class<T> entityClass, Predicate<T> predicate, Consumer<T> consumer) {
        context.level().getEntitiesOfClass(entityClass, context.area(), entity -> isEntityInRangeConsideringSubLevel(context, entity) && predicate.test(entity))
                .forEach(consumer);
    }

    public abstract static class AffectBlock extends DefaultRangedEffectHandler {
        @Override
        protected void onHit(FragileFluidTankImpactContext context) {
            var helper = Sable.HELPER;
            BlockPos.betweenClosedStream(context.area()).forEach((pos) -> {
                if (pos.distToCenterSqr(context.hitPos().x, context.hitPos().y, context.hitPos().z) > context.radius() * context.radius())
                    return;
                onHitDoBlock(context.level(), pos, context.level().getBlockState(pos), context.fluid());
                var subs = helper.getAllIntersecting(context.level(), new BoundingBox3d(pos));
                subs.forEach((subLevel) -> {
                    var plot = subLevel.getPlot();
                    var embLevel = plot.getEmbeddedLevelAccessor();
                    Pose3dc subLevelPose = subLevel.logicalPose();
                    Vector3d subVec = subLevelPose.transformPositionInverse(new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
                    var subPos = BlockPos.containing(subVec.x, subVec.y, subVec.z);
                    onHitDoBlock(embLevel.getLevel(), subPos, embLevel.getLevel().getBlockState(subPos), context.fluid());
                });
            });
            onHitDoRest(context);
        }

        protected abstract void onHitDoBlock(Level level, BlockPos pos, BlockState state, FluidStack fluid);

        protected abstract void onHitDoRest(FragileFluidTankImpactContext context);
    }
}
