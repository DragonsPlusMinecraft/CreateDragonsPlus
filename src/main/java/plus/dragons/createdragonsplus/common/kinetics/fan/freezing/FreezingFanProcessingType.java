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

package plus.dragons.createdragonsplus.common.kinetics.fan.freezing;

import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import java.util.List;
import java.util.Optional;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.processing.freeze.BlockFreezer;
import plus.dragons.createdragonsplus.common.processing.freeze.FreezeCondition;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.integration.ModIntegration;

public class FreezingFanProcessingType implements FanProcessingType {
    private final DeferredHolder<FanProcessingType, FanProcessingType> createGarnishedType;
    private final DeferredHolder<RecipeType<?>, RecipeType<ProcessingRecipe<SingleRecipeInput>>> createGarnishedRecipe;

    public FreezingFanProcessingType() {
        this.createGarnishedType = DeferredHolder.create(
                CreateRegistries.FAN_PROCESSING_TYPE,
                ModIntegration.CREATE_GARNISHED.asResource("freezing"));
        this.createGarnishedRecipe = DeferredHolder.create(Registries.RECIPE_TYPE, ModIntegration.CREATE_GARNISHED.asResource("freezing"));
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        if (!CDPConfig.recipes().enableBulkFreezing.get())
            return false;
        var state = level.getBlockState(pos);
        float freeze = BlockFreezer.findFreeze(level, pos, state);
        if (freeze >= 0)
            return true;
        return createGarnishedType.isBound() && createGarnishedType.get().isValidAt(level, pos);
    }

    @Override
    public int getPriority() {
        return 600; // Should be greater than Bulk Coloring
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        var recipe = level.getRecipeManager()
                .getRecipeFor(CDPRecipes.FREEZING.getType(), new SingleRecipeInput(stack), level);
        if (recipe.isPresent())
            return true;
        return canProcessByCreateGarnished(stack, level);
    }

    @Override
    public @Nullable List<ItemStack> process(ItemStack stack, Level level) {
        return level.getRecipeManager()
                .getRecipeFor(CDPRecipes.FREEZING.getType(), new SingleRecipeInput(stack), level)
                .map(recipe -> RecipeApplier.applyRecipeOn(level, stack, recipe))
                .or(() -> processByCreateGarnished(stack, level))
                .orElse(null);
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) == 0) {
            level.addParticle(
                    ParticleTypes.SNOWFLAKE,
                    pos.x + (level.random.nextFloat() - .5f) * .5f,
                    pos.y + .5f,
                    pos.z + (level.random.nextFloat() - .5f) * .5f,
                    0, 1 / 8f, 0);
        }
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        int color = Color.mixColors(FreezeCondition.PASSIVE.getColor(), FreezeCondition.FROZEN.getColor(), random.nextFloat());
        particleAccess.setColor(color);
        particleAccess.setAlpha(1f);
        if (random.nextInt(32) == 0)
            particleAccess.spawnExtraParticle(ParticleTypes.SNOWFLAKE, 1 / 8f);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide)
            return;
        if (entity.canFreeze())
            entity.setTicksFrozen(Math.min(entity.getTicksRequiredToFreeze(), entity.getTicksFrozen()) + 3);
        entity.extinguishFire();
    }

    private boolean canProcessByCreateGarnished(ItemStack stack, Level level) {
        if (!createGarnishedRecipe.isBound())
            return false;
        return level.getRecipeManager()
                .getRecipeFor(createGarnishedRecipe.get(), new SingleRecipeInput(stack), level)
                .isPresent();
    }

    private Optional<List<ItemStack>> processByCreateGarnished(ItemStack stack, Level level) {
        if (!createGarnishedRecipe.isBound())
            return Optional.empty();
        return level.getRecipeManager()
                .getRecipeFor(createGarnishedRecipe.get(), new SingleRecipeInput(stack), level)
                .map(recipe -> RecipeApplier.applyRecipeOn(level, stack, recipe));
    }
}
