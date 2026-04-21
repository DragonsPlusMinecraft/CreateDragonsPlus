package plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank;

import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3d;

/**
 * Wrap OpenEndedPipeEffectHandler to FragileFluidTankBreakEffectHandler. <p>
 * Only suitable for specific type of fluid. Proceed with caution.
 */
public class OpenEndedPipeEffectHandlerWrapper extends DefaultRangedEffectHandler {
    private final OpenPipeEffectHandler handler;

    public static OpenEndedPipeEffectHandlerWrapper of(OpenPipeEffectHandler handler){
        return new OpenEndedPipeEffectHandlerWrapper(handler);
    }

    private OpenEndedPipeEffectHandlerWrapper(OpenPipeEffectHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onHit(Level level, AABB aabb, Vector3d hitPos, FluidStack fluid) {
        handler.apply(level, aabb, fluid);
    }
}
