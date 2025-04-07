package plus.dragons.createdragonsplus.common.behaviours;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.registry.CDPCapabilities;

/**
 * Base implementation of {@link BehaviourProvider} through a wrapping {@link SmartBlockEntity}.
 * <p>
 * Register to {@link CDPCapabilities#BEHAVIOUR_PROVIDER} to supply {@link BlockEntityBehaviour} from non-{@link SmartBlockEntity}.
 * @param <T> the type of the owning {@link BlockEntity}
 */
public abstract class SmartBlockEntityBehaviourProvider<T extends BlockEntity> extends SmartBlockEntity implements BehaviourProvider {
    protected final T blockEntity;

    public SmartBlockEntityBehaviourProvider(T blockEntity) {
        super(blockEntity.getType(), blockEntity.getBlockPos(), blockEntity.getBlockState());
        this.blockEntity = blockEntity;
    }

    @Nullable
    @Override
    public Level getLevel() {
        return blockEntity.getLevel();
    }

    @Override
    public void setLevel(Level level) {
        blockEntity.setLevel(level);
    }
}
