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

package plus.dragons.createdragonsplus.integration;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.tterrag.registrate.providers.RegistrateDataMapProvider;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariant;
import plus.dragons.createdragonsplus.common.fluids.dye.RegisterDyeVariantsEvent;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.ColoringRecipe;
import plus.dragons.createdragonsplus.common.kinetics.fan.ending.EndingRecipe;
import plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingRecipe;
import plus.dragons.createdragonsplus.common.kinetics.fan.sanding.SandingRecipe;

public class CDPIntegrationContributions {
    private static final List<Consumer<RegisterDyeVariantsEvent>> DYE_VARIANTS = new CopyOnWriteArrayList<>();
    private static final List<NonNullConsumer<RegistrateDataMapProvider>> DATA_MAPS = new CopyOnWriteArrayList<>();
    private static final List<ColoringCompat> COLORING_COMPATS = new CopyOnWriteArrayList<>();
    private static final List<StandardFanProcessingCompat<FreezingRecipe>> FREEZING_COMPATS = new CopyOnWriteArrayList<>();
    private static final List<StandardFanProcessingCompat<SandingRecipe>> SANDING_COMPATS = new CopyOnWriteArrayList<>();
    private static final List<StandardFanProcessingCompat<EndingRecipe>> ENDING_COMPATS = new CopyOnWriteArrayList<>();
    private static final List<TagKey<Block>> SANDING_CATALYST_TAGS = new CopyOnWriteArrayList<>();
    private static final List<Consumer<List<Supplier<? extends ItemStack>>>> FAN_CATALYSTS = new CopyOnWriteArrayList<>();

    public static void registerDyeVariants(Consumer<RegisterDyeVariantsEvent> consumer) {
        DYE_VARIANTS.add(consumer);
    }

    public static void gatherDyeVariants(RegisterDyeVariantsEvent event) {
        DYE_VARIANTS.forEach(consumer -> consumer.accept(event));
    }

    public static void registerDataMaps(NonNullConsumer<RegistrateDataMapProvider> consumer) {
        DATA_MAPS.add(consumer);
    }

    public static void gatherDataMaps(RegistrateDataMapProvider provider) {
        DATA_MAPS.forEach(consumer -> consumer.accept(provider));
    }

    public static void registerColoringCompat(ColoringCompat compat) {
        COLORING_COMPATS.add(compat);
    }

    public static boolean canColorByCompat(DyeVariant variant, ItemStack stack, Level level) {
        for (var compat : COLORING_COMPATS) {
            if (compat.canProcess(variant, stack, level))
                return true;
        }
        return false;
    }

    public static Optional<List<ItemStack>> processColoringByCompat(DyeVariant variant, ItemStack stack, Level level) {
        for (var compat : COLORING_COMPATS) {
            var result = compat.process(variant, stack, level);
            if (result.isPresent())
                return result;
        }
        return Optional.empty();
    }

    public static void gatherColoringJeiRecipes(RecipeManager manager, List<RecipeHolder<ColoringRecipe>> recipes) {
        COLORING_COMPATS.forEach(compat -> compat.gatherJeiRecipes(manager, recipes));
    }

    public static void registerFreezingCompat(StandardFanProcessingCompat<FreezingRecipe> compat) {
        FREEZING_COMPATS.add(compat);
    }

    public static boolean isFreezingCatalyst(Level level, BlockPos pos) {
        return isValidAt(FREEZING_COMPATS, level, pos);
    }

    public static boolean canFreezeByCompat(ItemStack stack, Level level) {
        return canProcess(FREEZING_COMPATS, stack, level);
    }

    public static Optional<List<ItemStack>> processFreezingByCompat(ItemStack stack, Level level) {
        return process(FREEZING_COMPATS, stack, level);
    }

    public static void gatherFreezingJeiRecipes(RecipeManager manager, List<RecipeHolder<FreezingRecipe>> recipes) {
        FREEZING_COMPATS.forEach(compat -> compat.gatherJeiRecipes(manager, recipes));
    }

    public static void registerSandingCompat(StandardFanProcessingCompat<SandingRecipe> compat) {
        SANDING_COMPATS.add(compat);
    }

