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

package plus.dragons.createdragonsplus.common.processing.blaze;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerMovementBehaviour;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import plus.dragons.createdragonsplus.util.CodeReference;

@CodeReference(value = BlazeBurnerMovementBehaviour.class, source = "create", license = "mit")
public class BlazeMovementBehaviour implements MovementBehaviour {
    @Override
    public void tick(MovementContext context) {
        if (!context.world.isClientSide())
            return;

        RandomSource r = context.world.getRandom();
        Vec3 c = context.position;
        Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .125f)
                .multiply(1, 0, 1));
        if (r.nextInt(3) == 0 && context.motion.length() < 1 / 64f)
            context.world.addParticle(ParticleTypes.LARGE_SMOKE, v.x, v.y, v.z, 0, 0, 0);

        LerpedFloat headAngle = getHeadAngle(context);
        boolean quickTurn = !Mth.equal(context.relativeMotion.length(), 0);
        headAngle.chase(
                headAngle.getValue() + AngleHelper.getShortestAngleDiff(headAngle.getValue(), getTargetAngle(context)), .5f,
                quickTurn ? LerpedFloat.Chaser.EXP : LerpedFloat.Chaser.exp(5));
        headAngle.tickChaser();
        spawnParticles(context.world, context.position, BlazeBlock.getHeatLevelOf(context.state));
    }

    private LerpedFloat getHeadAngle(MovementContext context) {
        if (!(context.temporaryData instanceof LerpedFloat))
            context.temporaryData = LerpedFloat.angular()
                    .startWithValue(getTargetAngle(context));
        return (LerpedFloat) context.temporaryData;
    }

    private float getTargetAngle(MovementContext context) {
        if (!Mth.equal(context.relativeMotion.length(), 0)
                && context.contraption.entity instanceof CarriageContraptionEntity cce) {

            float angle = AngleHelper.deg(-Mth.atan2(context.relativeMotion.x, context.relativeMotion.z));
            return cce.getInitialOrientation()
                    .getAxis() == Direction.Axis.X ? angle + 180 : angle;
        }

        Entity player = Minecraft.getInstance().cameraEntity;
        if (player != null && !player.isInvisible() && context.position != null) {
            Vec3 applyRotation = context.contraption.entity.reverseRotation(player.position()
                    .subtract(context.position), 1);
            double dx = applyRotation.x;
            double dz = applyRotation.z;
            return AngleHelper.deg(-Mth.atan2(dz, dx)) - 90;
        }
        return 0;
    }

    protected void spawnParticles(Level level, Vec3 pos, BlazeBurnerBlock.HeatLevel heatLevel) {
        assert level != null;
        if (heatLevel == BlazeBurnerBlock.HeatLevel.NONE)
            return;

        RandomSource random = level.getRandom();

        Vec3 smokePos = pos.add(VecHelper.offsetRandomly(Vec3.ZERO, random, .125f)
                .multiply(1, 0, 1));

        if (random.nextInt(4) != 0)
            return;

        if (random.nextInt(8) == 0)
            level.addParticle(ParticleTypes.LARGE_SMOKE, smokePos.x, smokePos.y, smokePos.z, 0, 0, 0);

        double yMotion = random.nextDouble() * .0125f;
        Vec3 flamePos = pos.add(VecHelper.offsetRandomly(Vec3.ZERO, random, .5f)
                .multiply(1, .25f, 1)
                .normalize()
                .scale(.5 + random.nextDouble() * .25f))
                .add(0, .5, 0);

        if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)) {
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, flamePos.x, flamePos.y, flamePos.z, 0, yMotion, 0);
        } else if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
            level.addParticle(ParticleTypes.FLAME, flamePos.x, flamePos.y, flamePos.z, 0, yMotion, 0);
        }
    }
}
