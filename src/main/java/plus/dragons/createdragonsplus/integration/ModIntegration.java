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

package plus.dragons.createdragonsplus.integration;

import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.registries.DeferredHolder;

public enum ModIntegration {
    //TODO: Keep an eye on Create Garnished 2. Wait it add back Fan Processing
    CREATE_GARNISHED(Constants.CREATE_GARNISHED),
    CREATE_DND(Constants.CREATE_DND),
    IMMERSIVE_ENGINEERING(Constants.IMMERSIVE_ENGINEERING),
    QUICKSAND(Constants.QUICKSAND),
    SABLE(Constants.SABLE),;

    private final String id;

    ModIntegration(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public boolean enabled() {
        return ModList.get().isLoaded(id);
    }

    public ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(id, path);
    }

    public ModLoadedCondition condition() {
        return new ModLoadedCondition(id);
    }

    public static class Constants {
        public static final String CREATE_GARNISHED = "garnished";
        public static final String CREATE_DND = "dndesires";
        public static final String IMMERSIVE_ENGINEERING = "immersiveengineering";
        public static final String QUICKSAND = "quicksand";
        public static final String SABLE = "sable";
    }

    public DeferredHolder<FanProcessingType, FanProcessingType> fanType(String path) {
        return DeferredHolder.create(CreateRegistries.FAN_PROCESSING_TYPE, asResource(path));
    }

    public DeferredHolder<RecipeType<?>, RecipeType<StandardProcessingRecipe<SingleRecipeInput>>> recipeType(String path) {
        return DeferredHolder.create(Registries.RECIPE_TYPE, asResource(path));
    }
}
