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

package plus.dragons.createdragonsplus.common.kinetics.fan.sanding;

import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import plus.dragons.createdragonsplus.common.registry.CDPBlocks;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions;

public class SandingCatalysts {
    private static final ResourceLocation QUICKSAND = ResourceLocation.fromNamespaceAndPath("quicksand", "quicksand");

    public static boolean hasAnyCatalyst() {
        if (BuiltInRegistries.BLOCK.getOptional(QUICKSAND).isPresent())
            return true;
        return findBlockTag().isPresent();
    }

    public static Optional<HolderSet.Named<Block>> findBlockTag() {
        var base = findNonEmptyTag(CDPBlocks.MOD_TAGS.fanSandingCatalysts);
        if (base.isPresent())
            return base;
        for (var tag : CDPIntegrationContributions.sandingCatalystTags()) {
            var catalyst = findNonEmptyTag(tag);
            if (catalyst.isPresent())
                return catalyst;
        }
        return Optional.empty();
    }

    private static Optional<HolderSet.Named<Block>> findNonEmptyTag(TagKey<Block> tag) {
        var optional = BuiltInRegistries.BLOCK.getTag(tag);
        if (optional.isEmpty() || optional.get().size() == 0)
            return Optional.empty();
        return optional;
    }
}
