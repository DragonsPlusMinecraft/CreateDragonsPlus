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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.recipe.BaseRecipeBuilder;
import plus.dragons.createdragonsplus.data.recipe.integration.IntegrationResultRecipe;

public class ShapedRecipeBuilder extends BaseRecipeBuilder<ShapedRecipe, ShapedRecipeBuilder> {
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private int width;
    private final List<String> pattern = new ArrayList<>();
    private ItemStack result = ItemStack.EMPTY;
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();
    private RecipeCategory category = RecipeCategory.MISC;
    private String group = "";
    private boolean showNotification = true;

    public ShapedRecipeBuilder(@Nullable String directory) {
        super(directory);
    }

    public ShapedRecipeBuilder define(Character symbol, TagKey<Item> tag) {
        return define(symbol, Ingredient.of(tag));
    }

    public ShapedRecipeBuilder define(Character symbol, ItemLike item) {
        return define(symbol, Ingredient.of(item));
    }

    public ShapedRecipeBuilder define(Character symbol, Ingredient ingredient) {
        if (key.containsKey(symbol))
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        if (symbol == ' ')
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        key.put(symbol, ingredient);
        return this;
    }

    public ShapedRecipeBuilder pattern(String line) {
        Preconditions.checkArgument(!line.isEmpty(), "Pattern line must not be empty");
        if (width == 0)
            width = line.length();
        else if (width != line.length())
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        pattern.add(line);
        return this;
    }

    public ShapedRecipeBuilder output(ItemLike item) {
        return output(new ItemStack(item));
    }

    public ShapedRecipeBuilder output(ItemLike item, int count) {
        return output(new ItemStack(item, count));
    }

    public ShapedRecipeBuilder output(ItemStack stack) {
        result = stack;
        return this;
    }

    public IntegrationResultRecipe.Builder output(ResourceLocation result) {
        return new IntegrationResultRecipe.Builder(this, this.result, result);
    }

    public ShapedRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        criteria.put(name, criterion);
        return this;
    }

    public ShapedRecipeBuilder category(RecipeCategory category) {
        this.category = category;
        return this;
    }

    public ShapedRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    public ShapedRecipeBuilder showNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    @Override
    protected ShapedRecipeBuilder builder() {
        return this;
    }

    @Override
    public FinishedRecipe build() {
        if (id == null)
            id = BuiltInRegistries.ITEM.getKey(result.getItem());
        net.minecraft.data.recipes.ShapedRecipeBuilder vanilla = net.minecraft.data.recipes.ShapedRecipeBuilder
                .shaped(category, result.getItem(), result.getCount());
        key.forEach(vanilla::define);
        pattern.forEach(vanilla::pattern);
        criteria.forEach(vanilla::unlockedBy);
        vanilla.group(group).showNotification(showNotification);
        AtomicReference<FinishedRecipe> built = new AtomicReference<>();
        vanilla.save(built::set, outputId());
        return built.get();
    }
}
