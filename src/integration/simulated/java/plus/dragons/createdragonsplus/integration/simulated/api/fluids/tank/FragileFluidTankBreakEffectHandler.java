package plus.dragons.createdragonsplus.integration.simulated.api.fluids.tank;

import com.simibubi.create.api.registry.SimpleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3d;

@FunctionalInterface
public interface FragileFluidTankBreakEffectHandler {
    SimpleRegistry<Fluid, FragileFluidTankBreakEffectHandler> REGISTRY = SimpleRegistry.create();

    void apply(Level level, BlockPos pos, Vector3d hitPos, FluidStack fluid);
}
