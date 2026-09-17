# Developer guide

## Grouped Ponder tag cards

Use `plus.dragons.createdragonsplus.client.ponder.PonderTagGroups` from
`PonderPlugin.registerTags`. Tag, group and member IDs are `ResourceLocation` values.

```java
// Main plugin: declare the group and add its base members.
PonderTagGroups.registerGroup(TARGET_TAG, FOOD_GROUP,
        Component.translatable("example.ponder.group.foods"));
PonderTagGroups.addToGroup(helper, TARGET_TAG, FOOD_GROUP)
        .add(WHOLE_FOOD)
        .addHidden(PARTIALLY_CONSUMED_FOOD);
```

Other source sets or mods can append to the same tag/group pair:

```java
// Integration plugin's registerTags callback.
if (integrationEnabled) {
    PonderTagGroups.addToGroup(helper, TARGET_TAG, FOOD_GROUP)
            .add(INTEGRATION_FOOD);
}
```

Keep optional-mod classes in their integration source set. Load the plugin only
on the client when the required mod is present.

- `add` registers native tag members and carousel icons; `addHidden` folds members
  into the group without showing their icons. `addAll` and `addHiddenAll` accept collections.
- Register scenes through the native API; hidden members retain their own scenes.
- Contributions may precede the declaration. Each Ponder reload rebuilds the groups,
  so obtain a fresh helper in each callback; no manual clearing is needed.
- Members are deduplicated; visible membership overrides hidden membership.
  Conflicting titles or priorities for the same group raise an error.
- Priority defaults to `0`. Use `registerGroup(tag, group, title, priority)` to
  override it, e.g. `-100` for a fallback. Higher priorities claim members first;
  ties use the full group ID's lexical order.
