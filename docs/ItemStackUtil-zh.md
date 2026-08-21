# ItemStackUtil

`ItemStackUtil` 提供物品 NBT 读写与重建相关工具，基于 TabooLib 的 `ItemTag` 和 Sertraline 的物品重建管线。

---

## getItemTag

获取物品的 `ItemTag`（物品 NBT 的读写句柄）。可用 `tag["key"]` 读取、`tag["key"] = ...` 写入。

`getItemTag(itemStack: ItemStack): ItemTag`

## setItemTag

用给定的 `ItemTag` 覆盖物品的 NBT。**不会修改原物品**，返回写入后的新物品，需接收返回值。

`setItemTag(itemStack: ItemStack, tag: ItemTag): ItemStack`

## setItemTagDirect

用给定的 `ItemTag` 覆盖物品的 NBT。**会直接修改原物品**，返回值同样是修改后的物品。

`setItemTagDirect(itemStack: ItemStack, tag: ItemTag): ItemStack`

## transferToByte

TabooLib 不支持 boolean 类型的 NBT，传入 `true`/`false` 时可用此函数转换为对应的 `Byte` 值（`1`/`0`），再写入 NBT。

`transferToByte(input: Any?): Any?`

## rebuildLore

按物品模板重新构建该物品的 lore（重算变量、占位符、条件行等）。直接修改原物品，无返回值。

`rebuildLore(itemStack: ItemStack, player: Player?): Unit`

## rebuildName

按物品模板重新构建该物品的名称。直接修改原物品，无返回值。

`rebuildName(itemStack: ItemStack, player: Player?): Unit`

## rebuild

按物品模板完整重建该物品（名称 + lore + 属性等），返回新物品。用于物品数据/模板更新后刷新旧物品。

`rebuild(itemStack: ItemStack, player: Player?): ItemStack`

## rebuildBypassKeepData

快速重建：跳过 `keep-data` 保留逻辑，直接从模板重建并返回新物品。适合已知物品来源、无需保留运行时数据的场景。

`rebuildBypassKeepData(itemStack: ItemStack, player: Player?): ItemStack`

## rebuildUnsafe

强制重建，跳过部分安全校验。直接修改原物品，无返回值。仅在明确知道自己在做什么时使用。

`rebuildUnsafe(itemStack: ItemStack, player: Player?): Unit`
