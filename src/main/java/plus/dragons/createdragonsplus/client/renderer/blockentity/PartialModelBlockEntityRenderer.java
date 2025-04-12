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

package plus.dragons.createdragonsplus.client.renderer.blockentity;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.apache.commons.lang3.ArrayUtils;

public interface PartialModelBlockEntityRenderer {
    RenderType[] REVERSED_CHUNK_BUFFER_LAYERS = Util.make(
            RenderType.chunkBufferLayers().toArray(RenderType[]::new),
            ArrayUtils::reverse);

    default RenderType getRenderType(BlockState blockState, PartialModel model) {
        ChunkRenderTypeSet types = model.get().getRenderTypes(blockState, RandomSource.create(42L), ModelData.EMPTY);
        for (RenderType type : REVERSED_CHUNK_BUFFER_LAYERS)
            if (types.contains(type))
                return type;
        return RenderType.cutoutMipped();
    }
}
