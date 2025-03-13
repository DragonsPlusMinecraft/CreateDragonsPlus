/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package plus.dragons.createdragonsplus.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonsplus.common.recipe.CustomProcessingRecipe;
import plus.dragons.createdragonsplus.common.recipe.CustomProcessingRecipeSerializer;

import java.util.function.Function;

@Mixin(SequencedRecipe.class)
public class SequencedRecipeMixin<T extends ProcessingRecipe<?>> {
    @Shadow @Final private T wrapped;

    @ModifyReceiver(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;validate(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
    private static Codec<ProcessingRecipe<?>> codec$supportCustomProcessingRecipe(Codec<ProcessingRecipe<?>> codec, Function<ProcessingRecipe<?>, DataResult<ProcessingRecipe<?>>> checker) {
        Codec<ProcessingRecipe<?>> customCodec = Recipe.CODEC.comapFlatMap(
                recipe -> recipe instanceof ProcessingRecipe<?> customRecipe
                        ? DataResult.success(customRecipe)
                        : DataResult.error(() -> "Not a ProcessingRecipe"),
                Function.identity()
        );
        return NeoForgeExtraCodecs.withAlternative(customCodec, codec);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "writeToBuffer", at = @At("HEAD"), cancellable = true)
    private void writeToBuffer$supportCustomProcessingRecipe(RegistryFriendlyByteBuf buffer, CallbackInfo ci) {
        if (wrapped instanceof CustomProcessingRecipe) {
            RecipeSerializer<T> serializer = (RecipeSerializer<T>) wrapped.getSerializer();
            buffer.writeResourceLocation(RegisteredObjectsHelper.getKeyOrThrow(serializer));
            serializer.streamCodec().encode(buffer, wrapped);
            ci.cancel();
        }
    }

    @Inject(method = "readFromBuffer", at = @At(value = "NEW", target = "com/google/gson/JsonParseException"), cancellable = true)
    private static void readFromBuffer$supportCustomProcessingRecipe(RegistryFriendlyByteBuf buffer, CallbackInfoReturnable<SequencedRecipe<?>> cir, @Local RecipeSerializer<?> serializer) {
        if (serializer instanceof CustomProcessingRecipeSerializer<?,?> customSerializer) {
            var recipe = customSerializer.streamCodec().decode(buffer);
            cir.setReturnValue(new SequencedRecipe<>(recipe));
        }
    }
}
