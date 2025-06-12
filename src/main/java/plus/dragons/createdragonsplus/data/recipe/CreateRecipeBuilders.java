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

import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeBuilder;
import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class CreateRecipeBuilders {
    public static StandardProcessingRecipe.Builder<ConversionRecipe> conversion(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(ConversionRecipe::new, id);
    }

    public static StandardProcessingRecipe.Builder<CrushingRecipe> crushing(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(CrushingRecipe::new, id);
    }

    public static StandardProcessingRecipe.Builder<CuttingRecipe> cutting(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(CuttingRecipe::new, id);
    }

    public static StandardProcessingRecipe.Builder<MillingRecipe> milling(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(MillingRecipe::new, id);
    }

    public static StandardProcessingRecipe.Builder<MixingRecipe> mixing(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(MixingRecipe::new, id);
    }

    public static StandardProcessingRecipe.Builder<CompactingRecipe> compacting(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(CompactingRecipe::new, id);
    }

    public static StandardProcessingRecipe.Builder<PressingRecipe> pressing(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(PressingRecipe::new, id);
    }

    public static StandardProcessingRecipe.Builder<SandPaperPolishingRecipe> polishing(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(SandPaperPolishingRecipe::new, id);
    }

    public static StandardProcessingRecipe.Builder<SplashingRecipe> splashing(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(SplashingRecipe::new, id);
    }

    public static StandardProcessingRecipe.Builder<HauntingRecipe> haunting(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(HauntingRecipe::new, id);
    }

    public static ItemApplicationRecipe.Builder<DeployerApplicationRecipe> deploying(ResourceLocation id) {
        return new ItemApplicationRecipe.Builder<>(DeployerApplicationRecipe::new, id);
    }

    public static StandardProcessingRecipe.Builder<FillingRecipe> filling(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(FillingRecipe::new, id);
    }

    public static StandardProcessingRecipe.Builder<EmptyingRecipe> emptying(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(EmptyingRecipe::new, id);
    }

    public static ItemApplicationRecipe.Builder<ManualApplicationRecipe> manualApplication(ResourceLocation id) {
        return new ItemApplicationRecipe.Builder<>(ManualApplicationRecipe::new, id);
    }

    public static MechanicalCraftingRecipeBuilder mechanicalCrafting(ItemLike item, int count) {
        return new MechanicalCraftingRecipeBuilder(item, count);
    }

    public static MechanicalCraftingRecipeBuilder mechanicalCrafting(ItemLike item) {
        return new MechanicalCraftingRecipeBuilder(item, 1);
    }

    public static SequencedAssemblyRecipeBuilder sequencedAssembly(ResourceLocation id) {
        return new SequencedAssemblyRecipeBuilder(id);
    }
}
