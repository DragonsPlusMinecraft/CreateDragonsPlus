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

package plus.dragons.createdragonsplus.config;

import net.createmod.catnip.config.ConfigBase;

public class CDPDyeFluidConfig extends ConfigBase {
    public final ConfigBool dyeFluidBlockContactColorsItems = b(true,
            "dyeFluidBlockContactColorsItems",
            Comments.dyeFluidBlockContactColorsItems);
    public final ConfigBool dyeFluidBlockContactColorsLivingEntities = b(true,
            "dyeFluidBlockContactColorsLivingEntities",
            Comments.dyeFluidBlockContactColorsLivingEntities);
    public final ConfigBool dyeFluidBlockContactConsumesSource = b(false,
            "dyeFluidBlockContactConsumesSource",
            Comments.dyeFluidBlockContactConsumesSource);
    public final ConfigBool dyeFluidOpenPipeColorsItems = b(true,
            "dyeFluidOpenPipeColorsItems",
            Comments.dyeFluidOpenPipeColorsItems);
    public final ConfigBool dyeFluidOpenPipeColorsLivingEntities = b(true,
            "dyeFluidOpenPipeColorsLivingEntities",
            Comments.dyeFluidOpenPipeColorsLivingEntities);

    @Override
    public String getName() {
        return "dyeFluid";
    }

    static class Comments {
        static final String dyeFluidBlockContactColorsItems = "If Dye Fluid source blocks should color Item Entities on contact";
        static final String dyeFluidBlockContactColorsLivingEntities = "If Dye Fluid source blocks should color Living Entities and their equipment on contact";
        static final String dyeFluidBlockContactConsumesSource = "If a Dye Fluid source block should be consumed after it successfully colors an entity";
        static final String dyeFluidOpenPipeColorsItems = "If Dye Fluid from an Open Pipe should color Item Entities";
        static final String dyeFluidOpenPipeColorsLivingEntities = "If Dye Fluid from an Open Pipe should color Living Entities and their equipment";
    }
}
