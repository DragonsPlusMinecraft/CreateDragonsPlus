## Create: Dragons Plus 1.11.0

### Added
* Add Dye Fluid and Bulk Coloring support for **Dye Depot**, **Dyenamics** and **Arts & Crafts** (Bleachdew as a special "dye"). Dye Fluid bucket variants, recipes, tags, JEI entries, lava interactions and break effects for Fragile Fluid Tanks for supported extra dye colors.
* Add **The Aether** support: Bulk Freezing can now process Aether Freezer recipes. Add Bulk Enchanting for The Aether Altar recipes using Golden Aercloud as the fan catalyst. Bulk Enchanting can incubate Aether Moa Eggs into Moas.
* Player can search recipes in JEI via dye fluid and dye fluid bucket.
* Liquid Dragon's Breath can now slowly fill cauldrons through pointed dripstone. Dragon's Breath Cauldrons hold four bottle-sized levels and can be filled or emptied with Dragon's Breath bottles and buckets.

### Changed
* Bulk Coloring now supports namespaced dye variants instead of only vanilla dye colors

### Fix
* Bring back Bulgarian Localization (by @Boris Valkov)
* Fix Automated Brewing with Liquid Dragon's Breath doesn't respect priority
* Fix Major TPS Lag: ColoringFanProcessingType performs expensive recipe lookups on every Chute/BlockEntity tick
* Fix fluid hatch algorithm prevents fluid from being operated in certain cases. (such as conflict with create stuff additions tank)
