package plus.dragons.createdragonsplus.integration.simulated.client.ponder;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.simulated_team.simulated.ponder.SmoothMovementUtils;
import dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.phys.Vec3;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;

public class SEPonderScenes {
    public static void fragileFluidTank(final SceneBuilder builder, final SceneBuildingUtil util) {
        var scene = new CreateSceneBuilder(builder);
        var world = scene.world();
        scene.title("fragile_fluid_tank", "Fragile Fluid Tank");
        scene.configureBasePlate(0, 0, 6);
        scene.scaleSceneView(0.63f);
        scene.showBasePlate();

        scene.idle(10);
        scene.overlay().showText(80)
                .pointAt(util.vector().of(2.22, 4.2, 3.1))
                .text("These tanks are fragile. They break on impact")
                .attachKeyFrame();
        var t1 = world.showIndependentSection(util.select().position(0, 1, 0), Direction.UP);
        world.moveSection(t1, new Vec3(2.22, 3, 3.1), 0);
        scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(t1, new Vec3(25, 4, 6), 120, SmoothMovementUtils.linear()));
        scene.idle(15);
        var t2 = world.showIndependentSection(util.select().fromTo(1, 1, 0,1,2,0), Direction.UP);
        world.moveSection(t2, new Vec3(-1.42, 0.6, 1.1), 0);
        scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(t2, new Vec3(0, 47, 0), 120, SmoothMovementUtils.linear()));
        scene.idle(15);
        var t3 = world.showIndependentSection(util.select().position(2, 1, 0), Direction.UP);
        world.moveSection(t3, new Vec3(4.33, 3.87, 2.63), 0);
        scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(t3, new Vec3(4, 5, 17), 120, SmoothMovementUtils.linear()));
        scene.idle(50);

        var hoverArea = util.select().fromTo(0, 1, 2, 5, 2, 5);
        world.setKineticSpeed(util.select().everywhere(),128);

        var hoverCraft = world.showIndependentSection(hoverArea, Direction.UP);
        scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(hoverCraft, new Vec3(12, 0, 0), 0, SmoothMovementUtils.linear()));
        world.moveSection(hoverCraft, new Vec3(0.33, 4, 21), 0);
        scene.addInstruction(CustomAnimateWorldSectionInstruction.move(hoverCraft, new Vec3(0, -1, -20), 120, SmoothMovementUtils.linear()));

        scene.idle(120);
        scene.addKeyframe();
        world.setBlock(util.grid().at(0,1,0), Blocks.AIR.defaultBlockState(), false);
        world.setKineticSpeed(util.select().everywhere(),0);
        world.setBlock(util.grid().at(2,2,4), Blocks.FIRE.defaultBlockState(), false);
        world.setBlock(util.grid().at(3,2,4), Blocks.FIRE.defaultBlockState(), false);
        world.setBlock(util.grid().at(0,1,4), Blocks.FIRE.defaultBlockState().setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(Direction.EAST),true), false);
        world.setBlock(util.grid().at(5,1,4), Blocks.FIRE.defaultBlockState().setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(Direction.WEST),true), false);

        scene.idle(5);
        scene.overlay().showText(80)
                .independent()
                .text("When fragile Fluid Tanks breaks, fluid effect will be applied to surrounding");
        scene.addInstruction(CustomAnimateWorldSectionInstruction.move(hoverCraft, new Vec3(0, -0.5, -10), 100, SmoothMovementUtils.quadraticRiseOut()));
        scene.idle(78);
        world.setBlock(util.grid().at(0,1,4), Blocks.AIR.defaultBlockState(), false);
        world.setBlock(util.grid().at(1,1,4), Blocks.AIR.defaultBlockState(), false);
        scene.idle(12);
    }

    public static void fan(final SceneBuilder builder, final SceneBuildingUtil util) {
        var scene = new CreateSceneBuilder(builder);
        var world = scene.world();
        scene.title("air_current_interact_with_block", "Air Currents of Bulk Processing from Simulated Contraptions");
        scene.configureBasePlate(0, 0, 9);
        scene.scaleSceneView(0.8f);
        scene.showBasePlate();

        scene.idle(10);
        var hoverArea = util.select().fromTo(0, 1, 3, 5, 3, 8);
        var hoverInnerRing = util.select().fromTo(1, 1, 4, 4, 3, 7);
        var hoverOutRing = hoverArea.substract(hoverInnerRing);

        var hoverCraft1 = world.showIndependentSection(hoverInnerRing, Direction.UP);
        var hoverCraft2 = world.showIndependentSection(hoverOutRing, Direction.UP);
        world.moveSection(hoverCraft1, new Vec3(0, 1.12, 0), 0);
        world.moveSection(hoverCraft2, new Vec3(0, 0.12, 0), 0);

        scene.overlay().showText(80)
                .independent()
                .text("Air Current of Bulk Processing from Simulated Contraptions can affect block")
                .attachKeyFrame();
        world.setBlock(util.grid().at(2,3,3), CDPFluids.DYES_BY_COLOR.get(DyeColor.LIME).getSource().defaultFluidState().createLegacyBlock(), false);
        scene.idle(15);
        world.setBlock(util.grid().at(5,3,5), Blocks.LAVA.defaultBlockState(), false);
        scene.idle(15);
        world.setBlock(util.grid().at(3,3,8), Blocks.WATER.defaultBlockState(), false);
        scene.idle(15);
        world.setBlock(util.grid().at(0,3,6), Blocks.POWDER_SNOW.defaultBlockState(), false);
        scene.idle(15);

        world.showSection(util.select().layers(1,4).substract(hoverArea), Direction.DOWN);
        scene.idle(20);

        world.setKineticSpeed(util.select().everywhere(),64f);
        scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(hoverCraft1, new Vec3(0, 360, 0), 180, SmoothMovementUtils.linear()));
        scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(hoverCraft2, new Vec3(0, 360, 0), 180, SmoothMovementUtils.linear()));
        world.setBlock(util.grid().at(2,3,1), Blocks.LIME_WOOL.defaultBlockState(), false);
        world.setBlock(util.grid().at(7,3,5), Blocks.WATER.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(2,3,1), Blocks.LIME_WOOL.defaultBlockState(), false);
        world.setBlock(util.grid().at(7,3,4), Blocks.WATER.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(1,3,1), Blocks.LIME_WOOL.defaultBlockState(), false);
        world.setBlock(util.grid().at(7,4,3), Blocks.FIRE.defaultBlockState(), false);
        scene.overlay().showText(80)
                .pointAt(util.vector().centerOf(7,4,3))
                .attachKeyFrame()
                .placeNearTarget()
                .text("\"Bulk Blasting Air Current spreads fire\" is off by default in config to prevent new players from burning their properties accidentally");

        scene.idle(5);
        world.setBlock(util.grid().at(7,4,2), Blocks.FIRE.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(7,4,1), Blocks.FIRE.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(6,4,1), Blocks.FIRE.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(5,4,1), Blocks.FIRE.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(4,4,1), Blocks.FIRE.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(3,4,1), Blocks.FIRE.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(2,4,1), Blocks.FIRE.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(1,4,1), Blocks.FIRE.defaultBlockState(), false);
        world.setBlock(util.grid().at(7,4,3), Blocks.AIR.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(7,4,2), Blocks.AIR.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(7,4,1), Blocks.AIR.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(6,4,1), Blocks.AIR.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(5,4,1), Blocks.AIR.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(7,3,7), Blocks.ICE.defaultBlockState(), false);
        world.setBlock(util.grid().at(4,4,1), Blocks.AIR.defaultBlockState(), false);
        world.setBlock(util.grid().at(4,3,1), Blocks.WHITE_WOOL.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(7,3,6), Blocks.ICE.defaultBlockState(), false);
        world.setBlock(util.grid().at(3,4,1), Blocks.AIR.defaultBlockState(), false);
        world.setBlock(util.grid().at(3,3,1), Blocks.WHITE_WOOL.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(7,3,5), Blocks.ICE.defaultBlockState(), false);
        world.setBlock(util.grid().at(2,4,1), Blocks.AIR.defaultBlockState(), false);
        world.setBlock(util.grid().at(2,3,1), Blocks.WHITE_WOOL.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(7,3,4), Blocks.ICE.defaultBlockState(), false);
        world.setBlock(util.grid().at(1,4,1), Blocks.AIR.defaultBlockState(), false);
        world.setBlock(util.grid().at(1,3,1), Blocks.WHITE_WOOL.defaultBlockState(), false);

        scene.idle(75);
        world.setBlock(util.grid().at(7,3,7), Blocks.WATER.defaultBlockState(), false);
        world.setBlock(util.grid().at(4,3,1), Blocks.LIME_WOOL.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(7,3,6), Blocks.WATER.defaultBlockState(), false);
        world.setBlock(util.grid().at(3,3,1), Blocks.LIME_WOOL.defaultBlockState(), false);

        scene.idle(5);
        world.setBlock(util.grid().at(7,3,5), Blocks.WATER.defaultBlockState(), false);
        world.setBlock(util.grid().at(2,3,1), Blocks.LIME_WOOL.defaultBlockState(), false);
    }
}
