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

package plus.dragons.createdragonsplus.data.recipe;

import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public class VanillaRecipeBuilders {
    public static ShapedRecipeBuilder shaped() {
        return new ShapedRecipeBuilder("crafting");
    }

    public static ShapelessRecipeBuilder shapeless() {
        return new ShapelessRecipeBuilder("crafting");
    }

    public static SingleItemRecipeBuilder stonecutting() {
        return new SingleItemRecipeBuilder("stonecutting", StonecutterRecipe::new);
    }

    public static CookingRecipeBuilder<SmeltingRecipe> smelting() {
        return new CookingRecipeBuilder<>("smelting", SmeltingRecipe::new, 200);
    }

    public static CookingRecipeBuilder<BlastingRecipe> blasting() {
        return new CookingRecipeBuilder<>("blasting", BlastingRecipe::new, 100);
    }

    public static CookingRecipeBuilder<SmokingRecipe> smoking() {
        return new CookingRecipeBuilder<>("smoking", SmokingRecipe::new, 100);
    }

    public static CookingRecipeBuilder<CampfireCookingRecipe> campfire() {
        return new CookingRecipeBuilder<>("smoking", CampfireCookingRecipe::new, 600);
    }
}
