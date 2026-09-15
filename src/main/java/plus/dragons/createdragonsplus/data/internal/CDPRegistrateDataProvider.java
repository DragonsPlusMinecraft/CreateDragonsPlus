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

package plus.dragons.createdragonsplus.data.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.RegistrateDataProvider;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class CDPRegistrateDataProvider extends RegistrateDataProvider {
    private final PackOutput.PathProvider lootTables;
    private final Map<ResourceKey<LootTable>, List<ICondition>> lootTableConditions;

    public CDPRegistrateDataProvider(AbstractRegistrate<?> parent, GatherDataEvent event,
            Map<ResourceKey<LootTable>, List<ICondition>> lootTableConditions) {
        super(parent, parent.getModid(), event);
        this.lootTables = event.getGenerator().getPackOutput().createRegistryElementsPathProvider(Registries.LOOT_TABLE);
        this.lootTableConditions = lootTableConditions;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Map<Path, JsonElement> conditionsByPath = new HashMap<>();
        this.lootTableConditions.forEach((table, conditions) -> conditionsByPath.put(
                this.lootTables.json(table.location()), ICondition.LIST_CODEC.encodeStart(JsonOps.INSTANCE, conditions).getOrThrow()));
        var conditionalWrites = new ConcurrentLinkedQueue<CompletableFuture<?>>();

        // Keep Registrate's loot generation and validation, adding load conditions before caching the output.
        return super.run((path, bytes, hash) -> {
            JsonElement conditions = conditionsByPath.get(path);
            if (conditions == null) {
                output.writeIfNeeded(path, bytes, hash);
                return;
            }
            var table = JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8)).getAsJsonObject();
            table.add(ConditionalOps.DEFAULT_CONDITIONS_KEY, conditions);
            conditionalWrites.add(DataProvider.saveStable(output, table, path));
        }).thenCompose(ignored -> CompletableFuture.allOf(conditionalWrites.toArray(CompletableFuture[]::new)));
    }
}
