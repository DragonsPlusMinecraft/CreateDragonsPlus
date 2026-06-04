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

package plus.dragons.createdragonsplus.mixin.simulated;

import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import java.util.List;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import plus.dragons.createdragonsplus.common.kinetics.fan.AirCurrentSegmentAccess;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.integration.simulated.api.kinetics.fan.FanProcessingTypeSimulatedExtension;

@Restriction(require = @Condition(ModIntegration.Constants.SABLE))
@Mixin(AirCurrent.class)
public class AirCurrentMixinForSimulatedBehaviour {
    @Shadow
    @Final
    public IAirCurrentSource source;
    @Shadow
    public List<AirCurrentSegmentAccess> segments;
    @Shadow
    public Direction direction;

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tick$tickAirCurrentBlockHit(CallbackInfo ci) {
        if (!CDPConfig.features().airCurrentBlockInteraction.get()) return;
        if (!(source instanceof SmartBlockEntity be)) return;
        if (be.isVirtual() || be.getLevel().isClientSide) return;
        ActiveSableCompanion helper = Sable.HELPER;
        var subLevel = helper.getContaining((BlockEntity) source);
        if (subLevel == null)
            return;
        Pose3dc pose = subLevel.logicalPose();
        segments.forEach(seg -> {
            var type = seg.getType();
            if (type == null || !(type instanceof FanProcessingTypeSimulatedExtension extendType) || !extendType.active()) return;
            for (int i = seg.getStartOffset(); i < seg.getEndOffset(); i++) {
                var currentPos = source.getAirCurrentPos().relative(direction, i);
                var position = currentPos.getCenter();
                position = pose.transformPosition(position);
                var mainWorldPos = BlockPos.containing(position.x, position.y, position.z);
                var level = ((BlockEntity) source).getLevel();
                var blockState = level.getBlockState(mainWorldPos);
                if (extendType.canAffectBlock(level, mainWorldPos, blockState)) {
                    extendType.affectBlock(level, mainWorldPos, blockState);
                }
                var subLevelsToCheck = helper.getAllIntersecting(level, new BoundingBox3d(mainWorldPos));
                Vec3 finalPosition = position;
                subLevelsToCheck.forEach(sb -> {
                    if (sb != subLevel) {
                        var plot = sb.getPlot();
                        var embLevel = plot.getEmbeddedLevelAccessor();
                        Pose3dc sbPose = sb.logicalPose();
                        var inSBPosition = sbPose.transformPositionInverse(finalPosition);
                        var sbPos = BlockPos.containing(inSBPosition.x, inSBPosition.y, inSBPosition.z);
                        var bs = embLevel.getLevel().getBlockState(sbPos);
                        if (extendType.canAffectBlock(embLevel.getLevel(), sbPos, bs)) {
                            extendType.affectBlock(embLevel.getLevel(), sbPos, bs);
                        }
                    }
                });
            }
        });
    }
}
