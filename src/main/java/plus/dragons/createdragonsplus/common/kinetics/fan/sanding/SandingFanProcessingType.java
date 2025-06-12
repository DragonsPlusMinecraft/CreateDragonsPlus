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

package plus.dragons.createdragonsplus.common.kinetics.fan.sanding;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.kinetics.fan.DynamicParticleFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.sanding.SandingFanProcessingType.ParticleData;
import plus.dragons.createdragonsplus.common.registry.CDPBlocks;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.config.CDPConfig;

public class SandingFanProcessingType implements DynamicParticleFanProcessingType<ParticleData> {
    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        if (!CDPConfig.recipes().enableBulkSanding.get())
            return false;
        var state = level.getBlockState(pos);
        return state.is(CDPBlocks.MOD_TAGS.fanSandingCatalysts);
    }

    @Override
    public int getPriority() {
        return 700; // Should be greater than Bulk Freezing
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        var recipeManager = level.getRecipeManager();
        var input = new SingleRecipeInput(stack);
        return recipeManager
                .getRecipeFor((RecipeType<? extends StandardProcessingRecipe<SingleRecipeInput>>) CDPRecipes.SANDING.getType(), input, level)
                .or(() -> recipeManager.getRecipeFor(AllRecipeTypes.SANDPAPER_POLISHING.getType(), input, level))
                .filter(AllRecipeTypes.CAN_BE_AUTOMATED)
                .isPresent();
    }

    @Override
    public @Nullable List<ItemStack> process(ItemStack stack, Level level) {
        var recipeManager = level.getRecipeManager();
        var input = new SingleRecipeInput(stack);
        return recipeManager
                .getRecipeFor((RecipeType<? extends StandardProcessingRecipe<SingleRecipeInput>>) CDPRecipes.SANDING.getType(), input, level)
                .or(() -> recipeManager.getRecipeFor(AllRecipeTypes.SANDPAPER_POLISHING.getType(), input, level))
                .filter(AllRecipeTypes.CAN_BE_AUTOMATED)
                .map(recipe -> RecipeApplier.applyRecipeOn(level, stack, recipe))
                .orElse(null);
    }

    @Override
    public @Nullable ParticleData getParticleDataAt(Level level, BlockPos pos) {
        var state = level.getBlockState(pos);
        int color = 0xDBD3A0;
        if (state.getBlock() instanceof FallingBlock falling)
            color = falling.getDustColor(state, level, pos);
        return new ParticleData(state, color);
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos, @Nullable ParticleData data) {
        if (level.random.nextInt(8) == 0) {
            var state = data == null ? Blocks.SAND.defaultBlockState() : data.state;
            level.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, state),
                    pos.x + (level.random.nextFloat() - .5f) * .5f,
                    pos.y + .5f,
                    pos.z + (level.random.nextFloat() - .5f) * .5f,
                    0, 0, 0);
        }
        if (data != null)
            data.playSound(level, pos);
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random, @Nullable ParticleData data) {
        int color = data == null ? 0xDBD3A0 : data.color;
        var state = data == null ? Blocks.SAND.defaultBlockState() : data.state;
        particleAccess.setColor(color);
        particleAccess.setAlpha(1f);
        if (random.nextInt(32) == 0)
            particleAccess.spawnExtraParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, state), 0);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide)
            return;
        entity.extinguishFire();
    }

    public static class ParticleData {
        private final BlockState state;
        private final int color;
        private final Set<BlockPos> playedSoundPos = new ObjectArraySet<>();

        public ParticleData(BlockState state, int color) {
            this.state = state;
            this.color = color;
        }

        public void playSound(Level level, Vec3 pos) {
            if (level.getGameTime() % 7 == 0) {
                // Play sound only once per block pos
                if (playedSoundPos.add(BlockPos.containing(pos))) {
                    AllSoundEvents.SANDING_SHORT.playAt(level, pos,
                            0.3F + 0.1F * level.random.nextFloat(),
                            0.9F + 0.2F * level.random.nextFloat(),
                            true);
                }
            } else if (!playedSoundPos.isEmpty()) {
                playedSoundPos.clear();
            }
        }
    }
}
