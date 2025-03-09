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

package plus.dragons.createdragonsplus.integration.ponder;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import plus.dragons.createdragonsplus.common.CDPCommon;


public class CDPPonder {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
        var buckets = CDPCommon.REGISTRATE.getAll(Registries.ITEM).stream().map(ItemEntry::cast).toList();
        HELPER.forComponents(buckets).addStoryBoard("bulk_dye", CDPPonder::bulkDye);
    }

    public static void bulkDye(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("bulk_dye", "Bulk Dyeing");
        scene.configureBasePlate(0, 0, 10);
        scene.scaleSceneView(.60f);
        scene.world().setKineticSpeed(util.select().everywhere(),0);

        scene.showBasePlate();
        scene.idle(15);
        scene.world().showSection(util.select().fromTo(9, 1, 0, 8, 3, 9), Direction.DOWN);
        scene.idle(15);

        scene.world().showSection(util.select().fromTo(7, 1, 0, 5, 3, 9), Direction.DOWN);
        scene.idle(30);
        scene.world().setKineticSpeed(util.select().everywhere(),-24);
        scene.idle(30);

        scene.world().setKineticSpeed(util.select().fromTo(4, 1, 0, 4, 3, 9),0);
        scene.world().showSection(util.select().fromTo(4, 1, 0, 4, 3, 9), Direction.DOWN);
        scene.idle(15);
        scene.world().setKineticSpeed(util.select().fromTo(4, 1, 0, 4, 3, 9),-12);
        scene.idle(15);

        scene.overlay().showText(90)
                .pointAt(util.vector().topOf(6, 2, 6))
                .attachKeyFrame()
                .text("Air Flows passing through liquid dye create a bulk dyeing Setup");
        ItemStack paper = new ItemStack(Items.PAPER);
        ItemStack schematic = new ItemStack(AllItems.SCHEMATIC.asItem());
        var link = scene.world().createItemOnBelt(new BlockPos(4,1,7), Direction.DOWN, paper);
        scene.idle(80);
        scene.world().changeBeltItemTo(link, schematic);
        scene.idle(40);

        scene.world().showSection(util.select().fromTo(3, 1, 0, 1, 3, 9), Direction.DOWN);
        scene.world().setKineticSpeed(util.select().fromTo(8,1,0,8,3,9),-64);
        scene.idle(30);

        scene.world().setKineticSpeed(util.select().fromTo(0, 1, 0, 0, 3, 9),0);
        scene.world().showSection(util.select().fromTo(0, 1, 0, 0, 3, 9), Direction.DOWN);
        scene.idle(15);
        scene.world().setKineticSpeed(util.select().fromTo(0, 1, 0, 0, 3, 9),-12);
        scene.idle(15);

        scene.world().setKineticSpeed(util.select().fromTo(4, 1, 0, 4, 3, 9),0);

        scene.overlay().showText(90)
                .pointAt(util.vector().topOf(2, 2, 6))
                .attachKeyFrame()
                .text("As with other bulk processes, bulk dyeing is automatically compatible with all crafting recipes with dyes");
        ItemStack dough = new ItemStack(AllItems.DOUGH.asItem());
        ItemStack slimeBall = new ItemStack(Items.SLIME_BALL);
        link = scene.world().createItemOnBelt(new BlockPos(0,1,7), Direction.DOWN, dough);
        scene.idle(80);
        scene.world().changeBeltItemTo(link, slimeBall);
        scene.idle(100);
        scene.world().setKineticSpeed(util.select().fromTo(0, 1, 0, 0, 3, 9),0);
    }
}
