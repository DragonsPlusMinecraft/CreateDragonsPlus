## Tags

### Fluid

* `c:dyes` for Dye Fluid and `c:dyes/<color>` for a specific color
* `c:dragon_breath` for Liquid Dragon's Breath
* `create_dragons_plus:fan_processing_catalysts/ending` for Bulk Ending catalysts

### Item

* `c:buckets/dye` for Buckets of Dye Fluid and `c:buckets/dye/<color>` for a specific color
* `c:buckets/dragon_breath` for the Bucket of Liquid Dragon's Breath
* `c:buckets` for bucket items, including `c:buckets/dye` and `c:buckets/dragon_breath`
* `c:dyed/<color>` for items dyed with a specific color, including the corresponding Bucket of Dye Fluid
* `create_dragons_plus:not_applicable_for_coloring` for items that should not receive automatic Bulk Coloring recipe support

### Block

* `create_dragons_plus:passive_block_freezers` for passive block freezers used by Bulk Freezing
* `create_dragons_plus:fan_processing_catalysts/sanding` for Bulk Sanding catalysts
* `create_dragons_plus:fan_processing_catalysts/ending` for Bulk Ending catalysts
* `create_dragons_plus:not_applicable_for_polishing` for blocks that should not receive automatic polished-block Sandpaper/Bulk Sanding recipe support

## Data Maps

### Bulk Coloring Catalysts

Bulk Coloring catalysts are data maps instead of tags:

* `create_dragons_plus:fan_processing_catalysts/coloring` on fluids
* `create_dragons_plus:fan_processing_catalysts/coloring` on blocks

Each entry maps a fluid or block to a vanilla dye color name such as `red`, `light_blue`, or `black`.

### Air Current Block Interaction

The Sable-backed simulated extension exposes these block-to-block data maps:

* `create_dragons_plus:air_current_block_interaction/blasting`
* `create_dragons_plus:air_current_block_interaction/smoking`
* `create_dragons_plus:air_current_block_interaction/splashing`
* `create_dragons_plus:air_current_block_interaction/haunting`
* `create_dragons_plus:air_current_block_interaction/freezing`
* `create_dragons_plus:air_current_block_interaction/sanding`
* `create_dragons_plus:air_current_block_interaction/ending`

The mod ships default mappings for Blasting, Smoking, Splashing, and Freezing. The other map types are registered so packs can add their own mappings.

### Fragile Fluid Tank Break Effects

The simulated extension also exposes these block-to-block data maps for Fragile Fluid Tank break effects:

* `create_dragons_plus:fragile_fluid_tank/lava`
* `create_dragons_plus:fragile_fluid_tank/water`

## Recipes

### Bulk Coloring

`Bulk Coloring` is the fan recipe type `create_dragons_plus:coloring`. Its format is similar to Create processing recipes, with one extra `color` field for the dye color.

`Bulk Coloring` automatically supports every 1:1 and 1:8 dyeing crafting recipe unless the input item is in `create_dragons_plus:not_applicable_for_coloring`.

### Bulk Freezing

`Bulk Freezing` is the fan recipe type `create_dragons_plus:freezing`.

### Bulk Ending

`Bulk Ending` is the fan recipe type `create_dragons_plus:ending`.

### Bulk Sanding

`Bulk Sanding` is the fan recipe type `create_dragons_plus:sanding`.

Bulk Sanding requires Quicksand, Create: Dreams & Desires, or another valid block in `create_dragons_plus:fan_processing_catalysts/sanding`.

`Bulk Sanding` automatically supports every automatable Sandpaper Polishing recipe.

`Bulk Sanding` automatically supports de-oxidizing oxidizables and unwaxing waxables by using NeoForge inverse data maps. These recipes are also added to Sandpaper Polishing.

`Bulk Sanding` automatically supports polishing certain blocks into their polished variants by item id. These recipes are also added to Sandpaper Polishing. If a block should not be handled by this rule, add it to the block tag `create_dragons_plus:not_applicable_for_polishing`.

## Optional Compat

### Create: Garnished

`Bulk Coloring` and `Bulk Freezing` support compatible fan processing recipes from Create: Garnished.

### Create: Dreams & Desires

`Bulk Sanding`, `Bulk Ending`, and `Bulk Freezing` support compatible fan processing recipes from Create: Dreams & Desires.

### Quicksand

Blocks in `c:quicksands` are included in `create_dragons_plus:fan_processing_catalysts/sanding`, so Quicksand can provide a default Bulk Sanding catalyst.

## Sable / Simulated Extension

The Sable-backed simulated extension is enabled when `aeronautics` is loaded.

### Added Blocks

* `create_dragons_plus:fragile_fluid_tank`
* `create_dragons_plus:levitite_fragile_fluid_tank`

### Sable Tags and Physics Data

* `sable:fragile` includes both Fragile Fluid Tanks
* `sable:light` includes `create_dragons_plus:fluid_hatch`
* `create_dragons_plus:floating_materials/fragile_fluid_tank` defines the floating material referenced by the levitite tank
* `create_dragons_plus:physics_block_properties/levitite_fragile_fluid_tank` assigns that floating material to the levitite tank

### Features

* Air currents on simulated contraptions can run block interactions through the data maps listed above.
* Fragile Fluid Tanks break on impact and use fluid-specific break effects.
* Built-in break effect handlers exist for milk, lava, water, potions, tea, Dragon's Breath Fluid, and Dye Fluids.

## Config

### Feature Flags

Feature flags are in the common config and require restart when changed.

Notable feature flags:

* `fluid/dye`
* `fluid/dragon_breath`
* `fluid/dye/lava_interaction_generate_colored_concrete`
* `block/fluid_hatch`
* `item/blaze_upgrade_smithing_template`
* `recipe/automatic_brewing/dragon_breath`
* `recipe/sand_paper_polishing/polished_blocks`
* `recipe/sand_paper_polishing/oxidized_blocks`
* `recipe/sand_paper_polishing/waxed_blocks`
* `fluid/air_current_block_interaction`
* `fluid/fragile_fluid_tank`

Mods depending on certain features may forcibly enable or disable them. In that case, the corresponding config value is ignored.

### Recipe Switches

The normal server config contains packmaker-facing switches for:

* `enableBulkColoring`
* `enableBulkFreezing`
* `enableBulkSanding`
* `enableBulkEnding`

### Simulated Extension Server Config

When the simulated extension is active, `create_dragons_plus-simulated-extension-server.toml` provides:

* Per-processing-type air current block interaction switches for Blasting, Smoking, Splashing, Haunting, Freezing, Ending, Sanding, and Coloring
* Fragile Fluid Tank capacity and break-effect tuning
* Lava ignition / fire spread toggles for Fragile Fluid Tanks
* Dye Fluid block-coloring toggle for Fragile Fluid Tanks
