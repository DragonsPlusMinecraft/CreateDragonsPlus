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

package plus.dragons.createdragonsplus.data.lang;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import plus.dragons.createdragonsplus.mixin.accessor.ExistingFileHelperAccessor;

public class ForeignLanguageProvider implements DataProvider {
    private final String modid;
    private final String templateLocale;
    private final PackOutput.PathProvider langPathProvider;
    private final ResourceManager resourceManager;

    public ForeignLanguageProvider(String modid, String templateLocale, PackOutput output, ExistingFileHelper existingFileHelper) {
        this.modid = modid;
        this.templateLocale = templateLocale;
        this.langPathProvider = output.createPathProvider(Target.RESOURCE_PACK, "lang");
        this.resourceManager = ((ExistingFileHelperAccessor) existingFileHelper).invokeGetManager(PackType.CLIENT_RESOURCES);
    }

    public ForeignLanguageProvider(String modid, PackOutput output, ExistingFileHelper existingFileHelper) {
        this(modid, "en_us", output, existingFileHelper);
    }

    protected CompletableFuture<JsonObject> getTemplateLocalization() {
        return CompletableFuture.supplyAsync(() -> {
            File file = this.langPathProvider
                    .json(ResourceLocation.fromNamespaceAndPath(this.modid, this.templateLocale))
                    .toFile();
            try (FileInputStream inputStream = new FileInputStream(file)) {
                return GsonHelper.parse(new InputStreamReader(inputStream));
            } catch (IOException exception) {
                throw new JsonIOException(exception);
            }
        }, Util.backgroundExecutor());
    }

    protected CompletableFuture<JsonObject> getForeignTemplateLocalization(Resource resource) {
        return CompletableFuture.supplyAsync(() -> {
            try (InputStream inputStream = resource.open()) {
                return GsonHelper.parse(new InputStreamReader(inputStream));
            } catch (IOException exception) {
                throw new JsonIOException(exception);
            }
        }, Util.backgroundExecutor());
    }

    protected boolean isForeignLanguageFile(ResourceLocation location) {
        // Should match <namespace>:lang/<locale>.json
        if (!this.modid.equals(location.getNamespace()))
            return false;
        String[] paths = location.getPath().split("/");
        if (paths.length != 2)
            return false;
        return paths[1].endsWith(".json") && !paths[1].equals(this.templateLocale + ".json");
    }

    protected JsonObject combine(JsonObject template, JsonObject foreign) {
        JsonObject result = new JsonObject();
        Map<String, JsonElement> unlocalized = new LinkedHashMap<>();
        for (var entry : template.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            if (foreign.has(key)) result.add(key, foreign.get(key));
            else unlocalized.put(key, value);
        }
        if (!unlocalized.isEmpty()) {
            result.addProperty("_comment.unlocalized", "Remove this line after finishing localization.");
            unlocalized.forEach(result::add);
        }
        return result;
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    protected void save(CachedOutput output, String locale, JsonObject result) {
        Path path = this.langPathProvider.json(ResourceLocation.fromNamespaceAndPath(this.modid, locale));
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            HashingOutputStream hashingOutputStream =
                    new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
            try (JsonWriter jsonwriter = new JsonWriter(
                    new OutputStreamWriter(hashingOutputStream, StandardCharsets.UTF_8)
            )) {
                jsonwriter.setSerializeNulls(false);
                jsonwriter.setIndent(" ".repeat(java.lang.Math.max(0, INDENT_WIDTH.get())));
                GsonHelper.writeValue(jsonwriter, result, KEY_COMPARATOR);
            }
            output.writeIfNeeded(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
        } catch (IOException ioexception) {
            LOGGER.error("Failed to save file to {}", path, ioexception);
        }
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        CompletableFuture<JsonObject> readTemplate = this.getTemplateLocalization();
        CompletableFuture<?>[] all = this.resourceManager
                .listResources("lang", this::isForeignLanguageFile)
                .entrySet()
                .stream()
                .map(entry -> {
                    String locale = entry.getKey().getPath().split("/")[1].replace(".json", "");
                    return readTemplate.thenCombineAsync(
                            this.getForeignTemplateLocalization(entry.getValue()),
                            this::combine,
                            Util.backgroundExecutor()
                    ).thenAcceptAsync(result -> this.save(output, locale, result), Util.backgroundExecutor());
                })
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(all);
    }

    @Override
    public String getName() {
        return "ForeignLanguage";
    }
}
