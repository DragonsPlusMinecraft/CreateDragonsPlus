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
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ItemExistsCondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.OrCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public abstract class BaseRecipeBuilder<R extends Recipe<?>, B extends BaseRecipeBuilder<R, ?>> implements Consumer<RecipeOutput> {
    protected final @Nullable String directory;
    protected final List<ICondition> conditions = new ArrayList<>();
    protected @Nullable ResourceLocation id;

    protected BaseRecipeBuilder(@Nullable String directory) {
        this.directory = directory;
    }

    protected abstract B builder();

    public abstract RecipeHolder<R> build();

    public @Nullable AdvancementHolder buildAdvancement() {
        return null;
    }

    public @Nullable String getDirectory() {
        return directory;
    }

    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public final void accept(RecipeOutput output) {
        var holder = this.build();
        var id = this.directory == null
                ? holder.id()
                : holder.id().withPrefix(this.directory + "/");
        var conditions = this.conditions.toArray(ICondition[]::new);
        output.accept(id, holder.value(), this.buildAdvancement(), conditions);
    }

    public B withId(ResourceLocation id) {
        this.id = id;
        return builder();
    }

    public B withCondition(ICondition condition) {
        this.conditions.add(condition);
        return builder();
    }

    public final B withoutCondition(ICondition condition) {
        this.conditions.add(new NotCondition(condition));
        return builder();
    }

    public final B withAllCondition(ICondition... conditions) {
        Collections.addAll(this.conditions, conditions);
        return builder();
    }

    public final B withAnyCondition(ICondition... conditions) {
        this.conditions.add(new OrCondition(List.of(conditions)));
        return builder();
    }

    public final B withMod(String mod) {
        this.withCondition(new ModLoadedCondition(mod));
        return builder();
    }

    public final B withoutMod(String mod) {
        this.withoutCondition(new ModLoadedCondition(mod));
        return builder();
    }

    public final B withItem(ResourceLocation location) {
        this.withCondition(new ItemExistsCondition(location));
        return builder();
    }

    public final B withItem(DeferredHolder<Item, ?> item) {
        this.withCondition(new ItemExistsCondition(item.getId()));
        return builder();
    }

    public final B withoutItem(ResourceLocation location) {
        this.withoutCondition(new ItemExistsCondition(location));
        return builder();
    }

    public final B withoutItem(DeferredHolder<Item, ?> item) {
        this.withoutCondition(new ItemExistsCondition(item.getId()));
        return builder();
    }

    public final B withTag(ResourceLocation location) {
        this.withoutCondition(new TagEmptyCondition(location));
        return builder();
    }

    public final B withTag(TagKey<Item> tag) {
        this.withoutCondition(new TagEmptyCondition(tag));
        return builder();
    }

    public final B withoutTag(ResourceLocation location) {
        this.withCondition(new TagEmptyCondition(location));
        return builder();
    }

    public final B withoutTag(TagKey<Item> tag) {
        this.withCondition(new TagEmptyCondition(tag));
        return builder();
    }
}
