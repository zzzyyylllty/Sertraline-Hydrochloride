# ExternalItemHelper

`ExternalItemHelper` 通过 ItemBridge 从其他插件构建物品，并把 Sertraline 自身注册为名为 `sertralineprivate` 的 ItemBridge Provider，使外部插件也能按 Sertraline ID 构建物品。

---

## build

构建物品（带玩家上下文）。`plugin` 为外部物品插件名，`id` 为物品 ID；携带玩家可让依赖玩家变量的物品正确解析。

`build(player: Player?, plugin: String, id: String): ItemStack?`

构建失败返回 `null`。`plugin` 传入 `"sertralineprivate"` 可构建 Sertraline 私有物品。

## buildNoPlayer

构建物品（无玩家上下文）。`plugin` 为外部物品插件名，`id` 为物品 ID。

`buildNoPlayer(plugin: String, id: String): ItemStack?`

构建失败返回 `null`。不依赖玩家变量的物品可直接用它构建。
