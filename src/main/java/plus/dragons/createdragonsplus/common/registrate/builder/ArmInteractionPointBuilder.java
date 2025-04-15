package plus.dragons.createdragonsplus.common.registrate.builder;

import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ArmInteractionPointBuilder<T extends ArmInteractionPointType,P> extends AbstractBuilder<ArmInteractionPointType, T, P, ArmInteractionPointBuilder<T,P>> {

    private final Supplier<T> factory;

    public ArmInteractionPointBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, Supplier<T> factory) {
        super(owner, parent, name, callback, CreateRegistries.ARM_INTERACTION_POINT_TYPE);
        this.factory = factory;
    }

    @Override
    protected @NotNull T createEntry() {
        return factory.get();
    }
}
