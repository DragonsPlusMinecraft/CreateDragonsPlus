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

package plus.dragons.createdragonsplus.client.ponder.scenes;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;

public class QuickSandScenes {
    public static void bulkSanding(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("bulk_sanding", "Bulk Sanding");
        scene.configureBasePlate(0, 0, 5);
        scene.world().showSection(util.select().layer(0), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(3, 1, 3, 4, 1, 3), Direction.UP);
        scene.idle(3);
        scene.world().showSection(util.select().fromTo(3, 2, 3, 4, 2, 3), Direction.DOWN);
        scene.idle(10);

        scene.world().showSection(util.select().position(5, 2, 3), Direction.WEST);
        scene.idle(5);
        scene.world().setKineticSpeed(util.select().fromTo(4, 2, 3, 5, 2, 3), -8);
        scene.idle(5);

        var airCurrent = util.select().fromTo(3, 2, 3, 0, 2, 3);
        scene.overlay().showOutline(PonderPalette.OUTPUT, airCurrent, airCurrent, 20);
        scene.idle(40);

        scene.overlay()
                .showText(80)
                .pointAt(util.vector().topOf(1, 1, 3))
                .attachKeyFrame()
                .text("Air Flows passing through Quicksand create a Sanding Setup");
        scene.world().showSection(util.select().position(1, 1, 3), Direction.DOWN);
        scene.idle(10);
        scene.world().modifyBlockEntity(util.grid().at(1, 1, 3), DepotBlockEntity.class, depot -> depot.setHeldItem(Blocks.DIORITE.asItem().getDefaultInstance()));
        scene.idle(80);
        scene.world().modifyBlockEntity(util.grid().at(1, 1, 3), DepotBlockEntity.class, depot -> depot.setHeldItem(Blocks.POLISHED_DIORITE.asItem().getDefaultInstance()));
        scene.idle(10);

        scene.world().setKineticSpeed(util.select().fromTo(4, 2, 3, 5, 2, 3), 0);
        scene.idle(3);
        scene.world().hideSection(util.select().position(5, 2, 3), Direction.EAST);
        scene.idle(3);
        scene.world().hideSection(util.select().position(4, 2, 3), Direction.UP);
        scene.idle(3);
        scene.world().hideSection(util.select().position(4, 1, 3), Direction.DOWN);
        scene.idle(3);
        scene.world().showSection(util.select().position(3, 1, 4), Direction.UP);
        scene.idle(3);
        scene.world().showSection(util.select().position(3, 2, 4), Direction.DOWN);
        scene.idle(3);
        scene.world().showSection(util.select().position(3, 2, 5), Direction.NORTH);
        scene.idle(3);
        scene.world().setKineticSpeed(util.select().fromTo(3, 2, 4, 3, 2, 6), -8);
        scene.idle(3);

        scene.overlay()
                .showText(80)
                .pointAt(util.vector().topOf(3, 1, 1))
                .attachKeyFrame()
                .text("Like Bulk Blasting and Bulk Smoking, " +
                        "Bulk Sanding is automatically compatible with Sand Paper sanding recipes");
        scene.world().showSection(util.select().position(3, 1, 1), Direction.DOWN);
        scene.idle(10);
        scene.world().modifyBlockEntity(util.grid().at(3, 1, 1), DepotBlockEntity.class, depot -> depot.setHeldItem(AllItems.ROSE_QUARTZ.asStack()));
        scene.idle(80);
        scene.world().modifyBlockEntity(util.grid().at(3, 1, 1), DepotBlockEntity.class, depot -> depot.setHeldItem(AllItems.POLISHED_ROSE_QUARTZ.asStack()));
        scene.idle(10);
    }
}
