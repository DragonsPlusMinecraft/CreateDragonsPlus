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

package plus.dragons.createdragonsplus.mixin.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.fluids.potion.PotionMixingRecipes;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.config.CDPConfig;

@Mixin(PotionMixingRecipes.class)
public class PotionMixingRecipesMixin {
    @Unique
    private static final List<MixingRecipe> FLUID_DRAGON_BREATH_RECIPES = new ArrayList<>();

    @WrapOperation(method = "createRecipesImpl", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/fluids/potion/PotionMixingRecipes;createRecipe(Ljava/lang/String;Lnet/minecraft/world/item/crafting/Ingredient;Lnet/neoforged/neoforge/fluids/FluidStack;Lnet/neoforged/neoforge/fluids/FluidStack;)Lnet/minecraft/world/item/crafting/RecipeHolder;"))
    private static RecipeHolder<MixingRecipe> createRecipesImpl$createDragonBreathFluidRecipe(String id, Ingredient ingredient, FluidStack fromFluid, FluidStack toFluid, Operation<RecipeHolder<MixingRecipe>> original, @Local(name = "mixingRecipes") List<RecipeHolder<MixingRecipe>> mixingRecipes) {
        if (CDPConfig.features().generateAutomaticBrewingRecipeForDragonBreathFluid.get()) {
            if (ingredient.test(new ItemStack(Items.DRAGON_BREATH))) {
                var recipeId = CDPCommon.asResource(id + "_using_dragon_breath_fluid");
                var recipe = new StandardProcessingRecipe.Builder<>(MixingRecipe::new, recipeId)
                        .require(CDPFluids.COMMON_TAGS.dragonBreath, 250)
                        .require(FluidIngredient.fromFluidStack(fromFluid))
                        .output(toFluid)
                        .requiresHeat(HeatCondition.HEATED)
                        .build();
                FLUID_DRAGON_BREATH_RECIPES.add(recipe);
                mixingRecipes.add(new RecipeHolder<>(recipeId, recipe));
            }
        }
        return original.call(id, ingredient, fromFluid, toFluid);
    }

    @Inject(method = "sortRecipesByItem(Ljava/util/List;)Ljava/util/Map;", at = @At("TAIL"))
    private static void sortRecipesByItem$sortDragonBreathFluidRecipes(List<RecipeHolder<MixingRecipe>> all, CallbackInfoReturnable<Map<Item, List<MixingRecipe>>> cir) {
        var byItem = cir.getReturnValue();
        byItem.computeIfAbsent(Items.DRAGON_BREATH, ignored -> new ArrayList<>()).addAll(FLUID_DRAGON_BREATH_RECIPES);
    }
}
