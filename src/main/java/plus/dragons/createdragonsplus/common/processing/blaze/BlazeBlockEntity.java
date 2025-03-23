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

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.util.CodeReference;

@CodeReference(value = BlazeBlockEntity.class, source = "create", license = "mit")
public abstract class BlazeBlockEntity extends SmartBlockEntity {
    public final LerpedFloat headAnimation = LerpedFloat.linear();
    public final LerpedFloat headAngle = LerpedFloat.angular();

    public BlazeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract boolean isActive();

    public abstract boolean isCreative();

    public abstract HeatLevel getHeatLevel();

    @Override
    public void tick() {
        super.tick();
        assert level != null;
        if (level.isClientSide) {
            if (shouldTickAnimation())
                tickAnimation();
            if (!isVirtual())
                spawnParticles(getHeatLevelFromBlock());
            return;
        }

        if (isCreative())
            return;
        updateBlockState();
    }

    @OnlyIn(Dist.CLIENT)
    protected @Nullable PartialModel getGogglesModel(HeatLevel heatLevel) {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    protected @Nullable PartialModel getHatModel(HeatLevel heatLevel) {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    protected boolean shouldTickAnimation() {
        return !VisualizationManager.supportsVisualization(level);
    }

    @OnlyIn(Dist.CLIENT)
    protected void tickAnimation() {
        boolean active = getHeatLevelFromBlock().isAtLeast(HeatLevel.FADING) && isActive();
        if (active) {
            headAngle.chase((AngleHelper.horizontalAngle(getBlockState()
                    .getOptionalValue(BlazeBurnerBlock.FACING)
                    .orElse(Direction.SOUTH)
            ) + 180) % 360, .125f, Chaser.EXP);
            headAngle.tickChaser();
        } else {
            float target = 0;
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && !player.isInvisible()) {
                double x;
                double z;
                if (isVirtual()) {
                    x = -4;
                    z = -10;
                } else {
                    x = player.getX();
                    z = player.getZ();
                }
                double dx = x - (getBlockPos().getX() + 0.5);
                double dz = z - (getBlockPos().getZ() + 0.5);
                target = AngleHelper.deg(-Mth.atan2(dz, dx)) - 90;
            }
            target = headAngle.getValue() + AngleHelper.getShortestAngleDiff(headAngle.getValue(), target);
            headAngle.chase(target, .25f, Chaser.exp(5));
            headAngle.tickChaser();
        }

        headAnimation.chase(active ? 1 : 0, .25f, Chaser.exp(.25f));
        headAnimation.tickChaser();
    }

    public HeatLevel getHeatLevelFromBlock() {
        return BlazeBlock.getHeatLevelOf(getBlockState());
    }

    public HeatLevel getHeatLevelForRender() {
        HeatLevel heatLevel = getHeatLevelFromBlock();
        if (!heatLevel.isAtLeast(HeatLevel.FADING))
            return HeatLevel.FADING;
        return heatLevel;
    }

    public void updateBlockState() {
        setBlockHeat(getHeatLevel());
    }

    protected void onHeatChange(HeatLevel currentHeat, HeatLevel newHeat) {}

    protected void setBlockHeat(HeatLevel newHeat) {
        HeatLevel currentHeat = getHeatLevelFromBlock();
        if (currentHeat == newHeat)
            return;
        assert level != null;
        onHeatChange(currentHeat, newHeat);
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlazeBlock.HEAT_LEVEL, newHeat));
        notifyUpdate();
    }

    protected void playSound() {
        assert level != null;
        level.playSound(null, worldPosition, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS,
                .125f + level.random.nextFloat() * .125f,
                .75f - level.random.nextFloat() * .25f);
    }

    protected void spawnParticles(HeatLevel heatLevel) {
        assert level != null;
        if (heatLevel == BlazeBurnerBlock.HeatLevel.NONE)
            return;

        RandomSource random = level.getRandom();

        Vec3 center = VecHelper.getCenterOf(worldPosition);
        Vec3 smokePos = center.add(VecHelper.offsetRandomly(Vec3.ZERO, random, .125f)
                .multiply(1, 0, 1));

        if (random.nextInt(4) != 0)
            return;

        boolean empty = level.getBlockState(worldPosition.above())
                .getCollisionShape(level, worldPosition.above())
                .isEmpty();

        if (empty || random.nextInt(8) == 0)
            level.addParticle(ParticleTypes.LARGE_SMOKE, smokePos.x, smokePos.y, smokePos.z, 0, 0, 0);

        double yMotion = empty ? .0625f : random.nextDouble() * .0125f;
        Vec3 flamePos = center.add(VecHelper.offsetRandomly(Vec3.ZERO, random, .5f)
                        .multiply(1, .25f, 1)
                        .normalize()
                        .scale((empty ? .25f : .5) + random.nextDouble() * .125f))
                .add(0, .5, 0);

        if (heatLevel.isAtLeast(HeatLevel.SEETHING)) {
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, flamePos.x, flamePos.y, flamePos.z, 0, yMotion, 0);
        } else if (heatLevel.isAtLeast(HeatLevel.FADING)) {
            level.addParticle(ParticleTypes.FLAME, flamePos.x, flamePos.y, flamePos.z, 0, yMotion, 0);
        }
    }

    protected void spawnParticleBurst(boolean soul) {
        assert level != null;
        Vec3 c = VecHelper.getCenterOf(worldPosition);
        RandomSource random = level.random;
        for (int i = 0; i < 20; i++) {
            Vec3 offset = VecHelper.offsetRandomly(Vec3.ZERO, random, .5f)
                    .multiply(1, .25f, 1)
                    .normalize();
            Vec3 pos = c.add(offset.scale(.5 + random.nextDouble() * .125f)).add(0, .125, 0);
            Vec3 motion = offset.scale(1 / 32f);

            level.addParticle(soul ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                    pos.x, pos.y, pos.z,
                    motion.x, motion.y, motion.z);
        }
    }
}
