package plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3d;
import plus.dragons.createdragonsplus.integration.simulated.api.fluids.tank.FragileFluidTankBreakEffectHandler;
import plus.dragons.createdragonsplus.integration.simulated.config.CDPSEConfig;

public abstract class DefaultRangedEffectHandler implements FragileFluidTankBreakEffectHandler {
    @Override
    public void apply(Level level, BlockPos pos, Vector3d hitPos, FluidStack fluid) {
        double inflation = (double) fluid.getAmount() / CDPSEConfig.fluid().fragileFluidTankCapacity.get() * CDPSEConfig.fluid().fragileFluidTankAffectMaxRadius.get();
        var aabb = new AABB(pos).inflate(inflation);
        onHit(level,aabb,fluid);
    }

    protected abstract void onHit(Level level, AABB aabb, FluidStack fluid);
}
