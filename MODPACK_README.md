## Tags
#### Fluid
* `c:dyes` for Dye Fluid and `c:dyes/color`for specific color
* `c:dragons_breath` for Liquid Dragon's Breath
#### Item
* `c:buckets/dye` for Bucket of Dye Fluid and `c:buckets/dye/color`for specific color
* `c:dyed/color` for specific color dyed item, which includes corresponding Bucket of Dye Fluid
* `c:buckets` for Bucket Item, which includes `c:buckets/dye`

## Recipes
### Bulk Coloring
`Bulk Coloring` of Fan processing supports custom coloring recipe. It has similar format as the recipe of Create, with type `create_dragons_plus:coloring`. Bulk Coloring recipe requires extra `color` field as dye fluid color.   
`Bulk Coloring` also supports every 1:1 and 1:8 dyeing crafting recipe.
### Bulk Freezing
`Bulk Freezing` recipe is the recipe of Fan Freezing. It has similar format as the recipe of Create, with type `create_dragons_plus:freezing`.
### Compat with Create: Garnished
`Bulk Coloring` and `Bulk Freezing` supports all Fan processing recipes of Create: Garnished.

## Feature Flags
If you want to use C:DP only and want to disable some feature, you can configure it in common config.  
Mods depending on certain features may forcibly enable/disable them, in that case, the corresponding config will be ignored.