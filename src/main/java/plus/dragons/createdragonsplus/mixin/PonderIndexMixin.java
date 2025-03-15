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

package plus.dragons.createdragonsplus.mixin;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Debug
@Mixin(PonderIndex.class)
public class PonderIndexMixin {
    @Shadow
    @Final
    @Mutable
    private static Set<PonderPlugin> plugins;

    @Redirect(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/createmod/ponder/foundation/PonderIndex;plugins:Ljava/util/Set;"), require = 0)
    private static void clinit$orderPlugins(Set<PonderPlugin> value) {
        List<String> mods = ModList.get().getSortedMods().stream().map(ModContainer::getModId).toList();
        plugins = new TreeSet<>(Comparator.comparing(PonderPlugin::getModId, Comparator.comparingInt(mods::indexOf)));
    }
}
