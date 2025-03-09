package plus.dragons.createdragonsplus.integration.ponder;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import plus.dragons.createdragonsplus.common.CDPCommon;

public class CDPPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return CDPCommon.ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CDPPonder.register(helper);
    }
}
