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

package plus.dragons.createdragonsplus.mixin.create;

import com.simibubi.create.content.fluids.potion.PotionMixingRecipes;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import java.util.HashSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeVariantRegistry;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.DyeFluidMixingRecipes;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.util.ItemStackKey;

@Mixin(MechanicalMixerBlockEntity.class)
public abstract class MechanicalMixerBlockEntityMixin extends BasinOperatingBlockEntity {
    public MechanicalMixerBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Inject(method = "getMatchingRecipes", at = @At("TAIL"))
    private void getMatchingRecipes$checkDragonBreathFluid(CallbackInfoReturnable<List<Recipe<?>>> cir) {
        assert level != null;
        if (CDPConfig.features().generateAutomaticBrewingRecipeForDragonBreathFluid.get()) {
            var basin = getBasin();
            if (basin.isEmpty())
                return;
            var tanks = level.getCapability(FluidHandler.BLOCK, basin.get().getBlockPos(), null);
            if (tanks == null)
                return;
            for (int i = 0; i < tanks.getTanks(); i++) {
                var fluid = tanks.getFluidInTank(i);
                if (fluid.is(CDPFluids.COMMON_TAGS.dragonBreath)) {
                    var recipes = PotionMixingRecipes.sortRecipesByItem(level).get(Items.DRAGON_BREATH);
                    if (recipes == null)
                        return;
                    var matchingRecipes = cir.getReturnValue();
                    for (var recipe : recipes) {
                        if (matchBasinRecipe(recipe))
                            matchingRecipes.add(recipe);
                    }
                    break;
                }
            }
        }
    }

    @Inject(method = "getMatchingRecipes", at = @At("TAIL"))
    private void getMatchingRecipes$checkDyeFluidColoring(CallbackInfoReturnable<List<Recipe<?>>> cir) {
        assert level != null;
        if (!CDPConfig.features().dyeFluids.get())
            return;
        var optionalBasin = getBasin();
        if (optionalBasin.isEmpty())
            return;

        var basin = optionalBasin.get();
        var inputItems = basin.getInputInventory();
        var inputFluids = basin.inputTank.getCapability();
        var matchingRecipes = cir.getReturnValue();
        for (var variant : DyeVariantRegistry.all()) {
            var fluidTag = CDPFluids.COMMON_TAGS.dyesByVariant.get(variant.id());
            if (fluidTag == null)
                continue;
            boolean hasFluid = false;
            for (int tank = 0; tank < inputFluids.getTanks(); tank++) {
                if (inputFluids.getFluidInTank(tank).is(fluidTag)) {
                    hasFluid = true;
                    break;
                }
            }
            if (!hasFluid)
                continue;

            var seen = new HashSet<ItemStackKey>();
            for (int slot = 0; slot < inputItems.getSlots(); slot++) {
                var input = inputItems.getStackInSlot(slot);
                if (input.isEmpty() || !seen.add(ItemStackKey.of(input)))
                    continue;
                DyeFluidMixingRecipes.create(variant, input, level).ifPresent(recipe -> {
                    if (matchBasinRecipe(recipe))
                        matchingRecipes.add(recipe);
                });
            }
        }
    }
}
