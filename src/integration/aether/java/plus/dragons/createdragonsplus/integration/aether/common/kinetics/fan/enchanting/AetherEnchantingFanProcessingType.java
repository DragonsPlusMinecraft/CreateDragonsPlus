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

import com.aetherteam.aether.block.AetherBlocks;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import java.util.List;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.integration.aether.config.CDPAetherConfig;

public class AetherEnchantingFanProcessingType implements FanProcessingType {
    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        return CDPAetherConfig.bulkEnchantingEnabled()
                && level.getBlockState(pos).is(AetherBlocks.GOLDEN_AERCLOUD.get());
    }

    @Override
    public int getPriority() {
        return 575; // Between Bulk Freezing and Create's vanilla fan processors.
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        if (!CDPAetherConfig.bulkEnchantingEnabled())
            return false;
        return AetherAltarRecipeProcessing.canProcess(stack, level)
                || AetherIncubationProcessing.canProcess(stack, level);
    }

    @Override
    public @Nullable List<ItemStack> process(ItemStack stack, Level level) {
        if (!CDPAetherConfig.bulkEnchantingEnabled())
            return null;
        var altarResult = AetherAltarRecipeProcessing.process(stack, level);
        if (altarResult.isPresent())
            return altarResult.get();
        return null;
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) == 0) {
            level.addParticle(
                    ParticleTypes.ENCHANT,
                    pos.x + (level.random.nextFloat() - .5f) * .5f,
                    pos.y + .5f,
                    pos.z + (level.random.nextFloat() - .5f) * .5f,
                    0, 1 / 8f, 0);
        }
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        particleAccess.setColor(Color.mixColors(0x7A3FD1, 0xF0C766, random.nextFloat()));
        particleAccess.setAlpha(1f);
        if (random.nextInt(32) == 0)
            particleAccess.spawnExtraParticle(ParticleTypes.ENCHANT, 1 / 8f);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {}
}
