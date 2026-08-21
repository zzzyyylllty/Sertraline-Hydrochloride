# ItemStackUtil

`ItemStackUtil` provides item NBT read/write and rebuild utilities, built on TabooLib's `ItemTag` and Sertraline's item rebuild pipeline.

---

## getItemTag

Get the item's `ItemTag` (the handle for reading/writing item NBT). Use `tag["key"]` to read and `tag["key"] = ...` to write.

`getItemTag(itemStack: ItemStack): ItemTag`

## setItemTag

Overwrite the item's NBT with the given `ItemTag`. **Does NOT modify the original item** — it returns the new item with the tag applied, so capture the return value.

`setItemTag(itemStack: ItemStack, tag: ItemTag): ItemStack`

## setItemTagDirect

Overwrite the item's NBT with the given `ItemTag`. **Modifies the original item in place**; the return value is the same modified item.

`setItemTagDirect(itemStack: ItemStack, tag: ItemTag): ItemStack`

## transferToByte

TabooLib does not support boolean NBT. Pass `true`/`false` through this function to convert to the corresponding `Byte` value (`1`/`0`) before writing to NBT.

`transferToByte(input: Any?): Any?`

## rebuildLore

Rebuild the item's lore from its template (recalculating variables, placeholders, conditional lines, etc.). Modifies the original item in place; no return value.

`rebuildLore(itemStack: ItemStack, player: Player?): Unit`

## rebuildName

Rebuild the item's display name from its template. Modifies the original item in place; no return value.

`rebuildName(itemStack: ItemStack, player: Player?): Unit`

## rebuild

Fully rebuild the item from its template (name + lore + attributes, etc.) and return the new item. Used to refresh old items after item data/templates change.

`rebuild(itemStack: ItemStack, player: Player?): ItemStack`

## rebuildBypassKeepData

Fast rebuild that skips the `keep-data` retention logic and rebuilds directly from the template, returning the new item. Suited for items whose source is known and that need no runtime-data retention.

`rebuildBypassKeepData(itemStack: ItemStack, player: Player?): ItemStack`

## rebuildUnsafe

Force rebuild, skipping some safety checks. Modifies the original item in place; no return value. Use only when you know exactly what you are doing.

`rebuildUnsafe(itemStack: ItemStack, player: Player?): Unit`
