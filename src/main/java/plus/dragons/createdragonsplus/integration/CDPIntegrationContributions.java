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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import plus.dragons.createdragonsplus.common.fluids.dye.RegisterDyeVariantsEvent;

public class CDPIntegrationContributions {
    private static final List<Consumer<RegisterDyeVariantsEvent>> DYE_VARIANTS = new CopyOnWriteArrayList<>();

    public static void registerDyeVariants(Consumer<RegisterDyeVariantsEvent> consumer) {
        DYE_VARIANTS.add(consumer);
    }

    public static void gatherDyeVariants(RegisterDyeVariantsEvent event) {
        DYE_VARIANTS.forEach(consumer -> consumer.accept(event));
    }
}
