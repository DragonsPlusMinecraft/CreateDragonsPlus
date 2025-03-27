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

import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;
import static plus.dragons.createdragonsplus.data.recipe.VanillaRecipeBuilders.shaped;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import java.util.EnumMap;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SmithingTemplateItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import plus.dragons.createdragonsplus.client.texture.CDPGuiTextures;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeColors;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.data.tag.ItemTagRegistry;

public class CDPItems {
    public static final CommonTags COMMON_TAGS = new CommonTags();

    public static final ItemEntry<SmithingTemplateItem> BLAZE_UPGRADE_SMITHING_TEMPLATE = REGISTRATE
            .item("blaze_upgrade_smithing_template", prop -> new SmithingTemplateItem(
                    Tooltips.BLAZE_UPGRADE_APPLIES_TO,
                    Tooltips.BLAZE_UPGRADE_INGREDIENTS,
                    Tooltips.BLAZE_UPGRADE,
                    Tooltips.BLAZE_UPGRADE_BASE_SLOT,
                    Tooltips.BLAZE_UPGRADE_ADDITIONS_SLOT,
                    CDPGuiTextures.BLAZE_UPGRADE_BASE_SLOT_ICONS,
                    CDPGuiTextures.BLAZE_UPGRADE_ADDITIONS_SLOT_ICONS
            ))
            .lang("Smithing Template")
            .recipe((ctx, prov) -> shaped()
                    .output(ctx.get(), 2)
                    .define('t', ctx.get())
                    .define('n', Items.NETHERRACK)
                    .define('b', Items.BLAZE_ROD)
                    .pattern("btb")
                    .pattern("bnb")
                    .pattern("bbb")
                    .unlockedBy("has_template", RegistrateRecipeProvider.has(ctx.get()))
                    .withCondition(CDPConfig.features().blazeUpgradeSmithingTemplate)
                    .accept(prov))
            .register();

    public static void register(IEventBus modBus) {
        modBus.register(CDPItems.class);
        REGISTRATE.registerItemTags(COMMON_TAGS);
    }

    @SubscribeEvent
    public static void buildCreativeModeTab(final BuildCreativeModeTabContentsEvent event) {
        var tab = event.getTabKey();
        if (tab == CreativeModeTabs.COLORED_BLOCKS) {
            if (CDPConfig.features().dyeFluids.get()) {
                for (var color : DyeColors.CREATIVE_MODE_TAB) {
                    CDPFluids.DYES_BY_COLOR.get(color).getBucket().ifPresent(event::accept);
                }
            }
        } else if (tab == CreativeModeTabs.INGREDIENTS) {
            if (CDPConfig.features().blazeUpgradeSmithingTemplate.get()) {
                event.insertAfter(
                        new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        BLAZE_UPGRADE_SMITHING_TEMPLATE.asStack(),
                        TabVisibility.PARENT_AND_SEARCH_TABS
                );
            }
        }
    }

    public static class Tooltips {
        private static final ResourceLocation BLAZE_UPGRADE_SMITHING_TEMPLATE =
                CDPCommon.asResource("smithing_template.blaze_upgrade");
        public static final Component BLAZE_UPGRADE_APPLIES_TO = REGISTRATE.addLang("item",
                BLAZE_UPGRADE_SMITHING_TEMPLATE.withSuffix(".applies_to"),
                "Blaze Burner"
        ).withStyle(ChatFormatting.BLUE);
        public static final Component BLAZE_UPGRADE_INGREDIENTS = REGISTRATE.addLang("item",
                BLAZE_UPGRADE_SMITHING_TEMPLATE.withSuffix(".ingredients"),
                "Working blocks for Blaze"
        ).withStyle(ChatFormatting.BLUE);
        public static final Component BLAZE_UPGRADE = REGISTRATE.addLang("upgrade",
                CDPCommon.asResource("blaze_upgrade"),
                "Blaze Upgrade"
        ).withStyle(ChatFormatting.GRAY);
        public static final Component BLAZE_UPGRADE_BASE_SLOT = REGISTRATE.addLang("item",
                BLAZE_UPGRADE_SMITHING_TEMPLATE.withSuffix(".base_slot_description"),
                "Add Blaze Burner"
        );
        public static final Component BLAZE_UPGRADE_ADDITIONS_SLOT = REGISTRATE.addLang("item",
                BLAZE_UPGRADE_SMITHING_TEMPLATE.withSuffix(".additions_slot_description"),
                "Add working blocks for Blaze"
        );
    }

    public static class CommonTags extends ItemTagRegistry {
        public final TagKey<Item> dyeBuckets = tag("buckets/dye", "Dye Buckets");
        public final EnumMap<DyeColor, TagKey<Item>> dyeBucketsByColor = Util.make(new EnumMap<>(DyeColor.class), map -> {
            for (var color : DyeColors.ALL) {
                var tag = tag("buckets/dye/" + color.getName(), DyeColors.LOCALIZATION.get(color) + " Dye Buckets");
                map.put(color, tag);
                addTag(this.dyeBuckets, tag);
            }
        });

        protected CommonTags() {
            super("c");
            addTag(Tags.Items.BUCKETS, dyeBuckets);
        }
    }
}
