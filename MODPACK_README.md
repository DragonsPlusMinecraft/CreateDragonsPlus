## Tags
#### Fluid
* `c:dyes` for Dye Fluid and `c:dyes/color`for specific color
* `c:dragons_breath` for Liquid Dragon's Breath
#### Item
* `c:buckets/dye` for Bucket of Dye Fluid and `c:buckets/dye/color`for specific color
* `c:dyed/color` for specific color dyed item, which includes corresponding Bucket of Dye Fluid
* `c:buckets` for Bucket Item, which includes `c:buckets/dye`
* `create_dragons_plus:not_applicable_for_coloring` for Item that is not applicable for automatic Coloring Recipe support
#### Block
* `create_dragons_plus:passiveBlockFreezers` for Passive Block Freezers
* `create_dragons_plus:fan_processing_catalysts/sanding` for Bulk Sanding Catalysts
* `create_dragons_plus:fan_processing_catalysts/ending` for Bulk Ending Catalysts
* `create_dragons_plus:not_applicable_for_polishing` for Block that is not applicable for automatic Polishing Block Sanding Recipe support

## Recipes
### Bulk Coloring
`Bulk Coloring` recipe is the recipe of Fan Bulk Coloring. It has similar format as the recipe of Create, with type `create_dragons_plus:coloring`, and Bulk Coloring recipe requires extra `color` field as dye fluid color.   

`Bulk Coloring` supports every 1:1 and 1:8 dyeing crafting recipe.
### Bulk Freezing
`Bulk Freezing` recipe is the recipe of Fan Bulk Freezing. It has similar format as the recipe of Create, with type `create_dragons_plus:freezing`.
### Bulk Ending
`Bulk Ending` recipe is the recipe of Fan Bulk Sanding. It has similar format as the recipe of Create, with type `create_dragons_plus:ending`.
### Bulk Sanding (Require Quicksand Mod or C:DnD or valid bulk sanding catalyst)
`Bulk Sanding` recipe is the recipe of Fan Bulk Sanding. It has similar format as the recipe of Create, with type `create_dragons_plus:sanding`.

`Bulk Sanding` supports every Sand Paper Polishing recipe.

`Bulk Sanding` supports de-oxidizing Oxidizables and unwaxing Waxables automatically by using Corresponding inverse datamap of NeoForge. There recipes will also be added to `Sandpaper Sanding`.

`Bulk Sanding` supports polishing certain blocks to polished block automatically by identifying the item id. There recipes will also be added to `Sandpaper Sanding`. If a block shouldn't be applicable for this situation, add block to **block tag** `create_dragons_plus:not_applicable_for_polishing`.

### Compat with Create: Garnished
`Bulk Coloring` and `Bulk Freezing` supports all Fan processing recipes of Create: Garnished.

### Compat with Create: Dreams & Desires
`Bulk Sanding`, `Bulk Ending` and `Bulk Freezing` supports all Fan processing recipes of Create: Dreams & Desires.

## Feature Flags
If you want to use C:DP only and want to disable some feature, you can configure it in common config.  
Mods depending on certain features may forcibly enable/disable them, in that case, the corresponding config will be ignored.