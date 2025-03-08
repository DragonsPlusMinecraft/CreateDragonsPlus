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

package plus.dragons.createdragonsplus.common.recipe.color;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import plus.dragons.createdragonsplus.common.recipe.BaseRecipeBuilder;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;

public class ColoringRecipe implements Recipe<ColoringRecipeInput> {
    public static final MapCodec<ColoringRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DyeColor.CODEC.fieldOf("dye").forGetter(recipe -> recipe.color),
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
    ).apply(instance, ColoringRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ColoringRecipe> STREAM_CODEC = StreamCodec.composite(
            DyeColor.STREAM_CODEC,
            recipe -> recipe.color,
            Ingredient.CONTENTS_STREAM_CODEC,
            recipe -> recipe.ingredient,
            ItemStack.STREAM_CODEC,
            recipe -> recipe.result,
            ColoringRecipe::new
    );
    private final DyeColor color;
    private final Ingredient ingredient;
    private final ItemStack result;

    public ColoringRecipe(DyeColor color, Ingredient ingredient, ItemStack result) {
        this.color = color;
        this.ingredient = ingredient;
        this.result = result;
    }

    public static Builder builder(DyeColor color, Ingredient ingredient, ItemStack result) {
        return new Builder(color, ingredient, result);
    }

    public static Builder builder(DyeColor color, ItemLike ingredient, ItemStack result) {
        return new Builder(color, Ingredient.of(ingredient), result);
    }

    public static Builder builder(DyeColor color, ItemLike ingredient, ItemLike result) {
        return new Builder(color, Ingredient.of(ingredient), new ItemStack(result));
    }

    public static Builder builder(DyeColor color, TagKey<Item> ingredient, ItemStack result) {
        return new Builder(color, Ingredient.of(ingredient), result);
    }

    public static Builder builder(DyeColor color, TagKey<Item> ingredient, ItemLike result) {
        return new Builder(color, Ingredient.of(ingredient), new ItemStack(result));
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public boolean matches(ColoringRecipeInput input, Level level) {
        return this.color == input.color() && this.ingredient.test(input.item());
    }

    @Override
    public ItemStack assemble(ColoringRecipeInput input, Provider registries) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.ingredient);
        return list;
    }

    @Override
    public ItemStack getResultItem(Provider registries) {
        return this.result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CDPRecipes.COLORING_SERIALIZER.value();
    }

    @Override
    public RecipeType<?> getType() {
        return CDPRecipes.COLORING_TYPE.value();
    }

    public record Serializer(MapCodec<ColoringRecipe> codec,
                             StreamCodec<RegistryFriendlyByteBuf, ColoringRecipe> streamCodec
    ) implements RecipeSerializer<ColoringRecipe> {
        public Serializer() {
            this(CODEC, STREAM_CODEC);
        }
    }

    public static class Builder extends BaseRecipeBuilder<ColoringRecipe, Builder> {
        private final DyeColor color;
        private final Ingredient ingredient;
        private final ItemStack result;

        protected Builder(DyeColor color, Ingredient ingredient, ItemStack result) {
            super("coloring");
            this.color = color;
            this.ingredient = ingredient;
            this.result = result;
        }

        @Override
        protected Builder builder() {
            return this;
        }

        @Override
        public RecipeHolder<ColoringRecipe> build() {
            if (this.id == null) {
                this.id = this.result.getItemHolder().unwrapKey().orElseThrow().location();
            }
            var recipe = new ColoringRecipe(this.color, this.ingredient, this.result);
            return new RecipeHolder<>(this.id, recipe);
        }
    }
}