    public static boolean isSandingCatalyst(Level level, BlockPos pos) {
        return isValidAt(SANDING_COMPATS, level, pos);
    }

    public static boolean canSandByCompat(ItemStack stack, Level level) {
        return canProcess(SANDING_COMPATS, stack, level);
    }

    public static Optional<List<ItemStack>> processSandingByCompat(ItemStack stack, Level level) {
        return process(SANDING_COMPATS, stack, level);
    }

    public static void gatherSandingJeiRecipes(RecipeManager manager, List<RecipeHolder<SandingRecipe>> recipes) {
        SANDING_COMPATS.forEach(compat -> compat.gatherJeiRecipes(manager, recipes));
    }

    public static void registerSandingCatalystTag(TagKey<Block> tag) {
        SANDING_CATALYST_TAGS.add(tag);
    }

    public static List<TagKey<Block>> sandingCatalystTags() {
        return List.copyOf(SANDING_CATALYST_TAGS);
    }

    public static void registerEndingCompat(StandardFanProcessingCompat<EndingRecipe> compat) {
        ENDING_COMPATS.add(compat);
    }

    public static boolean isEndingCatalyst(Level level, BlockPos pos) {
        return isValidAt(ENDING_COMPATS, level, pos);
    }

    public static boolean canEndByCompat(ItemStack stack, Level level) {
        return canProcess(ENDING_COMPATS, stack, level);
    }

    public static Optional<List<ItemStack>> processEndingByCompat(ItemStack stack, Level level) {
        return process(ENDING_COMPATS, stack, level);
    }

    public static void affectEntityByEndingCompat(Entity entity, Level level) {
        ENDING_COMPATS.forEach(compat -> compat.affectEntity(entity, level));
    }

    public static void gatherEndingJeiRecipes(RecipeManager manager, List<RecipeHolder<EndingRecipe>> recipes) {
        ENDING_COMPATS.forEach(compat -> compat.gatherJeiRecipes(manager, recipes));
    }

    public static void registerFanCatalysts(Consumer<List<Supplier<? extends ItemStack>>> consumer) {
        FAN_CATALYSTS.add(consumer);
    }

    public static List<Supplier<? extends ItemStack>> gatherFanCatalysts(ItemStack defaultFan) {
        var catalysts = new ArrayList<Supplier<? extends ItemStack>>();
        catalysts.add(() -> defaultFan);
        FAN_CATALYSTS.forEach(consumer -> consumer.accept(catalysts));
        return List.copyOf(catalysts);
    }

    private static <R extends StandardProcessingRecipe<SingleRecipeInput>> boolean isValidAt(
            List<StandardFanProcessingCompat<R>> compats, Level level, BlockPos pos) {
        for (var compat : compats) {
            if (compat.isValidAt(level, pos))
                return true;
        }
        return false;
    }

    private static <R extends StandardProcessingRecipe<SingleRecipeInput>> boolean canProcess(
            List<StandardFanProcessingCompat<R>> compats, ItemStack stack, Level level) {
        for (var compat : compats) {
            if (compat.canProcess(stack, level))
                return true;
        }
        return false;
    }

    private static <R extends StandardProcessingRecipe<SingleRecipeInput>> Optional<List<ItemStack>> process(
            List<StandardFanProcessingCompat<R>> compats, ItemStack stack, Level level) {
        for (var compat : compats) {
            var result = compat.process(stack, level);
            if (result.isPresent())
                return result;
        }
        return Optional.empty();
    }

    public interface ColoringCompat {
        boolean canProcess(DyeVariant variant, ItemStack stack, Level level);

        Optional<List<ItemStack>> process(DyeVariant variant, ItemStack stack, Level level);

        void gatherJeiRecipes(RecipeManager manager, List<RecipeHolder<ColoringRecipe>> recipes);
    }

    public interface StandardFanProcessingCompat<R extends StandardProcessingRecipe<SingleRecipeInput>> {
        boolean isValidAt(Level level, BlockPos pos);

        boolean canProcess(ItemStack stack, Level level);

        Optional<List<ItemStack>> process(ItemStack stack, Level level);

        void gatherJeiRecipes(RecipeManager manager, List<RecipeHolder<R>> recipes);

        default void affectEntity(Entity entity, Level level) {}
    }
}
