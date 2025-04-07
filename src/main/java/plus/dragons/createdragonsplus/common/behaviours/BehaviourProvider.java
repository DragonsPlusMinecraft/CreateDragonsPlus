package plus.dragons.createdragonsplus.common.behaviours;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import plus.dragons.createdragonsplus.util.MethodsReturnNullabilityUnknownByDefault;
import plus.dragons.createdragonsplus.util.ParametersNullabilityUnknownByDefault;

import java.util.Collection;

/**
 * Capability interface for providing {@link BlockEntityBehaviour} without {@link SmartBlockEntity}.
 */
@MethodsReturnNullabilityUnknownByDefault
@ParametersNullabilityUnknownByDefault
public interface BehaviourProvider {
    <T extends BlockEntityBehaviour> T getBehaviour(BehaviourType<T> type);

    Collection<BlockEntityBehaviour> getAllBehaviours();
}
