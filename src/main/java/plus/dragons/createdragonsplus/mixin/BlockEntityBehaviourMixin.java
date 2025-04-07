package plus.dragons.createdragonsplus.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonsplus.common.registry.CDPCapabilities;

@Mixin(BlockEntityBehaviour.class)
public class BlockEntityBehaviourMixin {
    @Inject(method = "get(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lcom/simibubi/create/foundation/blockEntity/behaviour/BehaviourType;)Lcom/simibubi/create/foundation/blockEntity/behaviour/BlockEntityBehaviour;", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/BlockEntityBehaviour;get(Lnet/minecraft/world/level/block/entity/BlockEntity;Lcom/simibubi/create/foundation/blockEntity/behaviour/BehaviourType;)Lcom/simibubi/create/foundation/blockEntity/behaviour/BlockEntityBehaviour;"), cancellable = true)
    private static <T extends BlockEntityBehaviour> void createintegratedfarming$getSmartBlockEntityFromWrapper(BlockGetter blockGetter, BlockPos pos, BehaviourType<T> type, CallbackInfoReturnable<T> cir, @Local BlockEntity blockEntity) {
        if (!(blockEntity instanceof SmartBlockEntity) && blockGetter instanceof Level level) {
            var state = level.getBlockState(pos);
            var provider = level.getCapability(CDPCapabilities.BEHAVIOUR_PROVIDER, pos, state, blockEntity);
            if (provider != null)
                cir.setReturnValue(provider.getBehaviour(type));
        }
    }
}
