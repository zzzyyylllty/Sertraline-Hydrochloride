# ExternalItemHelper

`ExternalItemHelper` builds items from other plugins via ItemBridge, and registers Sertraline itself as an ItemBridge Provider named `sertralineprivate`, so external plugins can also build items by Sertraline ID.

---

## build

Build an item (with player context). `plugin` is the external item plugin name, `id` is the item ID; passing a player lets items that depend on player variables resolve correctly.

`build(player: Player?, plugin: String, id: String): ItemStack?`

Returns `null` on failure. Passing `plugin` as `"sertralineprivate"` builds Sertraline private items.

## buildNoPlayer

Build an item (no player context). `plugin` is the external item plugin name, `id` is the item ID.

`buildNoPlayer(plugin: String, id: String): ItemStack?`

Returns `null` on failure. Use it directly for items that do not depend on player variables.
