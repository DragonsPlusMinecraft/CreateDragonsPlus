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

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;

public class CDPFluidScenes {
    public static void dyeFluids(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("dye_fluids", "Liquid Dye");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(0.85f);
        scene.showBasePlate();
        scene.idle(5);

        var basin = util.grid().at(1, 2, 2);
        var mixer = util.grid().at(1, 4, 2);
        var sect = scene.world().showIndependentSection(util.select().position(basin).add(util.select().position(mixer)), Direction.DOWN);
        scene.world().moveSection(sect, new Vec3(0, -1, 0), 0);
        scene.idle(10);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .pointAt(util.vector().topOf(basin.below()))
                .placeNearTarget()
                .text("Mixing dye with water creates Liquid Dye");
        scene.overlay().showControls(util.vector().topOf(basin.below()).add(-0.55, 0, 0), Pointing.DOWN, 40)
                .withItem(Items.LIME_DYE.getDefaultInstance());
        scene.overlay().showControls(util.vector().topOf(basin.below()).add(0.55, 0, 0), Pointing.DOWN, 40)
                .withItem(Items.WATER_BUCKET.getDefaultInstance());
        scene.world().createItemOnBeltLike(basin, Direction.UP, Items.LIME_DYE.getDefaultInstance());
        scene.world().modifyBlockEntity(basin, BasinBlockEntity.class, be -> {
            be.getTanks().getFirst().getPrimaryHandler().setFluid(new FluidStack(Fluids.WATER, 4000));
        });
        scene.idle(10);
        scene.world().setKineticSpeed(util.select().position(mixer), 32);
        scene.world().modifyBlockEntity(mixer, MechanicalMixerBlockEntity.class, MechanicalMixerBlockEntity::startProcessingBasin);
        scene.idle(40);
        scene.world().modifyBlockEntity(basin, BasinBlockEntity.class, be -> {
            be.getTanks().getFirst().getPrimaryHandler()
                    .setFluid(new FluidStack(CDPFluids.DYES_BY_VARIANT.get(new ResourceLocation("lime")).getSource(), 4000));
        });
        scene.idle(45);

        var burner = util.grid().at(1, 1, 2);
        scene.world().moveSection(sect, new Vec3(0, 1, 0), 10);
        scene.idle(10);
        scene.world().showSection(util.select().position(burner), Direction.UP);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget()
                .text("Heated mixing can recover the dye item from the fluid");
        scene.world().modifyBlockEntity(mixer, MechanicalMixerBlockEntity.class, MechanicalMixerBlockEntity::startProcessingBasin);
        scene.idle(40);
        scene.world().modifyBlockEntity(basin, BasinBlockEntity.class, be -> {
            be.getTanks().getFirst().getPrimaryHandler().setFluid(new FluidStack(Fluids.WATER, 4000));
        });
        scene.idle(40);

        var pool = util.grid().at(3, 1, 2);
        scene.world().showSection(util.select().position(pool), Direction.DOWN);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .pointAt(util.vector().topOf(pool))
                .placeNearTarget()
                .text("Items and living entities touching Liquid Dye are colored");
        scene.idle(5);
        var sheep = scene.world().createEntity(level -> {
            Sheep s = new Sheep(EntityType.SHEEP, level);
            s.setColor(DyeColor.WHITE);
            Vec3 p = util.vector().centerOf(pool);
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
        scene.idle(35);
        scene.world().modifyEntity(sheep, it -> ((Sheep) it).setColor(DyeColor.LIME));
        scene.idle(40);
        scene.world().modifyEntity(sheep, Entity::discard);
        scene.idle(20);

        var lava = util.grid().at(4, 1, 2);
        scene.world().setBlock(lava, Blocks.LAVA.defaultBlockState(), false);
        scene.world().showSection(util.select().position(lava), Direction.DOWN);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .pointAt(util.vector().centerOf(lava))
                .placeNearTarget()
                .text("When Liquid Dye meets lava, it forms matching concrete");
        scene.idle(5);
        scene.world().setBlock(pool, Blocks.LIME_CONCRETE.defaultBlockState(), true);
        scene.idle(20);
        scene.world().showSection(util.select().position(4, 1, 3), Direction.DOWN);
        scene.idle(5);
        scene.world().setBlock(lava, Blocks.LIME_CONCRETE.defaultBlockState(), true);
        scene.idle(45);
    }

    public static void dragonBreathFluid(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("dragon_breath_fluid", "Dragon's Breath Fluid");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(0.85f);
        scene.showBasePlate();
        scene.idle(5);

        var cauldron = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().position(cauldron), Direction.DOWN);
        scene.idle(3);
        scene.world().showSection(util.select().fromTo(2, 2, 2, 2, 3, 2).add(util.select().fromTo(1, 4, 1, 3, 4, 3)), Direction.DOWN);
        scene.overlay().showText(75)
                .attachKeyFrame()
                .pointAt(util.vector().topOf(cauldron))
                .placeNearTarget()
                .text("Dragon's Breath supports Cauldron behaviour");
        scene.idle(85);

        var left = util.grid().at(3, 1, 0);
        scene.world().showSection(util.select().position(left), Direction.DOWN);
        scene.idle(10);
        var lava = util.grid().at(3, 2, 0);
        scene.world().setBlock(lava, Blocks.LAVA.defaultBlockState(), false);
        scene.world().showSection(util.select().position(lava), Direction.DOWN);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .pointAt(util.vector().centerOf(lava))
                .placeNearTarget()
                .text("When flowing Dragon's Breath meets lava, it forms End Stone");
        scene.idle(5);
        scene.world().setBlock(left, Blocks.END_STONE.defaultBlockState(), true);
        scene.idle(75);
    }
}
