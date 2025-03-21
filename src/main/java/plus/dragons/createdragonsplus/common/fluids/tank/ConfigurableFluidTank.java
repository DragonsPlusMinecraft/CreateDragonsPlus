package plus.dragons.createdragonsplus.common.fluids.tank;

import com.google.common.base.Predicates;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.function.Consumers;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConfigurableFluidTank extends SmartFluidTank {
    protected Predicate<FluidStack> insertion = Predicates.alwaysTrue();
    protected Predicate<FluidStack> extraction = Predicates.alwaysTrue();

    public ConfigurableFluidTank(int capacity, Consumer<FluidStack> updateCallback) {
        super(capacity, updateCallback);
    }

    public ConfigurableFluidTank(int capacity) {
        super(capacity, Consumers.nop());
    }

    public ConfigurableFluidTank allowInsertion() {
        this.insertion = Predicates.alwaysTrue();
        return this;
    }

    public ConfigurableFluidTank allowInsertion(Predicate<FluidStack> inputPredicate) {
        this.insertion = inputPredicate;
        return this;
    }

    public ConfigurableFluidTank forbidInsertion() {
        this.insertion = Predicates.alwaysFalse();
        return this;
    }

    public ConfigurableFluidTank allowExtraction() {
        this.extraction = Predicates.alwaysTrue();
        return this;
    }

    public ConfigurableFluidTank allowExtration(Predicate<FluidStack> contentPredicate) {
        this.extraction = contentPredicate;
        return this;
    }

    public ConfigurableFluidTank forbidExtraction() {
        this.extraction = Predicates.alwaysFalse();
        return this;
    }

    public int fill(FluidStack resource, FluidAction action, boolean forced) {
        return forced ? super.fill(resource, action) : this.fill(resource, action);
    }

    public FluidStack drain(FluidStack resource, FluidAction action, boolean forced) {
        return forced ? super.drain(resource, action) : this.drain(resource, action);
    }

    public FluidStack drain(int maxDrain, FluidAction action, boolean forced) {
        return forced ? super.drain(maxDrain, action) : this.drain(maxDrain, action);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (insertion.test(resource))
            return super.fill(resource, action);
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (extraction.test(fluid))
            return super.drain(resource, action);
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (extraction.test(fluid))
            return super.drain(maxDrain, action);
        return FluidStack.EMPTY;
    }
}
