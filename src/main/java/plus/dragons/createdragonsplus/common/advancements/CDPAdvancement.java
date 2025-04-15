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

package plus.dragons.createdragonsplus.common.advancements;

import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.RegisterEvent;
import plus.dragons.createdragonsplus.common.advancements.criterion.BuiltinTrigger;
import plus.dragons.createdragonsplus.common.advancements.criterion.StatTrigger;
import plus.dragons.createdragonsplus.util.CodeReference;

@CodeReference(value = CreateAdvancement.class, source = "create", license = "mit")
public abstract class CDPAdvancement {
    static final String SECRET_SUFFIX = "\n\u00A77(Hidden Advancement)";

    private final Advancement.Builder mcBuilder = Advancement.Builder.advancement();
    private BuiltinTrigger builtinTrigger;
    private CDPAdvancement parent;
    private final CDPAdvancement.Builder createBuilder = new CDPAdvancement.Builder();

    AdvancementHolder datagenResult;

    private String id;
    private String title;
    private String description;

    public CDPAdvancement(String id, UnaryOperator<CDPAdvancement.Builder> b) {
        this.id = id;

        b.apply(createBuilder);

        if (!createBuilder.externalTrigger) {
            builtinTrigger = add(asResource(id));
            mcBuilder.addCriterion("0", builtinTrigger.createCriterion(builtinTrigger));
        }

        if (createBuilder.type == CDPAdvancement.TaskType.SECRET)
            description += SECRET_SUFFIX;

        addToAdvancementEntries();
    }

    /**
     * This method is a compromise to keep registration in order. </br>
     * You should make your own BuiltinTrigger registration after Advancement Registration,
     * or it will throw unregistered problem. </br>
     * You can check {@link AllTriggers#register()} and {@link com.simibubi.create.Create#onRegister(RegisterEvent)}. <br>
     * 
     * @return A BuiltinTrigger
     */
    protected abstract BuiltinTrigger add(ResourceLocation id);

    protected abstract void addToAdvancementEntries();

    protected abstract ResourceLocation getBackground();

    protected abstract String namespace();

    private String titleKey() {
        return "advancement." + namespace() + "." + id;
    }

    private String descriptionKey() {
        return titleKey() + ".desc";
    }

    public boolean isAlreadyAwardedTo(Player player) {
        if (!(player instanceof ServerPlayer sp))
            return true;
        AdvancementHolder advancement = sp.getServer()
                .getAdvancements()
                .get(asResource(id));
        if (advancement == null)
            return true;
        return sp.getAdvancements()
                .getOrStartProgress(advancement)
                .isDone();
    }

    public BuiltinTrigger builtinTrigger() {
        if (builtinTrigger == null)
            throw new UnsupportedOperationException(
                    "Advancement " + id + " uses external Triggers");
        return builtinTrigger;
    }

    private ResourceLocation asResource(String id) {
        return ResourceLocation.fromNamespaceAndPath(namespace(), id);
    }

    public void awardTo(Player player) {
        if (!(player instanceof ServerPlayer sp))
            return;
        if (builtinTrigger == null)
            throw new UnsupportedOperationException(
                    "Advancement " + id + " uses external Triggers, it cannot be awarded directly");
        builtinTrigger.trigger(sp);
    }

    public void save(Consumer<AdvancementHolder> t, HolderLookup.Provider registries) {
        if (parent != null)
            mcBuilder.parent(parent.datagenResult);

        if (createBuilder.func != null)
            createBuilder.icon(createBuilder.func.apply(registries));

        mcBuilder.display(createBuilder.icon, Component.translatable(titleKey()),
                Component.translatable(descriptionKey()).withStyle(s -> s.withColor(0xDBA213)),
                id.equals("root") ? getBackground() : null, createBuilder.type.advancementType, createBuilder.type.toast,
                createBuilder.type.announce, createBuilder.type.hide);

        datagenResult = mcBuilder.save(t, asResource(id)
                .toString());
    }

    public void provideLang(BiConsumer<String, String> consumer) {
        consumer.accept(titleKey(), title);
        consumer.accept(descriptionKey(), description);
    }

    public enum TaskType {
        SILENT(AdvancementType.TASK, false, false, false),
        NORMAL(AdvancementType.TASK, true, false, false),
        NOISY(AdvancementType.TASK, true, true, false),
        EXPERT(AdvancementType.GOAL, true, true, false),
        SECRET(AdvancementType.GOAL, true, true, true);

        private final AdvancementType advancementType;
        private final boolean toast;
        private final boolean announce;
        private final boolean hide;

        TaskType(AdvancementType advancementType, boolean toast, boolean announce, boolean hide) {
            this.advancementType = advancementType;
            this.toast = toast;
            this.announce = announce;
            this.hide = hide;
        }
    }

    public class Builder {
        private CDPAdvancement.TaskType type = CDPAdvancement.TaskType.NORMAL;
        private boolean externalTrigger;
        private int keyIndex;
        private ItemStack icon;
        private Function<HolderLookup.Provider, ItemStack> func;

        public CDPAdvancement.Builder special(CDPAdvancement.TaskType type) {
            this.type = type;
            return this;
        }

        public CDPAdvancement.Builder after(CDPAdvancement other) {
            CDPAdvancement.this.parent = other;
            return this;
        }

        public CDPAdvancement.Builder icon(ItemProviderEntry<?, ?> item) {
            return icon(item.asStack());
        }

        public CDPAdvancement.Builder icon(ItemLike item) {
            return icon(new ItemStack(item));
        }

        public CDPAdvancement.Builder icon(ItemStack stack) {
            icon = stack;
            return this;
        }

        public CDPAdvancement.Builder icon(Function<HolderLookup.Provider, ItemStack> func) {
            this.func = func;
            return this;
        }

        public CDPAdvancement.Builder title(String title) {
            CDPAdvancement.this.title = title;
            return this;
        }

        public CDPAdvancement.Builder description(String description) {
            CDPAdvancement.this.description = description;
            return this;
        }

        public CDPAdvancement.Builder whenBlockPlaced(Block block) {
            return externalTrigger(ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(block));
        }

        public CDPAdvancement.Builder whenIconCollected() {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(icon.getItem()));
        }

        public CDPAdvancement.Builder whenItemCollected(ItemProviderEntry<?, ?> item) {
            return whenItemCollected(item.asStack()
                    .getItem());
        }

        public CDPAdvancement.Builder whenItemCollected(ItemLike itemProvider) {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(itemProvider));
        }

        public CDPAdvancement.Builder whenStatReach(Stat<?> stat, MinMaxBounds.Ints bounds) {
            return externalTrigger(StatTrigger.Instance.of(stat, bounds));
        }

        public CDPAdvancement.Builder whenItemCollected(TagKey<Item> tag) {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance
                    .hasItems(ItemPredicate.Builder.item().of(tag).build()));
        }

        public CDPAdvancement.Builder awardedForFree() {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[] {}));
        }

        public CDPAdvancement.Builder externalTrigger(Criterion<?> trigger) {
            mcBuilder.addCriterion(String.valueOf(keyIndex), trigger);
            externalTrigger = true;
            keyIndex++;
            return this;
        }
    }
}
