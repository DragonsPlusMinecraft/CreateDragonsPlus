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

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.fluids.potion.PotionMixingRecipes;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
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
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.config.CDPConfig;

@Mixin(MechanicalMixerBlockEntity.class)
public abstract class MechanicalMixerBlockEntityMixin extends BasinOperatingBlockEntity {
    public MechanicalMixerBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Inject(method = "getMatchingRecipes", at = @At(value = "FIELD", ordinal = 0, target = "Lcom/simibubi/create/content/kinetics/mixer/MechanicalMixerBlockEntity;level:Lnet/minecraft/world/level/Level;"))
    private void getMatchingRecipes$checkDragonBreathFluid(CallbackInfoReturnable<List<Recipe<?>>> cir, @Local BasinBlockEntity basin, @Local List<Recipe<?>> matchingRecipes) {
        assert level != null;
        if (CDPConfig.features().generateAutomaticBrewingRecipeForDragonBreathFluid.get()) {
            var tanks = level.getCapability(FluidHandler.BLOCK, basin.getBlockPos(), null);
            if (tanks == null)
                return;
            for (int i = 0; i < tanks.getTanks(); i++) {
                var fluid = tanks.getFluidInTank(i);
                if (fluid.is(CDPFluids.COMMON_TAGS.dragonBreath)) {
                    var recipes = PotionMixingRecipes.sortRecipesByItem(level).get(Items.DRAGON_BREATH);
                    if (recipes == null)
                        return;
                    for (var recipe : recipes) {
                        if (matchBasinRecipe(recipe))
                            matchingRecipes.add(recipe);
                    }
                    break;
                }
            }
        }
    }
}
