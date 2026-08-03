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

package plus.dragons.createdragonsplus.common.recipe;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.slf4j.Logger;
import plus.dragons.createdragonsplus.mixin.minecraft.RecipeManagerAccessor;

/**
 * Fired when the {@link RecipeManager} has reloaded and is about sync the recipes from the server to the client.
 *
 * <p>This event is not cancellable and does not have a result.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS game event bus},
 * only on the {@linkplain LogicalSide#SERVER logical server}, right after the
 * {@link TagsUpdatedEvent}. Therefore, updated tags and data maps can be retrieved in this event.</p>
 */
public class UpdateRecipesEvent extends Event {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final RecipeManager recipeManager;
    private final Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> byType;
    private final Map<ResourceLocation, Recipe<?>> byName;
    private int added;
    private int removed;

    @Internal
    public UpdateRecipesEvent(RecipeManager recipeManager, Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> byType, Map<ResourceLocation, Recipe<?>> byName) {
        this.recipeManager = recipeManager;
        this.byType = byType;
        this.byName = byName;
    }

    /**
     * @return the {@link RecipeManager recipe manager}.
     */
    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    /**
     * Gets a recipe from the current mutable recipe collection.
     *
     * @param id the recipe id
     * @return the recipe, if present
     */
    public Optional<Recipe<?>> getRecipe(ResourceLocation id) {
        return Optional.ofNullable(byName.get(id));
    }

    /**
     * Adds a {@link Recipe recipe} to the {@link RecipeManager recipe manager}.
     * 
     * @param recipe the recipe to add
     */
    public void addRecipe(Recipe<?> recipe) {
        byType.computeIfAbsent(recipe.getType(), $ -> new HashMap<>()).put(recipe.getId(), recipe);
        byName.put(recipe.getId(), recipe);
        added++;
    }

    /**
     * Removes a {@link Recipe recipe} from the {@link RecipeManager recipe manager}.
     * 
     * @param recipe the recipe to remove
     */
    public void removeRecipe(Recipe<?> recipe) {
        var recipes = byType.get(recipe.getType());
        if (recipes != null)
            recipes.remove(recipe.getId());
        byName.remove(recipe.getId());
        removed++;
    }

    @Internal
    public void apply() {
        var immutableByType = ImmutableMap.<RecipeType<?>, Map<ResourceLocation, Recipe<?>>>builder();
        byType.forEach((type, recipes) -> immutableByType.put(type, ImmutableMap.copyOf(recipes)));
        ((RecipeManagerAccessor) recipeManager).setRecipes(immutableByType.build());
        ((RecipeManagerAccessor) recipeManager).setByName(ImmutableMap.copyOf(byName));
        LOGGER.debug("Added {} recipes to RecipeManager", added);
        LOGGER.debug("Removed {} recipes from RecipeManager", removed);
    }
}
