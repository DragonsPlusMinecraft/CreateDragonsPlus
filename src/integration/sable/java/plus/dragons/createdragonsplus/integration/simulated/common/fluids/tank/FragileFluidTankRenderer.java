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

package plus.dragons.createdragonsplus.integration.simulated.common.fluids.tank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.neoforged.neoforge.fluids.FluidStack;

public class FragileFluidTankRenderer extends SmartBlockEntityRenderer<FragileFluidTankBlockEntity> {
    public FragileFluidTankRenderer(Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(FragileFluidTankBlockEntity tank, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(tank, partialTicks, poseStack, buffer, light, overlay);
        TankSegment sg = tank.tank.getPrimaryTank();
        FluidStack fluidStack = sg.getRenderedFluid();
        float fluidLevel = sg.getFluidLevel().getValue(partialTicks);
        if (!fluidStack.isEmpty() && fluidLevel != 0) {
            boolean top = fluidStack.getFluid().getFluidType().isLighterThanAir();
            fluidLevel = Math.max(fluidLevel, 0.175f) * (11 / 16f);
            float min = 2.5f / 16f;
            float max = min + (11 / 16f);
            float minY = top ? (max - fluidLevel) : min;
            float maxY = top ? max : (min + fluidLevel);
            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack,
                    min, minY, min,
                    max, maxY, max,
                    buffer, poseStack, light,
                    false, true);
        }
    }
}
