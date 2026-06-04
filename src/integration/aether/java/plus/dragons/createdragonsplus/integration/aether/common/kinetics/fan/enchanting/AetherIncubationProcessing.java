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

package plus.dragons.createdragonsplus.integration.aether.common.kinetics.fan.enchanting;

import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import plus.dragons.createdragonsplus.integration.aether.config.CDPAetherConfig;

public class AetherIncubationProcessing {
    public static boolean canProcess(ItemStack stack, Level level) {
        return CDPAetherConfig.bulkMoaIncubationEnabled() && findRecipe(stack, level).isPresent();
    }

    public static boolean tryIncubate(ItemStack stack, Level level, Vec3 pos) {
        if (!CDPAetherConfig.bulkEnchantingEnabled() || !CDPAetherConfig.bulkMoaIncubationEnabled())
            return false;
        if (!(level instanceof ServerLevel serverLevel))
            return false;
        var recipe = findRecipe(stack, level);
        if (recipe.isEmpty())
            return false;
        var count = stack.getCount();
        for (int i = 0; i < count; i++) {
            spawn(recipe.get().value(), stack, serverLevel, pos.add(
                    (serverLevel.random.nextDouble() - .5) * .35,
                    0,
                    (serverLevel.random.nextDouble() - .5) * .35));
        }
        return true;
    }

    private static void spawn(IncubationRecipe recipe, ItemStack stack, ServerLevel level, Vec3 pos) {
        var tag = recipe.getTag().orElse(null);
        var customName = stack.has(DataComponents.CUSTOM_NAME) ? stack.getHoverName() : null;
        var entity = recipe.getEntity().spawn(level, EntityType.appendDefaultStackConfig(spawned -> {
            if (tag != null && spawned instanceof LivingEntity living)
                living.readAdditionalSaveData(tag);
        }, level, stack, null), BlockPos.containing(pos), MobSpawnType.TRIGGERED, true, false);
        if (entity != null) {
            entity.setPos(pos);
            entity.setCustomName(customName);
        }
    }

    public static Optional<RecipeHolder<IncubationRecipe>> findRecipe(ItemStack stack, Level level) {
        return level.getRecipeManager()
                .getRecipeFor(AetherRecipeTypes.INCUBATION.get(), new SingleRecipeInput(stack), level);
    }
}
