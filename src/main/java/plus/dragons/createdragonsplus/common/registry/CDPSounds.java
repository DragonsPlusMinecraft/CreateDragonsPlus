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

package plus.dragons.createdragonsplus.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import plus.dragons.createdragonsplus.common.CDPCommon;

public class CDPSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister
            .create(Registries.SOUND_EVENT, CDPCommon.ID);
    public static final DeferredHolder<SoundEvent, SoundEvent> BUCKET_EMPTY_DYE = register("item.bucket.empty_dye");
    public static final DeferredHolder<SoundEvent, SoundEvent> BUCKET_FILL_DYE = register("item.bucket.fill_dye");

    public static void register(IEventBus modBus) {
        SOUNDS.register(modBus);
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUNDS.register(name, SoundEvent::createVariableRangeEvent);
    }

    public static class DefinitionsProvider extends SoundDefinitionsProvider {
        public DefinitionsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, CDPCommon.ID, existingFileHelper);
        }

        @Override
        public void registerSounds() {
            this.add(BUCKET_EMPTY_DYE.getId(), definition().subtitle("subtitles.item.bucket.empty").with(
                    sound("item/bucket/empty1"),
                    sound("item/bucket/empty1").pitch(0.9),
                    sound("item/bucket/empty2"),
                    sound("item/bucket/empty3")));
            this.add(BUCKET_FILL_DYE.getId(), definition().subtitle("subtitles.item.bucket.fill").with(
                    sound("item/bucket/fill1"),
                    sound("item/bucket/fill2"),
                    sound("item/bucket/fill3")));
        }
    }
}
