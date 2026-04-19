package plus.dragons.createdragonsplus.mixin.simulated.create;

import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import plus.dragons.createdragonsplus.common.kinetics.fan.AirCurrentSegmentAccess;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.integration.simulated.common.kinetics.fan.IFanProcessingTypeSimulatedExtension;
import plus.dragons.createdragonsplus.integration.simulated.config.CDPSEConfig;

import java.util.List;

@Restriction(require = @Condition(ModIntegration.Constants.AERONAUTICS))
@Mixin(AirCurrent.class)
public class AirCurrentMixinForSimulatedBehaviour {
    @Shadow
    @Final
    public IAirCurrentSource source;
    @Shadow
    public List<AirCurrentSegmentAccess> segments;
    @Shadow
    public Direction direction;

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tick$tickAirCurrentBlockHit(CallbackInfo ci) {
        if (!CDPSEConfig.features().airCurrentBlockInteraction.get()) return;
        if (!(source instanceof SmartBlockEntity be)) return;
        if (be.isVirtual()) return;
        ActiveSableCompanion helper = Sable.HELPER;
        var subLevelAccess = helper.getContaining((BlockEntity) source);
        if (subLevelAccess == null)
            return;
        Pose3dc pose = subLevelAccess.logicalPose();
        segments.forEach(seg -> {
            var type = seg.getType();
            if (type == null || !(type instanceof IFanProcessingTypeSimulatedExtension extendType) || !extendType.active()) return;
            for(int i= seg.getStartOffset(); i < seg.getEndOffset(); i++) {
                var currentPos = source.getAirCurrentPos().relative(direction, i);
                var position = currentPos.getCenter();
                position = pose.transformPosition(position);
                var globalPos = BlockPos.containing(position.x, position.y, position.z);
                var blockState = ((BlockEntity) source).getLevel().getBlockState(globalPos);
                if(extendType.canAffectBlock(((BlockEntity) source).getLevel(), globalPos, blockState)){
                    extendType.affectBlock(((BlockEntity) source).getLevel(), globalPos, blockState);
                }
            }
        });
    }
}
