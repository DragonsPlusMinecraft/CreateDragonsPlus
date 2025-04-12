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

package plus.dragons.createdragonsplus.data.recipe;

import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.data.recipe.MechanicalCraftingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import plus.dragons.createdragonsplus.common.recipe.CustomSequencedAssemblyRecipeBuilder;

public class CreateRecipeBuilders {
    public static ProcessingRecipeBuilder<ConversionRecipe> conversion(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(ConversionRecipe::new, id);
    }

    public static ProcessingRecipeBuilder<CrushingRecipe> crushing(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(CrushingRecipe::new, id);
    }

    public static ProcessingRecipeBuilder<CuttingRecipe> cutting(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(CuttingRecipe::new, id);
    }

    public static ProcessingRecipeBuilder<MillingRecipe> milling(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(MillingRecipe::new, id);
    }

    public static ProcessingRecipeBuilder<MixingRecipe> mixing(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(MixingRecipe::new, id);
    }

    public static ProcessingRecipeBuilder<CompactingRecipe> compacting(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(CompactingRecipe::new, id);
    }

    public static ProcessingRecipeBuilder<PressingRecipe> pressing(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(PressingRecipe::new, id);
    }

    public static ProcessingRecipeBuilder<SandPaperPolishingRecipe> polishing(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(SandPaperPolishingRecipe::new, id);
    }

    public static ProcessingRecipeBuilder<SplashingRecipe> splashing(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(SplashingRecipe::new, id);
    }

    public static ProcessingRecipeBuilder<HauntingRecipe> haunting(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(HauntingRecipe::new, id);
    }

    public static ProcessingRecipeBuilder<DeployerApplicationRecipe> deploying(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(DeployerApplicationRecipe::new, id);
    }

    public static ProcessingRecipeBuilder<FillingRecipe> filling(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(FillingRecipe::new, id);
    }

    public static ProcessingRecipeBuilder<EmptyingRecipe> emptying(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(EmptyingRecipe::new, id);
    }

    public static ProcessingRecipeBuilder<ManualApplicationRecipe> manualApplication(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(ManualApplicationRecipe::new, id);
    }

    public static MechanicalCraftingRecipeBuilder mechanicalCrafting(ItemLike item, int count) {
        return new MechanicalCraftingRecipeBuilder(item, count);
    }

    public static MechanicalCraftingRecipeBuilder mechanicalCrafting(ItemLike item) {
        return new MechanicalCraftingRecipeBuilder(item, 1);
    }

    public static CustomSequencedAssemblyRecipeBuilder sequencedAssembly(ResourceLocation id) {
        return new CustomSequencedAssemblyRecipeBuilder(id);
    }
}
