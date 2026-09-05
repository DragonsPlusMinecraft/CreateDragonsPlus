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

package plus.dragons.createdragonsplus.integration.aether.integration.jei;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.integration.aether.config.CDPAetherConfig;
import plus.dragons.createdragonsplus.integration.aether.integration.jei.category.AetherFanEnchantingCategory;
import plus.dragons.createdragonsplus.integration.aether.integration.jei.category.AetherFanIncubationCategory;

@JeiPlugin
public class CDPAetherJeiPlugin implements IModPlugin {
    public static final ResourceLocation ID = CDPCommon.asResource("aether_jei_plugin");
    private final List<CreateRecipeCategory<?>> categories = new ArrayList<>();

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        categories.clear();
        if (!ModIntegration.AETHER.enabled() || !CDPAetherConfig.bulkEnchantingEnabled())
            return;
        categories.add(AetherFanEnchantingCategory.create());
        if (CDPAetherConfig.bulkMoaIncubationEnabled())
            categories.add(AetherFanIncubationCategory.create());
        registration.addRecipeCategories(categories.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        categories.forEach(category -> category.registerRecipes(registration));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        categories.forEach(category -> category.registerCatalysts(registration));
    }
}
