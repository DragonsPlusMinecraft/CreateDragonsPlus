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
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.slf4j.Logger;
import plus.dragons.createdragonsplus.mixin.minecraft.RecipeManagerAccessor;

/**
 * Fired when the {@link RecipeManager} has reloaded and is about sync the recipes from the server to the client.
 *
 * <p>This event is not {@linkplain ICancellableEvent cancellable}, and does not have a result.</p>
 *
 * <p>This event is fired on the {@linkplain NeoForge#EVENT_BUS game event bus},
 * only on the {@linkplain LogicalSide#SERVER logical server}, right after the
 * {@link TagsUpdatedEvent}. Therefore, updated tags and data maps can be retrieved in this event.</p>
 */
public class UpdateRecipesEvent extends Event {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final RecipeManager recipeManager;
    private final Multimap<RecipeType<?>, RecipeHolder<?>> byType;
    private final Map<ResourceLocation, RecipeHolder<?>> byName;
    private int added;
    private int removed;

    @Internal
    public UpdateRecipesEvent(RecipeManager recipeManager, Multimap<RecipeType<?>, RecipeHolder<?>> byType, Map<ResourceLocation, RecipeHolder<?>> byName) {
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
     * Adds a {@link RecipeHolder recipe} to the the {@link RecipeManager recipe manager}.
     * 
     * @param recipe the recipe to add
     */
    public void addRecipe(RecipeHolder<?> recipe) {
        byType.put(recipe.value().getType(), recipe);
        byName.put(recipe.id(), recipe);
        added++;
    }

    /**
     * Removes a {@link RecipeHolder recipe} from the the {@link RecipeManager recipe manager}.
     * 
     * @param recipe the recipe to remove
     */
    public void removeRecipe(RecipeHolder<?> recipe) {
        byType.remove(recipe.value().getType(), recipe);
        byName.remove(recipe.id());
        removed++;
    }

    @Internal
    public void apply() {
        ((RecipeManagerAccessor) recipeManager).setByType(ImmutableMultimap.copyOf(byType));
        ((RecipeManagerAccessor) recipeManager).setByName(ImmutableMap.copyOf(byName));
        LOGGER.debug("Added {} recipes to RecipeManager", added);
        LOGGER.debug("Removed {} recipes from RecipeManager", removed);
    }
}
