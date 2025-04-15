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

package plus.dragons.createdragonsplus.common.registrate.builder;

import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class ArmInteractionPointBuilder<T extends ArmInteractionPointType, P> extends AbstractBuilder<ArmInteractionPointType, T, P, ArmInteractionPointBuilder<T, P>> {
    private final Supplier<T> factory;

    public ArmInteractionPointBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, Supplier<T> factory) {
        super(owner, parent, name, callback, CreateRegistries.ARM_INTERACTION_POINT_TYPE);
        this.factory = factory;
    }

    @Override
    protected @NotNull T createEntry() {
        return factory.get();
    }
}
