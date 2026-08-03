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

package plus.dragons.createdragonsplus.data.runtime;

import com.google.common.base.Stopwatch;
import com.google.common.hash.HashCode;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.pack.DynamicPack;
import com.simibubi.create.foundation.pack.DynamicPackSource;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.fml.ModContainer;
import org.slf4j.Logger;

/** An in-memory data pack populated through vanilla data providers. */
public final class RuntimePackResources extends DynamicPack implements RepositorySource, CachedOutput {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Path OUTPUT_ROOT = Path.of("runtime");

    private final PackType type;
    private final DynamicPackSource source;
    private final PackOutput output = new PackOutput(OUTPUT_ROOT);

    public RuntimePackResources(String name, ModContainer modContainer, PackType type, Pack.Position position) {
        super(modContainer.getModInfo().getModId() + "/" + name, type);
        this.type = type;
        this.source = new DynamicPackSource(packId(), type, position, this);
    }

    public PackOutput getPackOutput() {
        return output;
    }

    public void addDataProvider(DataProvider provider) {
        LOGGER.info("Starting provider [{}] for runtime resource [{}]", provider, packId());
        Stopwatch stopwatch = Stopwatch.createStarted();
        provider.run(this).join();
        LOGGER.info("{} finished after {} ms", provider, stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Override
    public void loadPacks(Consumer<Pack> loader) {
        source.loadPacks(loader);
    }

    @Override
    public void writeIfNeeded(Path filePath, byte[] data, HashCode hashCode) {
        Path normalized = filePath.normalize();
        int dataIndex = -1;
        for (int i = 0; i < normalized.getNameCount(); i++) {
            if (type.getDirectory().equals(normalized.getName(i).toString())) {
                dataIndex = i;
                break;
            }
        }
        if (dataIndex < 0 || dataIndex + 2 >= normalized.getNameCount()) {
            LOGGER.warn("Ignoring invalid runtime pack output path: {}", filePath);
            return;
        }

        String namespace = normalized.getName(dataIndex + 1).toString();
        StringBuilder path = new StringBuilder();
        for (int i = dataIndex + 2; i < normalized.getNameCount(); i++) {
            if (!path.isEmpty())
                path.append('/');
            path.append(normalized.getName(i));
        }
        put(new ResourceLocation(namespace, path.toString()), data);
    }
}
