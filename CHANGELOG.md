## Create: Dragons Plus 1.11.0

### Added
* Added Dye Fluid and Bulk Coloring support for Dye Depot colors
* Added Dye Fluid and Bulk Coloring support for Dyenamics colors
* Added Arts & Crafts Bleachdew as a special Bleached dye fluid for Bulk Coloring
* Added Dye Fluid bucket variants, recipes, tags, JEI entries, and lava interactions for supported extra dye colors

### Changed
* Bulk Coloring now supports namespaced dye variants instead of only vanilla dye colors
* Dye Fluid break effects for Fragile Fluid Tanks now work with all registered dye variants

### Fix
* Bring back Bulgarian Localization (by @Boris Valkov)
* Fix Automated Brewing with Liquid Dragon's Breath doesn't respect priority
* Fix Major TPS Lag: ColoringFanProcessingType performs expensive recipe lookups on every Chute/BlockEntity tick
