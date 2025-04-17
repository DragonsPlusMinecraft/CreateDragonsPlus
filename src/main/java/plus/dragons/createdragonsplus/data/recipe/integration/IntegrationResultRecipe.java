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

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.mixin.accessor.MappedRegistryAccessor;
import java.util.Map;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.recipe.BaseRecipeBuilder;

public final class IntegrationResultRecipe implements Recipe<RecipeInput> {
    private static final Map<RecipeSerializer<Recipe<?>>, Serializer> SERIALIZER_DELEGATES = Maps.newConcurrentMap();
    private final Recipe<?> delegate;
    private final IntegrationResult result;

    public IntegrationResultRecipe(Recipe<?> delegate, ItemStack delegateResult, ResourceLocation result) {
        this.delegate = delegate;
        this.result = new IntegrationResult(delegateResult, result);
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack assemble(RecipeInput input, Provider registries) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack getResultItem(Provider registries) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeSerializer<?> getSerializer() {
        RecipeSerializer<Recipe<?>> delegateSerializer = (RecipeSerializer<Recipe<?>>) this.delegate.getSerializer();
        return SERIALIZER_DELEGATES.computeIfAbsent(delegateSerializer, Serializer::new);
    }

    @Override
    public RecipeType<?> getType() {
        return this.delegate.getType();
    }

    static final class Serializer implements RecipeSerializer<IntegrationResultRecipe> {
        private static final HolderOwner<RecipeSerializer<?>> HOLDER_OWNER = new HolderOwner<>() {
            @Override
            public boolean canSerializeIn(HolderOwner<RecipeSerializer<?>> owner) {
                return false;
            }
        };
        private final MapCodec<IntegrationResultRecipe> codec;

        @SuppressWarnings("unchecked")
        Serializer(RecipeSerializer<Recipe<?>> delegate) {
            this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    delegate.codec().forGetter(recipe -> recipe.delegate),
                    IntegrationResult.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)).apply(instance, (d, result) -> {
                        throw new UnsupportedOperationException("Can not decode with encode-only codec");
                    }));
            MappedRegistryAccessor<RecipeSerializer<?>> registry = (MappedRegistryAccessor<RecipeSerializer<?>>) BuiltInRegistries.RECIPE_SERIALIZER;
            int id = registry.getToId().getOrDefault(this, -1);
            ResourceKey<RecipeSerializer<?>> wrappedKey = registry.getByValue().get(delegate).key();
            registry.getToId().put(this, id);
            registry.getByValue().put(this, Reference.createStandAlone(HOLDER_OWNER, wrappedKey));
        }

        @Override
        public MapCodec<IntegrationResultRecipe> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, IntegrationResultRecipe> streamCodec() {
            throw new UnsupportedOperationException();
        }
    }

    public static class Builder extends BaseRecipeBuilder<IntegrationResultRecipe, Builder> {
        private final BaseRecipeBuilder<?, ?> delegate;
        private final ItemStack delegateResult;
        private final ResourceLocation result;

        public Builder(BaseRecipeBuilder<?, ?> delegate, ItemStack delegateResult, ResourceLocation result) {
            super(delegate.getDirectory());
            this.delegate = delegate;
            this.delegateResult = delegateResult;
            this.result = result;
            if (delegate.getId() == null) {
                delegate.withId(result);
            }
        }

        @Override
        protected Builder builder() {
            return this;
        }

        @Override
        public RecipeHolder<IntegrationResultRecipe> build() {
            var holder = delegate.build();
            return new RecipeHolder<>(holder.id(), new IntegrationResultRecipe(holder.value(), delegateResult, result));
        }

        @Override
        public @Nullable AdvancementHolder buildAdvancement() {
            return delegate.buildAdvancement();
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
            delegate.withCondition(condition);
            return this;
        }
    }
}
