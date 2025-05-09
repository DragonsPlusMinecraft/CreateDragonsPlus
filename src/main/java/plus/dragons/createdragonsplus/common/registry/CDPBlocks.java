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

import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.providers.RegistrateTagsProvider.IntrinsicImpl;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.fluids.hatch.FluidHatchBlock;
import plus.dragons.createdragonsplus.data.tag.IntrinsicTagRegistry;

public class CDPBlocks {
    public static final ModTags MOD_TAGS = new ModTags();

    public static final BlockEntry<FluidHatchBlock> FLUID_HATCH = REGISTRATE
            .block("fluid_hatch", FluidHatchBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .transform(pickaxeOnly())
            .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), AssetLookup.standardModel(ctx, prov)))
            .simpleItem()
            .register();

    public static void register(IEventBus modBus) {
        REGISTRATE.registerBlockTags(MOD_TAGS);
    }

    public static class ModTags extends IntrinsicTagRegistry<Block, IntrinsicImpl<Block>> {
        public final TagKey<Block> passiveBlockFreezers = tag("passive_block_freezers", "Passive Block Freezers");
        public final TagKey<Block> fanSandingCatalysts = tag("fan_processing_catalysts/sanding", "Bulk Sanding Catalysts");
        public final TagKey<Block> fanEndingCatalysts = tag("fan_processing_catalysts/ending", "Bulk Ending Catalysts");

        public ModTags() {
            super(CDPCommon.ID, Registries.BLOCK);
        }

        @Override
        public void generate(IntrinsicImpl<Block> provider) {
            provider.addTag(passiveBlockFreezers).add(
                    Blocks.SNOW_BLOCK,
                    Blocks.POWDER_SNOW,
                    Blocks.FROSTED_ICE,
                    Blocks.ICE,
                    Blocks.PACKED_ICE,
                    Blocks.BLUE_ICE);
            provider.addTag(fanSandingCatalysts)
                    .addOptionalTag(ResourceLocation.fromNamespaceAndPath("c", "quicksands"));
            provider.addTag(fanEndingCatalysts);
            provider.addTag(AllBlockTags.FAN_TRANSPARENT.tag)
                    .add(Blocks.DRAGON_HEAD)
                    .add(Blocks.DRAGON_WALL_HEAD);
        }
    }
}
