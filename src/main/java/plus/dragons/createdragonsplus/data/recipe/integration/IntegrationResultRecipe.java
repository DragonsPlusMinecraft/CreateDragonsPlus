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

package plus.dragons.createdragonsplus.data.recipe.integration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.recipe.BaseRecipeBuilder;

public final class IntegrationResultRecipe {
    private IntegrationResultRecipe() {}

    public static class Builder extends BaseRecipeBuilder<Recipe<?>, Builder> {
        private final BaseRecipeBuilder<?, ?> delegate;
        private final ItemStack delegateResult;
        private final ResourceLocation result;

        public Builder(BaseRecipeBuilder<?, ?> delegate, ItemStack delegateResult, ResourceLocation result) {
            super(delegate.getDirectory());
            this.delegate = delegate;
            this.delegateResult = delegateResult;
            this.result = result;
            this.conditions.addAll(delegate.getConditions());
            if (delegate.getId() == null)
                delegate.withId(result);
        }

        @Override
        protected Builder builder() {
            return this;
        }

        @Override
        public FinishedRecipe build() {
            FinishedRecipe original = delegate.build();
            return new FinishedRecipe() {
                @Override
                public void serializeRecipeData(JsonObject json) {
                    JsonObject serialized = original.serializeRecipe();
                    serialized.remove("type");
                    replaceResult(serialized);
                    for (Map.Entry<String, JsonElement> entry : serialized.entrySet())
                        json.add(entry.getKey(), entry.getValue());
                }

                private void replaceResult(JsonObject json) {
                    JsonElement resultElement = json.get("result");
                    if (resultElement != null && resultElement.isJsonObject()) {
                        JsonObject resultObject = resultElement.getAsJsonObject();
                        resultObject.addProperty("item", result.toString());
                        if (delegateResult.getCount() > 1)
                            resultObject.addProperty("count", delegateResult.getCount());
                    } else {
                        json.addProperty("result", result.toString());
                        if (delegateResult.getCount() > 1)
                            json.addProperty("count", delegateResult.getCount());
                    }
                }

                @Override
                public ResourceLocation getId() {
                    return original.getId();
                }

                @Override
                public RecipeSerializer<?> getType() {
                    return original.getType();
                }

                @Override
                public @Nullable JsonObject serializeAdvancement() {
                    return original.serializeAdvancement();
                }

                @Override
                public @Nullable ResourceLocation getAdvancementId() {
                    return original.getAdvancementId();
                }
            };
        }

        @Override
        public @Nullable ResourceLocation getId() {
            return delegate.getId();
        }

        @Override
        public @Nullable String getDirectory() {
            return delegate.getDirectory();
        }

        @Override
        public Builder withId(ResourceLocation id) {
            delegate.withId(id);
            return this;
        }

        @Override
        public Builder withCondition(ICondition condition) {
            super.withCondition(condition);
            return this;
        }
    }
}
