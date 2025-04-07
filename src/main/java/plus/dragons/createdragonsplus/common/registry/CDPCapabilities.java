package plus.dragons.createdragonsplus.common.registry;

import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.behaviours.BehaviourProvider;

public class CDPCapabilities {
    public static final BlockCapability<BehaviourProvider, @Nullable Void> BEHAVIOUR_PROVIDER = BlockCapability
            .createVoid(CDPCommon.asResource("behaviour_provider"), BehaviourProvider.class);
}
