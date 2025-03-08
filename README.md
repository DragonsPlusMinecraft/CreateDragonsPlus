## Welcome to **Create: Dragons Plus**
A library mod for DragonsPlusMinecraft Create-addons.

## Add Depenency
```groovy
repositories {
    maven {
        url "https://maven.dragons.plus/releases" // DragonsPlusMinecraft Maven
    }
}

dependencies {
    implementation("plus.dragons.createdragonsplus:create-dragons-plus-${minecraft_version}:${create_dragons_plus_version}")
}
```

## Features

### Register
- `CDPRegistrate`: Registrate with Create related builders and other advanced functions.

### Tag
- `TagRegistry`: Instance based, easily seperate tags under different namespaces.
  Supports datagen (including tag localization).

### Localization
- `CDPRegistrate#registerBuiltinLocalization`: Merge builtin language files (under `lang/builtin`) to language provider.
- `CDPRegistrate#registerForeignLocalization`: Merge foreign language files with untranslate entries. Add `'--existing', file('src/main/translations/').getAbsolutePath()`
  to datagen program arguments to use translated language files under `src/main/translations`.

### Advancement
- `CriterionStatBehaviour`: `BlockEntityBehaviour` for awarding owner player criterion and stats.
- `BuiltinTrigger`: Criterion trigger for code triggered advancements.
- `StatTrigger`: Criterion trigger for player stats

### Gameplay
- Dye Fluids and Bulk Coloring

## Contribute
Feel free to open a PR to either translate the mod or to add another feature! All help is appreciated!

### If you want to help us to translate...
Please use the language files in `src/generated` and submit to `src/main/translations`.
