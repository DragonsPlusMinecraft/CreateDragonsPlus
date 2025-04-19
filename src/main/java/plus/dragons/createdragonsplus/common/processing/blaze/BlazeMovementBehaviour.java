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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
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
    }

    private LerpedFloat getHeadAngle(MovementContext context) {
        if (!(context.temporaryData instanceof LerpedFloat))
            context.temporaryData = LerpedFloat.angular()
                    .startWithValue(getTargetAngle(context));
        return (LerpedFloat) context.temporaryData;
    }

    private float getTargetAngle(MovementContext context) {
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
}
