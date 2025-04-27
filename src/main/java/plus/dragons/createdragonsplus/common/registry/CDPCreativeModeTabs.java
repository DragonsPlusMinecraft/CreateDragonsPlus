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

import static plus.dragons.createdragonsplus.common.registry.CDPBlocks.*;
import static plus.dragons.createdragonsplus.common.registry.CDPItems.*;

import com.simibubi.create.AllCreativeModeTabs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeColors;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.util.CDPLang;

public class CDPCreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, CDPCommon.ID);
    public static final Holder<CreativeModeTab> BASE = TABS.register("base", CDPCreativeModeTabs::base);

    public static void register(IEventBus modBus) {
        TABS.register(modBus);
    }

    private static CreativeModeTab base(ResourceLocation id) {
        return CreativeModeTab.builder()
                .title(CDPLang.description("itemGroup", id).component())
                .withTabsBefore(AllCreativeModeTabs.BASE_CREATIVE_TAB.getId())
                .icon(RARE_MARBLE_GATE_PACKAGE::asStack)
                .displayItems(CDPCreativeModeTabs::buildBaseContents)
                .build();
    }

    private static void buildBaseContents(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        if (CDPConfig.features().fluidHatch.get())
            output.accept(FLUID_HATCH);
        if (CDPConfig.features().blazeUpgradeSmithingTemplate.get())
            output.accept(BLAZE_UPGRADE_SMITHING_TEMPLATE);
        if (CDPConfig.features().dyeFluids.get())
            for (var color : DyeColors.CREATIVE_MODE_TAB) {
                CDPFluids.DYES_BY_COLOR.get(color).getBucket().ifPresent(output::accept);
            }
        output.accept(RARE_BLAZE_PACKAGE, TabVisibility.SEARCH_TAB_ONLY);
        output.accept(RARE_MARBLE_GATE_PACKAGE, TabVisibility.SEARCH_TAB_ONLY);
    }
}
