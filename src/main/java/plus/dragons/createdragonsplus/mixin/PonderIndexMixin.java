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

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import plus.dragons.createdragonsplus.client.ponder.PonderPluginSorting;

@Mixin(PonderIndex.class)
public class PonderIndexMixin {
    @ModifyReceiver(method = "forEachPlugin", at = @At(value = "INVOKE", target = "Ljava/util/Set;forEach(Ljava/util/function/Consumer;)V"))
    private static Set<PonderPlugin> forEachPlugin$sorted(Set<PonderPlugin> plugins, Consumer<PonderPlugin> consumer) {
        return plugins.stream().sorted(PonderPluginSorting::comparePlugins).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @ModifyReturnValue(method = "streamPlugins", at = @At("RETURN"))
    private static Stream<PonderPlugin> streamPlugins$sorted(Stream<PonderPlugin> plugins) {
        return plugins.sorted(PonderPluginSorting::comparePlugins);
    }
}
