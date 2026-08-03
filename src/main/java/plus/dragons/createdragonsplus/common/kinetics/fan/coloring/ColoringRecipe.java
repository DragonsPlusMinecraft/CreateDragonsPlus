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

import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;

public class ColoringRecipe extends ProcessingRecipe<ColoringRecipeWrapper> {
    private ResourceLocation color;
    private int dyeFluidAmount = ColoringRecipeParams.DEFAULT_DYE_FLUID_AMOUNT;

    public ColoringRecipe(ProcessingRecipeParams params) {
        super(CDPRecipes.COLORING, params);
    }

    public static Builder builder(ResourceLocation id, ResourceLocation color) {
        return new Builder(id, color);
    }

    public ResourceLocation getColor() {
        return color;
    }

    public int getDyeFluidAmount() {
        return dyeFluidAmount;
    }

    @Override
    public boolean matches(ColoringRecipeWrapper input, Level level) {
        return !input.isEmpty() && color.equals(input.color()) && ingredients.get(0).test(input.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 12;
    }

    @Override
    public void readAdditional(JsonObject json) {
        color = new ResourceLocation(GsonHelper.getAsString(json, "color"));
        dyeFluidAmount = GsonHelper.getAsInt(json, "dye_fluid_amount",
                ColoringRecipeParams.DEFAULT_DYE_FLUID_AMOUNT);
        validateDyeFluidAmount(dyeFluidAmount);
    }

    @Override
    public void readAdditional(FriendlyByteBuf buffer) {
        color = buffer.readResourceLocation();
        dyeFluidAmount = buffer.readVarInt();
        validateDyeFluidAmount(dyeFluidAmount);
    }

    @Override
    public void writeAdditional(JsonObject json) {
        json.addProperty("color", color.toString());
        if (dyeFluidAmount != ColoringRecipeParams.DEFAULT_DYE_FLUID_AMOUNT)
            json.addProperty("dye_fluid_amount", dyeFluidAmount);
    }

    @Override
    public void writeAdditional(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(color);
        buffer.writeVarInt(dyeFluidAmount);
    }

    private static void validateDyeFluidAmount(int amount) {
        if (amount <= 0 || amount > ColoringRecipeParams.MAX_DYE_FLUID_AMOUNT)
            throw new IllegalArgumentException("Dye Fluid amount must be between 1 and "
                    + ColoringRecipeParams.MAX_DYE_FLUID_AMOUNT);
    }

    public static class Builder extends ProcessingRecipeBuilder<ColoringRecipe> {
        private final ResourceLocation color;
        private int dyeFluidAmount = ColoringRecipeParams.DEFAULT_DYE_FLUID_AMOUNT;

        protected Builder(ResourceLocation recipeId, ResourceLocation color) {
            super(ColoringRecipe::new, recipeId);
            this.color = color;
        }

        public Builder dyeFluidAmount(int amount) {
            validateDyeFluidAmount(amount);
            dyeFluidAmount = amount;
            return this;
        }

        @Override
        public Builder require(Ingredient ingredient) {
            super.require(ingredient);
            return this;
        }

        @Override
        public Builder output(ItemStack output) {
            super.output(output);
            return this;
        }

        @Override
        public Builder output(ProcessingOutput output) {
            super.output(output);
            return this;
        }

        @Override
        public Builder withItemIngredients(NonNullList<Ingredient> ingredients) {
            super.withItemIngredients(ingredients);
            return this;
        }

        @Override
        public Builder withItemOutputs(ProcessingOutput... outputs) {
            super.withItemOutputs(outputs);
            return this;
        }

        @Override
        public ColoringRecipe build() {
            ColoringRecipe recipe = super.build();
            recipe.color = color;
            recipe.dyeFluidAmount = dyeFluidAmount;
            return recipe;
        }
    }
}
