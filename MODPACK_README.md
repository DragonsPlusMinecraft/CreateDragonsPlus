## Tags

### Fluid

* `forge:dyes` contains all Create: Dragons Plus dye fluids.
* `forge:dyes/<serialized_name>` contains one dye variant. Examples include
  `forge:dyes/red`, `forge:dyes/dye_depot_amber`, `forge:dyes/dyenamics_amber`, and
  `forge:dyes/arts_and_crafts_bleached`.
* `forge:dragon_breath` contains Liquid Dragon's Breath.
* `create_dragons_plus:fan_processing_catalysts/coloring/<serialized_name>` selects the
  fluid catalyst for one Bulk Coloring variant.
* `create_dragons_plus:fan_processing_catalysts/ending` contains fluid catalysts for Bulk Ending.

### Item

* `forge:buckets/dye` contains dye-fluid buckets, with
  `forge:buckets/dye/<serialized_name>` for an individual variant.
* `forge:buckets/dragon_breath` contains the Bucket of Liquid Dragon's Breath.
* `forge:buckets` includes both bucket groups above.
* `create_dragons_plus:dyes/<namespace>/<color>` contains source dye items used by dye-fluid
  recipes. Keeping the namespace in this tag distinguishes colors shared by Dye Depot and
  Dyenamics.
* `create_dragons_plus:not_applicable_for_coloring` excludes inputs from automatically converted
  Bulk Coloring recipes.

### Block

* `create_dragons_plus:passive_block_freezers` contains passive Bulk Freezing catalysts.
* `create_dragons_plus:fan_processing_catalysts/sanding` contains exactly `minecraft:sand` by
  default. Datapacks may extend the tag.
* `create_dragons_plus:fan_processing_catalysts/ending` contains block catalysts for Bulk Ending.
* `create_dragons_plus:fan_processing_catalysts/coloring/<serialized_name>` selects block
  catalysts for an individual Bulk Coloring variant. These tags are empty by default so packs can
  extend them without replacing generated data.
* `create_dragons_plus:not_applicable_for_polishing` excludes blocks from automatic polished-block
  recipes.

## Recipes

### Bulk Coloring

`Bulk Coloring` uses the fan recipe type `create_dragons_plus:coloring`. It follows Create's 1.20.1
processing-recipe format and adds a `color` field containing the dye variant ID.

```json
{
  "type": "create_dragons_plus:coloring",
  "color": "dyenamics:amber",
  "ingredients": [
    {
      "item": "minecraft:white_wool"
    }
  ],
  "results": [
    {
      "item": "minecraft:orange_wool"
    }
  ]
}
```

Every 1:1 and 1:8 dyeing crafting recipe is eligible for automatic conversion unless its input is
in `create_dragons_plus:not_applicable_for_coloring`. Vanilla variants can also recolor vanilla
colorable entities. Non-vanilla variants process matching recipes and equipment without forcing an
unrelated vanilla entity color.

Each variant gets matching fluid and block catalyst tags at
`create_dragons_plus:fan_processing_catalysts/coloring/<serialized_name>`. The generated fluid tag
contains the CDP dye source and flowing fluids. The 16 vanilla-color tags also contain optional
Create: Garnished mastic resin source and flowing fluids.

### Bulk Freezing

`Bulk Freezing` uses `create_dragons_plus:freezing` and Create 6.0.8 processing-recipe JSON. The
built-in recipes include `ice -> packed_ice`, `packed_ice -> blue_ice`, and
`magma_cream -> slime_ball`.

### Bulk Ending

`Bulk Ending` uses `create_dragons_plus:ending`. Its catalysts are controlled by the block and fluid
tags named `create_dragons_plus:fan_processing_catalysts/ending`.

### Bulk Sanding

`Bulk Sanding` uses `create_dragons_plus:sanding`. Its built-in catalyst is normal sand only; red
sand is not included.

Bulk Sanding supports automatable Sandpaper Polishing recipes. CDP also generates runtime sanding
and polishing recipes for vanilla/registered copper oxidation and waxing relationships, and for
eligible `<name> -> polished_<name>` block pairs. These runtime recipes are rebuilt after resource
reload. Add a block to `create_dragons_plus:not_applicable_for_polishing` to opt it out of the
polished-name rule.

## Optional Compatibility

### Dye Depot

When Dye Depot is loaded, CDP registers fluids, buckets, mixing recipes, catalyst tags, and Bulk
Coloring support for its official 1.20.1 colors. Overlapping names remain namespaced; for example,
`dye_depot:amber` and `dyenamics:amber` are distinct variants.

### Dyenamics

When Dyenamics is loaded, CDP registers fluids, buckets, mixing recipes, catalyst tags, and Bulk
Coloring support for its official 1.20.1 colors.

### Arts & Crafts

When Arts & Crafts and its JinxedLib dependency are loaded, `arts_and_crafts:bleachdew` supplies the
`arts_and_crafts:bleached` variant. This creates the corresponding dye fluid and bucket and connects
it to Bulk Coloring; it is not a separate fan processing type.

### Create: Garnished

CDP consumes the official Garnished 2.1.7.b dye-blowing recipes for the 16 vanilla colors and its
freezing recipes. While the corresponding CDP feature is enabled, the equivalent Garnished fan
processing type and duplicate JEI recipe supplier are disabled so only the CDP category processes
those recipes. Garnished's own Dragon's Breath ending catalyst data is left untouched.

### Immersive Engineering

The Fluid Hatch accepts Immersive Engineering potion fluid through Forge fluid capabilities. IE
1.20.1 stores potion data in NBT without a bottle type, so the hatch outputs a normal potion item,
copies the complete potion NBT, and consumes 250 mB.

## Configuration

### Feature Flags

Feature flags are in the common config and require a restart when changed. The public feature IDs
are:

* `fluid/dye`
* `fluid/dragon_breath`
* `fluid/dragon_breath/dripstone_duplication`
* `fluid/dye/lava_interaction_generate_colored_concrete`
* `block/fluid_hatch`
* `item/blaze_upgrade_smithing_template`
* `recipe/automatic_brewing/dragon_breath`
* `recipe/sand_paper_polishing/polished_blocks`
* `recipe/sand_paper_polishing/oxidized_blocks`
* `recipe/sand_paper_polishing/waxed_blocks`

Mods depending on a feature may force it enabled or disabled. In that case the corresponding config
value is ignored.

### Recipe Switches

The normal server config exposes:

* `enableBulkColoring`
* `enableBulkFreezing`
* `enableBulkSanding`
* `enableBulkEnding`

The condition serializer remains `create_dragons_plus:config_feature`; generated Forge 1.20.1
conditional recipes use it to reference the feature IDs above.
