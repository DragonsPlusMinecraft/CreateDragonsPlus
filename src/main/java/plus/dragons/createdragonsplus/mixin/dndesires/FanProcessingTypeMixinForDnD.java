package plus.dragons.createdragonsplus.mixin.dndesires;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import dev.lopyluna.dndesires.content.fan_types.DragonBreathingType;
import dev.lopyluna.dndesires.content.fan_types.FreezingType;
import dev.lopyluna.dndesires.content.fan_types.SandingType;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.integration.ModIntegration;

@Restriction(require = @Condition(ModIntegration.Constants.CREATE_DND))
@Mixin(FanProcessingType.class)
public interface FanProcessingTypeMixinForDnD {
    @WrapOperation(
            method = "getAt",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;isValidAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z")
    )
    private static boolean ignoreDisabledType(FanProcessingType instance, Level level, BlockPos blockPos, Operation<Boolean> original) {
        if (instance instanceof FreezingType && CDPConfig.recipes().enableBulkFreezing.get())
           return false;
        else if (instance instanceof DragonBreathingType && CDPConfig.recipes().enableBulkEnding.get())
            return false;
        else if (instance instanceof SandingType && CDPConfig.recipes().enableBulkSanding.get())
            return false;
        else return original.call(instance, level, blockPos);
    }
}
