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

package plus.dragons.createdragonsplus.common.kinetics.fan.coloring;

import static plus.dragons.createdragonsplus.common.CDPCommon.PERSISTENT_DATA_KEY;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariant;
import plus.dragons.createdragonsplus.common.registry.CDPDataMaps;
import plus.dragons.createdragonsplus.common.registry.CDPItems;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.integration.CDPIntegrationContributions;
import plus.dragons.createdragonsplus.util.ItemStackKey;
import plus.dragons.createdragonsplus.util.PersistentDataHelper;

public class ColoringFanProcessingType implements FanProcessingType {
    private final DyeVariant variant;
    private final Vector3f rgb;
    private final Map<ItemStackKey, Boolean> canProcessCache = new ConcurrentHashMap<>();
    private final Map<ItemStackKey, ItemStack> craftingResultCache = new ConcurrentHashMap<>();
    private static final ResourceLocation SUPPLEMENTARIES_SUS_CRAFTING = ResourceLocation
            .fromNamespaceAndPath("supplementaries", "sus_crafting");

    public ColoringFanProcessingType(DyeVariant variant) {
        this.variant = variant;
        this.rgb = new Color(this.variant.color()).asVectorF();
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        if (!CDPConfig.recipes().enableBulkColoring.get())
            return false;
        if (this.variant.id().equals(level.getFluidState(pos).holder().getData(CDPDataMaps.FLUID_FAN_COLORING_CATALYSTS)))
            return true;
        return this.variant.id().equals(level.getBlockState(pos).getBlockHolder().getData(CDPDataMaps.BLOCK_FAN_COLORING_CATALYSTS));
    }

    public void recreateCache() {
        canProcessCache.clear();
        craftingResultCache.clear();
    }

    @Override
    public int getPriority() {
        return 500; // Should be greater than splashing (400)
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        if (!CDPConfig.recipes().enableBulkColoring.get())
            return false;
        return canProcessCache.computeIfAbsent(ItemStackKey.of(stack), key -> canProcessUncached(stack, level));
    }

    private boolean canProcessUncached(ItemStack stack, Level level) {
        var recipe = level.getRecipeManager()
                .getRecipeFor(CDPRecipes.COLORING.getType(), new ColoringRecipeInput(this.variant.id(), stack), level);
        if (recipe.isPresent())
            return true;
        if (CDPIntegrationContributions.canColorByCompat(this.variant, stack, level))
            return true;
        return this.processByCrafting(stack, level).isPresent();
    }

    @Override
    public @Nullable List<ItemStack> process(ItemStack stack, Level level) {
        return level.getRecipeManager()
                .getRecipeFor(CDPRecipes.COLORING.getType(), new ColoringRecipeInput(this.variant.id(), stack), level)
                .map(recipe -> RecipeApplier.applyRecipeOn(level, stack, recipe.value(), false))
                .or(() -> CDPIntegrationContributions.processColoringByCompat(this.variant, stack, level))
                .or(() -> processByCrafting(stack, level)
                        .map(result -> ItemHelper.multipliedOutput(stack, result)))
                .orElse(null);
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) == 0) {
            level.addParticle(new DustParticleOptions(this.rgb, 2),
                    pos.x + (level.random.nextFloat() - .5f) * .5f,
                    pos.y + .5f,
                    pos.z + (level.random.nextFloat() - .5f) * .5f,
                    0, 1 / 8f, 0);
        }
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        particleAccess.setColor(this.variant.color());
        particleAccess.setAlpha(1f);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide)
            return;
        if (entity instanceof LivingEntity livingEntity)
            this.applyColoring(livingEntity, level);
        if (entity instanceof EnderMan || entity.getType() == EntityType.SNOW_GOLEM || entity.getType() == EntityType.BLAZE) {
            entity.hurt(entity.damageSources().drown(), 2);
        }
        if (entity.isOnFire()) {
            entity.clearFire();
            level.playSound(null, entity.blockPosition(), SoundEvents.GENERIC_EXTINGUISH_FIRE,
                    SoundSource.NEUTRAL, 0.7F, 1.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
        }
    }

    private Optional<ItemStack> processByCrafting(ItemStack stack, Level level) {
        if (stack.is(CDPItems.MOD_TAGS.notApplicableColoring))
            return Optional.empty();

        var key = ItemStackKey.of(stack);
        var cached = craftingResultCache.get(key);
        if (cached != null)
            return cached.isEmpty() ? Optional.empty() : Optional.of(cached.copy());

        var result = processByCraftingUncached(stack, level);
        craftingResultCache.put(key, result.map(ItemStack::copy).orElse(ItemStack.EMPTY));
        return result;
    }

    private Optional<ItemStack> processByCraftingUncached(ItemStack stack, Level level) {
        // 1 Dye + 1 Colorless = 1 Dyed
        var dye = this.variant.dyeItemStack();
        if (dye.isEmpty())
            return Optional.empty();
        var input = CraftingInput.of(2, 1, List.of(stack, dye));
        var result = findAutomaticColoringCraftingResult(input, level, 1);
        if (result.isPresent())
            return result;
        // 1 Dye + 8 Colorless = 8 Dyed
        var items = NonNullList.withSize(9, stack);
        items.set(4, dye);
        input = CraftingInput.of(3, 3, items);
        result = findAutomaticColoringCraftingResult(input, level, 8);
        if (result.isPresent()) {
            var craftingResult = result.get();
            craftingResult.setCount(1);
            return Optional.of(craftingResult);
        }
        return Optional.empty();
    }

    private static Optional<ItemStack> findAutomaticColoringCraftingResult(CraftingInput input, Level level, int resultCount) {
        for (var holder : level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
            var recipe = holder.value();
            if (isIgnoredAutomaticColoringRecipe(recipe) || !recipe.matches(input, level))
                continue;
            var result = recipe.assemble(input, level.registryAccess());
            if (result.getCount() == resultCount)
                return Optional.of(result);
        }
        return Optional.empty();
    }

    private static boolean isIgnoredAutomaticColoringRecipe(CraftingRecipe recipe) {
        var serializerId = BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipe.getSerializer());
        return SUPPLEMENTARIES_SUS_CRAFTING.equals(serializerId);
    }

    public void applyColoring(LivingEntity entity, Level level) {
        if (processColoring(entity)) {
            var vanillaColor = this.variant.vanillaColor();
            if (vanillaColor != null) {
                switch (entity) {
                    case Sheep sheep -> sheep.setColor(vanillaColor);
                    case Shulker shulker -> shulker.setVariant(Optional.of(vanillaColor));
                    case Cat cat -> cat.setCollarColor(vanillaColor);
                    case Wolf wolf -> wolf.setCollarColor(vanillaColor);
                    default -> {}
                }
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
        if (!(nbt.contains("Color", Tag.TAG_STRING) && nbt.getString("Color").equals(this.variant.id().toString()))) {
            nbt.putString("Color", this.variant.id().toString());
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
        var coloringInput = new ColoringRecipeInput(this.variant.id(), stack);
        var coloringRecipe = level.getRecipeManager().getRecipeFor(CDPRecipes.COLORING.getType(), coloringInput, level);
        if (coloringRecipe.isPresent()) {
            ItemStack result = coloringRecipe.get().value().assemble(coloringInput, level.registryAccess());
            return Optional.of(result);
        }
        return this.processByCrafting(stack, level);
    }
}
