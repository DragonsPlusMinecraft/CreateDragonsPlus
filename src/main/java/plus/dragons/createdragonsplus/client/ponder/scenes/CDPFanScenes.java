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

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.decoration.placard.PlacardBlock;
import com.simibubi.create.content.decoration.placard.PlacardBlockEntity;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import java.util.List;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;

public class CDPFanScenes {
    public static void bulkColoring(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("bulk_coloring", "Bulk Coloring");
        scene.configureBasePlate(1, 0, 5);
        var base = util.select().layer(0)
                .add(util.select().position(6, 3, 1));
        scene.world().showSection(base, Direction.UP);
        scene.idle(5);
        var fan = util.select().fromTo(5, 2, 1, 6, 2, 2);
        scene.world().showSection(fan, Direction.DOWN);
        scene.idle(5);
        var fanBase = util.select().fromTo(4, 1, 2, 5, 1, 2);
        scene.world().showSection(fanBase, Direction.UP);
        scene.idle(10);
        var blockInFront = scene.world().makeSectionIndependent(util.select().position(3, 1, 2));
        scene.world().moveSection(blockInFront, util.vector().of(1, 1, 0), 0);
        var dye = CDPFluids.DYES_BY_COLOR.get(DyeColor.LIME).getSource().defaultFluidState().createLegacyBlock();
        scene.world().setBlock(util.grid().at(4, 2, 2), dye, false);
        scene.idle(10);
        var airCurrent = util.select().fromTo(4, 2, 2, 1, 2, 2);
        scene.overlay()
                .showOutline(PonderPalette.GREEN, airCurrent, airCurrent, 20);
        scene.idle(40);
        ItemStack redWool = new ItemStack(Items.RED_WOOL);
        ItemStack limeWool = new ItemStack(Items.LIME_WOOL);
        var belt = util.select().fromTo(0, 1, 1, 2, 2, 4);
        scene.world().showSection(belt, Direction.DOWN);
        var transported = scene.world().createItemOnBelt(util.grid().at(2, 1, 3), Direction.SOUTH, redWool);
        scene.overlay()
                .showText(80)
                .pointAt(util.vector().topOf(2, 1, 2))
                .attachKeyFrame()
                .text("Air Flows passing through Liquid Dye create a Coloring Setup");
        scene.idle(80);
        scene.world().changeBeltItemTo(transported, limeWool);
        scene.idle(80);
        scene.world().removeItemsFromBelt(util.grid().at(2, 1, 1));
        scene.idle(10);
        ItemStack dough = new ItemStack(AllItems.DOUGH.asItem());
        ItemStack slimeBall = new ItemStack(Items.SLIME_BALL);
        var placard = AllBlocks.PLACARD.getDefaultState()
                .setValue(PlacardBlock.FACE, AttachFace.WALL)
                .setValue(PlacardBlock.FACING, Direction.NORTH);
        var placardPos0 = util.grid().at(2, 2, 3);
        var placardPos1 = util.grid().at(1, 1, 1);
        scene.world().setBlock(placardPos0, placard, true);
        scene.world().modifyBlockEntity(placardPos0, PlacardBlockEntity.class, it -> it.setHeldItem(dough));
        scene.world().setBlock(placardPos1, placard, true);
        scene.world().modifyBlockEntity(placardPos1, PlacardBlockEntity.class, it -> it.setHeldItem(slimeBall));
        transported = scene.world().createItemOnBelt(util.grid().at(2, 1, 3), Direction.SOUTH, dough);
        scene.overlay()
                .showText(80)
                .pointAt(util.vector().topOf(2, 1, 2))
                .attachKeyFrame()
                .text("Like Bulk Blasting and Bulk Smoking, " +
                        "Bulk Coloring is automatically compatible with certain crafting recipes using dyes");
        scene.idle(80);
        scene.world().changeBeltItemTo(transported, slimeBall);
        scene.idle(80);
        scene.world().removeItemsFromBelt(util.grid().at(2, 1, 1));
        scene.idle(10);
        scene.world().setKineticSpeed(belt.add(util.select().position(0, 0, 4)), 0);
        var pos = util.grid().at(1, 2, 2);
        var sheep = scene.world().createEntity(level -> {
            Sheep s = new Sheep(EntityType.SHEEP, level);
            s.setColor(DyeColor.WHITE);
            Vec3 p = pos.getBottomCenter();
            s.setPos(p.x, p.y, p.z);
            s.xo = p.x;
            s.yo = p.y;
            s.zo = p.z;
            WalkAnimationState animation = s.walkAnimation;
            animation.update(-animation.position(), 1);
            animation.setSpeed(1);
            s.yRotO = 210;
            s.setYRot(210);
            s.yHeadRotO = 210;
            s.yHeadRot = 210;
            return s;
        });
        scene.overlay()
                .showText(80)
                .pointAt(util.vector().topOf(pos))
                .attachKeyFrame()
                .text("Coloring Air Flow can also affect entities, changing their color.");
        scene.idle(80);
        scene.world().modifyEntity(sheep, it -> ((Sheep) it).setColor(DyeColor.LIME));
        scene.idle(80);
        scene.world().modifyEntity(sheep, Entity::discard);
        scene.idle(10);
        var armorStand = scene.world().createEntity(level -> {
            ArmorStand as = new ArmorStand(EntityType.ARMOR_STAND, level);
            as.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
            as.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
            as.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
            as.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
            as.setPos(pos.getBottomCenter());
            as.lookAt(Anchor.EYES, as.getEyePosition().add(0, 0, -1));
            return as;
        });
        scene.overlay()
                .showText(80)
                .pointAt(util.vector().topOf(pos))
                .attachKeyFrame()
                .text("Equipped armor will also be colored.");
        scene.idle(80);
        scene.world().modifyEntity(armorStand, it -> {
            var as = (ArmorStand) it;
            var slots = new EquipmentSlot[] {
                    EquipmentSlot.FEET,
                    EquipmentSlot.LEGS,
                    EquipmentSlot.CHEST,
                    EquipmentSlot.HEAD,
            };
            for (var slot : slots) {
                var armor = as.getItemBySlot(slot);
                var dyedArmor = DyedItemColor.applyDyes(armor, List.of(DyeItem.byColor(DyeColor.LIME)));
                as.setItemSlot(slot, dyedArmor);
            }
        });
        scene.idle(80);
        scene.world().modifyEntity(sheep, Entity::discard);
        scene.idle(20);
    }

