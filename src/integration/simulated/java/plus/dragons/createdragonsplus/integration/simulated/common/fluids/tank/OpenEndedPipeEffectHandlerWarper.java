package plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank;

import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Warp OpenEndedPipeEffectHandler to FragileFluidTankBreakEffectHandler. <p>
 * Only suitable for specific type of fluid. Proceed with caution.
 */
public class OpenEndedPipeEffectHandlerWarper extends DefaultRangedEffectHandler {
    private final OpenPipeEffectHandler handler;

    public static OpenEndedPipeEffectHandlerWarper of(OpenPipeEffectHandler handler){
        return new OpenEndedPipeEffectHandlerWarper(handler);
    }

    private OpenEndedPipeEffectHandlerWarper(OpenPipeEffectHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onHit(Level level, AABB aabb, FluidStack fluid) {
        handler.apply(level, aabb, fluid);
    }
}
