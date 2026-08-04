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

package plus.dragons.createdragonsplus.client.ponder;

import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import java.util.List;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import plus.dragons.createdragonsplus.client.ponder.scenes.CDPFanScenes;
import plus.dragons.createdragonsplus.client.ponder.scenes.CDPFluidScenes;
import plus.dragons.createdragonsplus.client.ponder.scenes.SandingScenes;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariantRegistry;
import plus.dragons.createdragonsplus.common.registry.CDPCauldrons;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;

public class CDPPonderScenes {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        var registration = helper.<ItemProviderEntry<?>>withKeyFunction(RegistryEntry::getId);
        registration.forComponents(AllBlocks.ENCASED_FAN)
                .addStoryBoard("bulk_coloring", CDPFanScenes::bulkColoring)
                .addStoryBoard("bulk_freezing", CDPFanScenes::bulkFreezing)
                .addStoryBoard("bulk_ending", CDPFanScenes::bulkEnding)
                .addStoryBoard("bulk_sanding", SandingScenes::bulkSanding);

        var itemRegistration = helper.<ItemLike>withKeyFunction(CatnipServices.REGISTRIES::getKeyOrThrow);
        List<ItemLike> dyeBuckets = DyeVariantRegistry.all().stream()
                .filter(variant -> variant.isAvailable())
                .flatMap(variant -> CDPFluids.DYES_BY_VARIANT.get(variant.id()).getBucket().stream())
                .map(bucket -> (ItemLike) bucket)
                .toList();
        itemRegistration.forComponents(dyeBuckets)
                .addStoryBoard("dye_fluids", CDPFluidScenes::dyeFluids);
        itemRegistration.forComponents(
                CDPFluids.DRAGON_BREATH.getBucket().get(),
                CDPCauldrons.DRAGON_BREATH_CAULDRON.get(),
                Items.DRAGON_BREATH)
                .addStoryBoard("dragon_breath_fluid", CDPFluidScenes::dragonBreathFluid);
    }
}
