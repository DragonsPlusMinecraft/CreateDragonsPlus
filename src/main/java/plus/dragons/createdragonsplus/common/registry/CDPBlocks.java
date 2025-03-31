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

package plus.dragons.createdragonsplus.common.registry;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;
import static plus.dragons.createdragonsplus.data.recipe.VanillaRecipeBuilders.shapeless;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import plus.dragons.createdragonsplus.common.features.ConfigFeatureBlockItem;
import plus.dragons.createdragonsplus.common.fluids.hatch.FluidHatchBlock;
import plus.dragons.createdragonsplus.config.CDPConfig;

public class CDPBlocks {
    public static final BlockEntry<FluidHatchBlock> FLUID_HATCH = REGISTRATE
            .block("fluid_hatch", FluidHatchBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .transform(pickaxeOnly())
            .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), AssetLookup.standardModel(ctx, prov)))
            .item(ConfigFeatureBlockItem::new)
            .recipe((ctx, prov) -> shapeless()
                    .output(ctx.get())
                    .require(Tags.Items.INGOTS_COPPER)
                    .require(AllBlocks.ITEM_DRAIN)
                    .withCondition(CDPConfig.features().fluidHatch)
                    .accept(prov))
            .build()
            .register();
    public static void register(IEventBus modBus) {}
}
