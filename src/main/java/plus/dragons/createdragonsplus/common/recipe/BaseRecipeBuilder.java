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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ItemExistsCondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public abstract class BaseRecipeBuilder<R extends Recipe<?>, B extends BaseRecipeBuilder<R, ?>> implements Consumer<Consumer<FinishedRecipe>> {
    protected final @Nullable String directory;
    protected final List<ICondition> conditions = new ArrayList<>();
    protected @Nullable ResourceLocation id;

    protected BaseRecipeBuilder(@Nullable String directory) {
        this.directory = directory;
    }

    protected abstract B builder();

    public abstract FinishedRecipe build();

    public @Nullable String getDirectory() {
        return directory;
    }

    public @Nullable ResourceLocation getId() {
        return id;
    }

    public List<ICondition> getConditions() {
        return List.copyOf(conditions);
    }

    protected final ResourceLocation outputId() {
        if (id == null)
            throw new IllegalStateException("Recipe id has not been set");
        return directory == null ? id : new ResourceLocation(id.getNamespace(), directory + "/" + id.getPath());
    }

    @Override
    public final void accept(Consumer<FinishedRecipe> output) {
        FinishedRecipe recipe = build();
        if (conditions.isEmpty()) {
            output.accept(recipe);
            return;
        }

        ConditionalRecipe.Builder conditional = ConditionalRecipe.builder();
        conditions.forEach(conditional::addCondition);
        conditional.addRecipe(recipe);
        if (recipe.serializeAdvancement() != null)
            conditional.generateAdvancement(recipe.getAdvancementId());
        conditional.build(output, recipe.getId());
    }

    public B withId(ResourceLocation id) {
        this.id = id;
        return builder();
    }

    public B withCondition(ICondition condition) {
        conditions.add(condition);
        return builder();
    }

    public final B withoutCondition(ICondition condition) {
        conditions.add(new NotCondition(condition));
        return builder();
    }

    public final B withAllCondition(ICondition... conditions) {
        Collections.addAll(this.conditions, conditions);
        return builder();
    }

    public final B withAnyCondition(ICondition... conditions) {
        this.conditions.add(new OrCondition(conditions));
        return builder();
    }

    public final B withMod(String mod) {
        return withCondition(new ModLoadedCondition(mod));
    }

    public final B withoutMod(String mod) {
        return withoutCondition(new ModLoadedCondition(mod));
    }

    public final B withItem(ResourceLocation location) {
        return withCondition(new ItemExistsCondition(location));
    }

    public final B withItem(RegistryObject<? extends Item> item) {
        return withItem(item.getId());
    }

    public final B withoutItem(ResourceLocation location) {
        return withoutCondition(new ItemExistsCondition(location));
    }

    public final B withoutItem(RegistryObject<? extends Item> item) {
        return withoutItem(item.getId());
    }

    public final B withTag(ResourceLocation location) {
        return withoutCondition(new TagEmptyCondition(location));
    }

    public final B withTag(TagKey<Item> tag) {
        return withTag(tag.location());
    }

    public final B withoutTag(ResourceLocation location) {
        return withCondition(new TagEmptyCondition(location));
    }

    public final B withoutTag(TagKey<Item> tag) {
        return withoutTag(tag.location());
    }
}
