## Welcome to **Create: Dragons Plus**
A library mod for DragonsPlusMinecraft Create-addons.

## Add Depenency
```groovy
repositories {
    maven { url "https://maven.dragons.plus/releases" } // DragonsPlusMinecraft Maven
    maven { url "https://maven.fallenbreath.me/releases" } // Conditional Mixin
}

dependencies {
    implementation("plus.dragons.createdragonsplus:create-dragons-plus-${minecraft_version}:${create_dragons_plus_version}")
}
```
Note that Create: Dragons Plus has exposed Create and it's dependencies as Gradle's `api` configuration, so you don't need specify them again in your dependencies block unless you wish to use another version of these artifacts.

## Development Features

### Register
- `CDPRegistrate`: Registrate with Create related builders and other advanced functions, with builder for more registries.

### 

### Resource
- `RuntimePackResource`: Runtime resource pack implementation supporting datagen.

### Tag
- `TagRegistry`: Instance based, easily seperates tags under different namespaces.
  Supports datagen (including tag localization).

### Recipe
- Full support for custom processing recipe with custom params, including builder, serializer 
  and sequenced assembly support. (Will be superseded by Create 6.0.5)
- Custom builder for vanilla recipe types, supporting integration output (output that might not exist at runtime).
- `RecipeConverter`: Supports cached recipe conversion.

### Advancement
- `AdvancementBehaviour`: `BlockEntityBehaviour` for awarding owner player advancements by stats and builtin trigger.
- `CDPAdvancement`: Open version of `CreateAdvancement`
- `BuiltinTrigger`: Criterion trigger for code triggered advancements. Builtin trigger of CDPAdvancement. Read all comment before use.
- `StatTrigger`: Criterion trigger for player stats.

### Localization
- `CDPRegistrate#registerBuiltinLocalization`: Merge builtin language files (under `lang/builtin`) to language provider.
- `CDPRegistrate#registerForeignLocalization`: Merge foreign language files with untranslate entries. Add `'--existing', file('src/main/translations/').getAbsolutePath()`
- `CDPRegistrate#registerExtraLocalization`: Merge language piece from methods of certain format
  to datagen program arguments to use translated language files under `src/main/translations`.

### Ponder
- Ponder plugins are made to be sorted by mod dependency order, allowing addons to register scenes to existing components 
with reliable orders. (Will be superseded by Create 6.0.5)

## Gameplay Elements
- Fluid Hatch
- Dye Fluids
- Dragon's Breath (Fluid)
- Bulk Coloring (Compatible with Create: Garnished)
- Bulk Freezing (Compatible with Create: Garnished)
- Bulk Sanding (Needs Quicksand)
- Bulk Ending

## Contribute
Feel free to open a PR to either provide localization or to add another feature! All help is appreciated!

### Localization
Please use the language files in `src/generated` and submit to `src/main/translations`.
