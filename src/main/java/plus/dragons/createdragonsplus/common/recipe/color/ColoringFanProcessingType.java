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

package plus.dragons.createdragonsplus.common.recipe.color;

import static plus.dragons.createdragonsplus.common.CDPCommon.PERSISTENT_DATA_KEY;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import java.util.Optional;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import plus.dragons.createdragonsplus.common.registry.CDPDataMaps;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.util.PersistentDataHelper;

public class ColoringFanProcessingType implements FanProcessingType {
    private final DyeColor color;
    private final Vector3f rgb;
    private final DeferredHolder<RecipeType<?>, RecipeType<ProcessingRecipe<SingleRecipeInput>>> garnishedRecipe;

    public ColoringFanProcessingType(DyeColor color) {
        this.color = color;
        this.rgb = new Color(this.color.getTextureDiffuseColor()).asVectorF();
        this.garnishedRecipe = DeferredHolder.create(Registries.RECIPE_TYPE,
                ModIntegration.GARNISHED.asResource(color.getSerializedName() + "_dye_blowing"));
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        if (!CDPConfig.recipes().enableBulkColoring.get())
            return false;
        if (level.getFluidState(pos).holder().getData(CDPDataMaps.FLUID_FAN_COLORING_CATALYSTS) == this.color)
            return true;
        return level.getBlockState(pos).getBlockHolder().getData(CDPDataMaps.BLOCK_FAN_COLORING_CATALYSTS) == this.color;
    }

    @Override
    public int getPriority() {
        return 500; // Should be greater than splashing (400)
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        var recipeManager = level.getRecipeManager();
        Optional<? extends RecipeHolder<?>> recipe;
        recipe = recipeManager.getRecipeFor(
                CDPRecipes.COLORING.getType(),
                new ColoringRecipeInput(this.color, stack),
                level);
        if (recipe.isEmpty() && garnishedRecipe.isBound())
            recipe = recipeManager.getRecipeFor(
                    garnishedRecipe.get(),
                    new SingleRecipeInput(stack),
                    level);
        if (recipe.isPresent())
            return true;
        return this.processCrafting(stack, level).isPresent();
    }

