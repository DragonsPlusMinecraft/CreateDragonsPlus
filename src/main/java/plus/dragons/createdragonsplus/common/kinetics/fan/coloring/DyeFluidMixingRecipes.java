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

package plus.dragons.createdragonsplus.common.kinetics.fan.coloring;

import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.ApiStatus.Internal;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariant;
import plus.dragons.createdragonsplus.common.registry.CDPFanProcessingTypes;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;

public final class DyeFluidMixingRecipes {
    private static final int MAX_ITEM_OUTPUTS = 4;

    private DyeFluidMixingRecipes() {}

    public static Optional<MixingRecipe> create(DyeVariant variant, ItemStack input, Level level) {
        if (input.isEmpty())
            return Optional.empty();
        var processingType = CDPFanProcessingTypes.COLORING.get(variant.id());
        if (processingType == null)
            return Optional.empty();
        var fluidTag = CDPFluids.COMMON_TAGS.dyesByVariant.get(variant.id());
        if (fluidTag == null)
            return Optional.empty();

        return processingType.get()
                .processForDyeFluidMixing(input, level)
                .filter(result -> result.outputs().size() <= MAX_ITEM_OUTPUTS)
                .map(result -> new StandardProcessingRecipe.Builder<>(MixingRecipe::new, runtimeRecipeId(variant))
                        .withItemIngredients(DataComponentIngredient.of(true, input.copyWithCount(1)))
                        .withFluidIngredients(SizedFluidIngredient.of(fluidTag, result.dyeFluidAmount()))
                        .withItemOutputs(result.outputs().toArray(ProcessingOutput[]::new))
                        .build());
    }

    public static Optional<RecipeHolder<BasinRecipe>> createJeiRecipe(RecipeHolder<ColoringRecipe> holder) {
        var coloringRecipe = holder.value();
        var fluidTag = CDPFluids.COMMON_TAGS.dyesByVariant.get(coloringRecipe.getColor());
        if (fluidTag == null || coloringRecipe.getRollableResults().size() > MAX_ITEM_OUTPUTS)
            return Optional.empty();

        var recipeId = jeiRecipeId(holder.id(), coloringRecipe.getColor());
        var builder = new StandardProcessingRecipe.Builder<>(
                params -> new JeiMixingRecipe(params, coloringRecipe.getColor()), recipeId)
                        .withItemIngredients(coloringRecipe.getIngredients())
                        .withFluidIngredients(SizedFluidIngredient.of(fluidTag, coloringRecipe.getDyeFluidAmount()))
                        .withItemOutputs(coloringRecipe.getRollableResults()
                                .toArray(ProcessingOutput[]::new));
        BasinRecipe recipe = builder.build();
        return Optional.of(new RecipeHolder<>(recipeId, recipe));
    }

    @Internal
    public static Optional<ResourceLocation> getJeiRecipeColor(BasinRecipe recipe) {
        return recipe instanceof JeiMixingRecipe jeiRecipe
                ? Optional.of(jeiRecipe.color)
                : Optional.empty();
    }

    private static ResourceLocation runtimeRecipeId(DyeVariant variant) {
        return CDPCommon.asResource("dye_fluid_coloring/" + variant.serializedName());
    }

    private static ResourceLocation jeiRecipeId(ResourceLocation coloringRecipeId, ResourceLocation color) {
        return CDPCommon.asResource("dye_fluid_coloring/"
                + color.getNamespace()
                + "/"
                + color.getPath()
                + "/"
                + coloringRecipeId.getNamespace()
                + "/"
                + coloringRecipeId.getPath());
    }

    private static final class JeiMixingRecipe extends MixingRecipe {
        private final ResourceLocation color;

        private JeiMixingRecipe(ProcessingRecipeParams params, ResourceLocation color) {
            super(params);
            this.color = color;
        }
    }
}