    public static void bulkFreezing(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("bulk_freezing", "Bulk Freezing");
        scene.configureBasePlate(0, 1, 5);
        scene.world().showSection(util.select().layer(0), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(2, 1, 4, 2, 1, 5), Direction.UP);
        scene.world().showSection(util.select().fromTo(2, 2, 5, 2, 2, 7), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showControls(util.vector().centerOf(2, 3, 4), Pointing.DOWN, 20).withItem(Items.POWDER_SNOW_BUCKET.getDefaultInstance()).rightClick();

        // Temporary solution for Powder Snow does not render in ponder.
        // See https://github.com/Creators-of-Create/Ponder/issues/26
        var snow = scene.world().showIndependentSection(util.select().position(2, 3, 4), Direction.DOWN);
        scene.world().moveSection(snow, new Vec3(0, -1, 0), 0);

        scene.world().setKineticSpeed(util.select().position(3, 0, 0), 4);
        scene.world().setKineticSpeed(util.select().fromTo(2, 2, 5, 2, 2, 6), -8);
        scene.idle(30);

        var airCurrent = util.select().fromTo(2, 2, 1, 2, 2, 4);
        scene.overlay().showOutline(PonderPalette.WHITE, airCurrent, airCurrent, 20);
        scene.idle(40);

        scene.overlay()
                .showText(80)
                .pointAt(util.vector().topOf(2, 1, 2))
                .attachKeyFrame()
                .text("Air Flows passing through Powder Snow create a Freezing Setup");

        var belt = util.select().fromTo(0, 1, 2, 5, 1, 2).add(util.select().fromTo(4, 1, 1, 4, 1, 0));
        scene.world().setKineticSpeed(belt, -4);
        scene.world().showSection(belt, Direction.DOWN);
        var transported = scene.world().createItemOnBelt(util.grid().at(1, 1, 2), Direction.DOWN, Items.BLAZE_ROD.getDefaultInstance());
        scene.idle(160);
        scene.world().changeBeltItemTo(transported, Items.BREEZE_ROD.getDefaultInstance());
        scene.idle(30);
    }

    public static void bulkEnding(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("bulk_ending", "Bulk Ending");
        scene.configureBasePlate(0, 0, 5);
        scene.world().showSection(util.select().layer(0).substract(util.select().position(1, 0, 5)), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(3, 1, 3, 3, 1, 4), Direction.UP);
        scene.idle(3);
        scene.world().showSection(util.select().fromTo(1, 1, 3, 1, 1, 4), Direction.UP);
        scene.idle(3);
        scene.world().showSection(util.select().fromTo(3, 2, 3, 3, 2, 4), Direction.DOWN);
        scene.idle(3);
        scene.world().showSection(util.select().fromTo(1, 2, 3, 1, 2, 4), Direction.DOWN);
        scene.idle(10);

        scene.world().showSection(util.select().fromTo(0, 0, 5, 4, 2, 5), Direction.NORTH);
        scene.idle(5);
        var bigCog = util.select().position(2, 1, 5);
        scene.world().setKineticSpeed(bigCog, 4);
        scene.world().setKineticSpeed(util.select().everywhere().substract(bigCog), -8);
        scene.idle(5);

        var airCurrent = util.select().fromTo(1, 2, 0, 1, 2, 3).add(util.select().fromTo(3, 2, 0, 3, 2, 3));
        scene.overlay().showOutline(PonderPalette.BLACK, airCurrent, airCurrent, 20);
        scene.idle(40);

        scene.overlay()
                .showText(80)
                .pointAt(util.vector().topOf(1, 1, 1))
                .attachKeyFrame()
                .text("Air Flows passing through Dragon Head or Dragon Breath create a Ending Setup");
        scene.world().showSection(util.select().position(1, 1, 1).add(util.select().position(3, 1, 1)), Direction.DOWN);
        scene.idle(10);
        scene.world().modifyBlockEntity(util.grid().at(1, 1, 1), DepotBlockEntity.class, depot -> depot.setHeldItem(Items.COBBLESTONE.getDefaultInstance()));
        scene.world().modifyBlockEntity(util.grid().at(3, 1, 1), DepotBlockEntity.class, depot -> depot.setHeldItem(Items.LEATHER.getDefaultInstance()));
        scene.idle(80);
        scene.world().modifyBlockEntity(util.grid().at(1, 1, 1), DepotBlockEntity.class, depot -> depot.setHeldItem(Items.END_STONE.getDefaultInstance()));
        scene.world().modifyBlockEntity(util.grid().at(3, 1, 1), DepotBlockEntity.class, depot -> depot.setHeldItem(Items.PHANTOM_MEMBRANE.getDefaultInstance()));
    }
}
