package plus.dragons.createdragonsplus.common.advancements.criterion;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;

public class UnitTrigger extends BlockEntityBehaviourTrigger<Unit> {
    protected UnitTrigger() {
        super(Unit.CODEC);
    }

    public static UnitTrigger of() {
        return new UnitTrigger();
    }

    @Override
    protected boolean test(ServerPlayer player, SmartBlockEntity blockEntity, Unit data) {
        return true;
    }
}
