## Create: Dragons Plus 1.11.0

### Added
* Added Dye Fluid and Bulk Coloring support for Dye Depot, Dyenamics and Arts & Crafts (Bleachdew as a special "dye"). Dye Fluid bucket variants, recipes, tags, JEI entries, lava interactions and break effects for Fragile Fluid Tanks for supported extra dye colors.
* Player can search recipes in JEI via dye fluid and dye fluid bucket.

### Changed
* Bulk Coloring now supports namespaced dye variants instead of only vanilla dye colors

### Fix
* Bring back Bulgarian Localization (by @Boris Valkov)
* Fix Automated Brewing with Liquid Dragon's Breath doesn't respect priority
* Fix Major TPS Lag: ColoringFanProcessingType performs expensive recipe lookups on every Chute/BlockEntity tick
