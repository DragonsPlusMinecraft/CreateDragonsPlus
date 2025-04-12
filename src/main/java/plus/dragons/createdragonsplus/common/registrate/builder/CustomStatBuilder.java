package plus.dragons.createdragonsplus.common.registrate.builder;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CustomStatBuilder<P> extends AbstractBuilder<ResourceLocation, ResourceLocation, P, CustomStatBuilder<P>> {

    private final Supplier<ResourceLocation> factory;

    public CustomStatBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, Supplier<ResourceLocation> factory) {
        super(owner, parent, name, callback, BuiltInRegistries.CUSTOM_STAT.key());
        this.factory = factory;
    }

    public CustomStatBuilder<P> lang(String name) {
        return lang(resourceLocation -> "stat." + resourceLocation.toLanguageKey(), name);
    }

    @Override
    protected @NotNull ResourceLocation createEntry() {
        return factory.get();
    }
}
