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
Note that Create: Dragons Plus has exposed Create and it's dependencies as Gradle's `api` configuration, so you don't need to specify them again in your dependencies block unless you wish to use another version of these artifacts.

## Contribute
Feel free to open a PR to either provide localization or to add another feature! All help is appreciated!
### If you want to help us to translate...
Please find incomplete language file in `src/generated/assets/create_dragon_plus/lang`, and **submit complete language file to`src/translations/assets/create_dragon_plus/lang`**.
