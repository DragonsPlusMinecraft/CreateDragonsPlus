## Create: Dragons Plus 1.11.3

Updated for Sable 2.0.3 and Create: Aeronautics 1.3.0.

When using the Sable and Create: Aeronautics integration, NeoForge 21.1.228 or newer is required.

### Update
* Updated compatibility with Sable 2.0.3 and Create: Aeronautics 1.3.0.
* Added separate Dye Fluid block and Open Pipe contact controls, optional source consumption, and Mechanical Mixer coloring recipes.

### Fix
* Fixed Simulated fan processing mixins causing class-loading errors.
* Fixed potion data being ignored for Dragon's Breath mixing recipe fluid inputs. (by @ShrHang)
* Fixed Create fluid pipes being unable to drain full Dragon's Breath Cauldrons.
* Fixed Dragon's Breath Cauldrons not being mineable with pickaxes or dropping an empty cauldron.
* Fixed Dragon's Breath Cauldrons returning no item when picked in Creative mode.
* Fixed Fragile Fluid Tank goggle tooltips crashing for fluids without a break-effect handler.
* Fixed filled Fragile Fluid Tanks freezing the server when breaking during a Simulated physics step.
* Fixed Sable-only clients loading the Simulated extension and crashing.
* Fixed Bulk Coloring treating Supplementaries' item-renaming recipe as coloring and returning named dye.
* Fixed `create_dragons_plus:not_applicable_for_polishing` being evaluated before block tags were loaded.
* Corrected the Rare Blaze and Rare Marble Gate package registry IDs while preserving their legacy misspellings. (by @wenxiaojie1)