    @Override
    public @Nullable List<ItemStack> process(ItemStack stack, Level level) {
        var recipeManager = level.getRecipeManager();
        Optional<? extends RecipeHolder<?>> recipe;
        recipe = recipeManager.getRecipeFor(
                CDPRecipes.COLORING.getType(),
                new ColoringRecipeInput(this.color, stack),
                level);
        if (recipe.isEmpty() && garnishedRecipe.isBound())
            recipe = recipeManager.getRecipeFor(
                    garnishedRecipe.get(),
                    new SingleRecipeInput(stack),
                    level);
        if (recipe.isPresent())
            return RecipeApplier.applyRecipeOn(level, stack, recipe.get());
        return this.processCrafting(stack, level)
                .map(result -> ItemHelper.multipliedOutput(stack, result))
                .orElse(null);
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) != 0)
            return;
        level.addParticle(new DustParticleOptions(this.rgb, 1),
                pos.x + (level.random.nextFloat() - .5f) * .5f,
                pos.y + .5f,
                pos.z + (level.random.nextFloat() - .5f) * .5f,
                0, 1 / 8f, 0);
        if (level.random.nextInt(2) == 0)
            return;
        level.addParticle(new DustParticleOptions(this.rgb, 2),
                pos.x + (level.random.nextFloat() - .5f) * .5f,
                pos.y + .5f,
                pos.z + (level.random.nextFloat() - .5f) * .5f,
                0, 1 / 8f, 0);
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        particleAccess.setColor(this.color.getTextureDiffuseColor());
        particleAccess.setAlpha(1f);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide)
            return;
        if (entity instanceof LivingEntity livingEntity)
            this.applyColoring(livingEntity, level);
        if (entity instanceof EnderMan || entity.getType() == EntityType.SNOW_GOLEM
                || entity.getType() == EntityType.BLAZE) {
            entity.hurt(entity.damageSources().drown(), 2);
        }
        if (entity.isOnFire()) {
            entity.clearFire();
            level.playSound(null, entity.blockPosition(), SoundEvents.GENERIC_EXTINGUISH_FIRE,
                    SoundSource.NEUTRAL, 0.7F, 1.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
        }
    }

    private Optional<ItemStack> processCrafting(ItemStack stack, Level level) {
        // 1 Dye + 1 Colorless = 1 Dyed
        var input = CraftingInput.of(2, 1, List.of(stack, new ItemStack(DyeItem.byColor(this.color))));
        var optional = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level);
        if (optional.isPresent()) {
            var recipe = optional.get().value();
            var result = recipe.assemble(input, level.registryAccess());
            // Not a coloring recipe if result count is not 1
            return result.getCount() == 1 ? Optional.of(result) : Optional.empty();
        }
        // 1 Dye + 8 Colorless = 8 Dyed
        var items = NonNullList.withSize(9, stack);
        items.set(4, new ItemStack(DyeItem.byColor(this.color)));
        input = CraftingInput.of(3, 3, items);
        optional = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level);
        if (optional.isPresent()) {
            var recipe = optional.get().value();
            var result = recipe.assemble(input, level.registryAccess());
            // Not a coloring recipe if result count is not 8
            if (result.getCount() != 8)
                return Optional.empty();
            result.setCount(1);
            return Optional.of(result);
        }
        return Optional.empty();
    }

    public void applyColoring(LivingEntity entity, Level level) {
        if (processColoring(entity)) {
            switch (entity) {
                case Sheep sheep -> sheep.setColor(this.color);
                case Shulker shulker -> shulker.setVariant(Optional.of(this.color));
                case Cat cat -> cat.setCollarColor(this.color);
                case Wolf wolf -> wolf.setCollarColor(this.color);
                default -> {}
            }
            for (var slot : EquipmentSlot.values()) {
                ItemStack stack = entity.getItemBySlot(slot);
                if (stack.isEmpty())
                    continue;
                this.applyColoring(stack, level).ifPresent(it -> {
                    it.setCount(stack.getCount());
                    entity.setItemSlot(slot, it);
                });
            }
        }
    }

    private boolean processColoring(LivingEntity entity) {
        CompoundTag nbt = PersistentDataHelper.getOrCreate(entity.getPersistentData(), PERSISTENT_DATA_KEY, "Coloring");
        int sinceLastProcess = 0;
        if (!(nbt.contains("Color", Tag.TAG_STRING) && nbt.getString("Color").equals(this.color.getName()))) {
            nbt.putString("Color", this.color.getName());
            nbt.remove("Time");
        } else if (nbt.contains("LastProcess", Tag.TAG_INT)) {
            int lastProcess = nbt.getInt("LastProcess");
            sinceLastProcess = entity.tickCount - lastProcess - 1;
        }
        nbt.putInt("LastProcess", entity.tickCount);
        int processingTime = AllConfigs.server().kinetics.fanProcessingTime.get();
        if (!nbt.contains("Time", Tag.TAG_INT) || sinceLastProcess < 0) {
            nbt.putInt("Time", processingTime);
            return false;
        }
        int time = nbt.getInt("Time") + sinceLastProcess;
        if (time == 0) {
            nbt.remove("Color");
            nbt.remove("LastProcess");
            nbt.remove("Time");
            return true;
        }
        nbt.putInt("Time", Math.min(processingTime, time - 1));
        return false;
    }

    private Optional<ItemStack> applyColoring(ItemStack stack, Level level) {
        var coloringInput = new ColoringRecipeInput(this.color, stack);
        var coloringRecipe = level.getRecipeManager().getRecipeFor(CDPRecipes.COLORING.getType(), coloringInput, level);
        if (coloringRecipe.isPresent()) {
            ItemStack result = coloringRecipe.get().value().assemble(coloringInput, level.registryAccess());
            return Optional.of(result);
        }
        return this.processCrafting(stack, level);
    }
}
