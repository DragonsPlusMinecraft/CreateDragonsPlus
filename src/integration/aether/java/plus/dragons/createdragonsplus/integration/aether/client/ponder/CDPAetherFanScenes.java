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

package plus.dragons.createdragonsplus.integration.aether.client.ponder;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.passive.Moa;
import com.aetherteam.aether.item.AetherItems;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class CDPAetherFanScenes {
    public static void bulkEnchanting(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("bulk_enchanting", "Bulk Enchanting");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(0.8f);
        scene.showBasePlate();
        scene.idle(5);

        scene.world().showSection(util.select().fromTo(3, 1, 2, 4, 1, 2), Direction.UP);
        scene.idle(10);
        var fan = util.grid().at(4, 2, 2);
        var catalyst = util.grid().at(3, 2, 2);
        scene.world().showSection(util.select().position(fan).add(util.select().position(catalyst)), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(5, 2, 2), Direction.WEST);
        scene.idle(5);
        scene.world().showSection(util.select().position(5, 1, 3), Direction.WEST);
        scene.idle(5);

        scene.world().setKineticSpeed(util.select().position(fan).add(util.select().position(5, 2, 2)), -8);
        scene.world().setKineticSpeed(util.select().position(5, 1, 3), 4);
        var airCurrent = util.select().fromTo(3, 2, 2, 0, 2, 2);
        scene.overlay().showOutline(PonderPalette.OUTPUT, airCurrent, airCurrent, 30);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .pointAt(util.vector().centerOf(catalyst))
                .placeNearTarget()
                .text("Air Flows passing through Golden Aercloud create a Bulk Enchanting setup");
        scene.idle(90);

        var depot = util.grid().at(1, 1, 2);
        scene.world().showSection(util.select().position(depot), Direction.DOWN);
        scene.idle(10);
        var altar = util.grid().at(4, 1, 0);
        scene.world().showSection(util.select().position(altar), Direction.DOWN);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .pointAt(util.vector().topOf(depot))
                .placeNearTarget()
                .text("Bulk Enchanting processes Aether Enchanter recipes with fan processing");
        scene.world().modifyBlockEntity(depot, DepotBlockEntity.class, it -> it.setHeldItem(AetherItems.SKYROOT_PICKAXE.get().getDefaultInstance()));
        scene.idle(60);
        scene.world().modifyBlockEntity(depot, DepotBlockEntity.class, it -> it.setHeldItem(AetherItems.ZANITE_PICKAXE.get().getDefaultInstance()));
        scene.idle(25);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .pointAt(util.vector().topOf(depot))
                .placeNearTarget()
                .text("Some recipes repair equipment, so the input and output may be the same item type");
        scene.idle(90);

        var incubator = util.grid().at(0, 1, 0);
        scene.world().setBlock(incubator, AetherBlocks.INCUBATOR.get().defaultBlockState(), false);
        scene.world().showSection(util.select().position(incubator), Direction.DOWN);
        scene.world().modifyBlockEntity(depot, DepotBlockEntity.class, it -> it.setHeldItem(AetherItems.BLUE_MOA_EGG.get().getDefaultInstance()));
        scene.overlay().showText(85)
                .attachKeyFrame()
                .pointAt(util.vector().topOf(depot))
                .placeNearTarget()
                .text("The same airflow can incubate Moa Eggs as item processing");
        scene.idle(85);

        scene.world().modifyBlockEntity(depot, DepotBlockEntity.class, it -> it.setHeldItem(new ItemStack(AetherItems.MOA_SPAWN_EGG.get())));
        scene.idle(45);
        scene.world().modifyBlockEntity(depot, DepotBlockEntity.class, DepotBlockEntity::clearContent);
        scene.world().createEntity(level -> {
            Moa moa = new Moa(AetherEntityTypes.MOA.get(), level);
            Vec3 p = util.vector().topOf(depot);
            moa.setPos(p.x, p.y, p.z);
            moa.xo = p.x;
            moa.yo = p.y;
            moa.zo = p.z;
            WalkAnimationState animation = moa.walkAnimation;
            animation.update(-animation.position(), 1);
            animation.setSpeed(1);
            moa.yRotO = 210;
            moa.setYRot(210);
            moa.yHeadRotO = 210;
            moa.yHeadRot = 210;
            return moa;
        });
        scene.idle(40);
    }
}
