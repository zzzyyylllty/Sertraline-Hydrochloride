package io.github.zzzyyylllty.sertraline.api

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.config.TemplateManager
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.function.data.getSertralineId
import io.github.zzzyyylllty.sertraline.item.rebuild
import io.github.zzzyyylllty.sertraline.item.rebuildLore
import io.github.zzzyyylllty.sertraline.item.rebuildName
import io.github.zzzyyylllty.sertraline.item.rebuildUnsafe
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import io.github.zzzyyylllty.sertraline.item.sertralineVarItemBuilder
import io.github.zzzyyylllty.sertraline.manager.ManagerRange
import io.github.zzzyyylllty.sertraline.manager.ManagerType
import io.github.zzzyyylllty.sertraline.manager.SubManagerType
import io.github.zzzyyylllty.sertraline.util.ItemTagUtil.parseMapNBT
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag

public class SertralineAPIImpl : SertralineAPI {
}

public interface SertralineAPI {

    // ==================== 物品查询与构建 / Item Query & Build ====================

    /**
     * 获取 Sertraline 物品对象
     * Get Sertraline Item Object
     */
    public fun getItem(s: String): ModernSItem? {
        return Sertraline.itemMap[s]
    }

    /**
     * 获取所有已注册物品
     * Get all registered items
     */
    public fun getAllItems(): Map<String, ModernSItem> {
        return Sertraline.itemMap
    }

    /**
     * 为玩家构建物品
     * Build an item for a player
     * @param [sItem] Sertraline 物品 ID / Sertraline item ID
     * @param [player] 玩家 / Player
     * @param [source] 源物品，null 则自动 / Source item, null for auto
     * @param [amount] 物品数量 / Item amount
     * @param [overrideData] 覆盖物品数据 / Override item data
     */
    public fun buildItem(sItem: String, player: Player?, source: ItemStack? = null, amount: Int = 1, overrideData: Map<String, Any?>? = null): ItemStack? {
        return sertralineItemBuilder(sItem, player, source, amount, overrideData)
    }

    /**
     * 为玩家构建物品（支持变量）
     * Build an item for a player with variables
     * @param [sItem] Sertraline 物品 ID / Sertraline item ID
     * @param [player] 玩家 / Player
     * @param [source] 源物品，null 则自动 / Source item, null for auto
     * @param [amount] 物品数量 / Item amount
     * @param [overrideData] 覆盖物品数据 / Override item data
     * @param [vars] 模板变量 / Template variables (persisted to NBT)
     * @param [context] 运行时上下文（Player、Event 等对象），可通过 {context:xxx} 标签访问，不持久化到 NBT
     *                  Runtime context (Player, Event, etc.), accessible via {context:xxx}, NOT persisted to NBT
     */
    public fun buildDataItem(sItem: String, player: Player?, source: ItemStack? = null, amount: Int = 1, overrideData: Map<String, Any?>? = null, vars: Map<String, Any?>? = null, context: Map<String, Any?>? = null): ItemStack? {
        return sertralineVarItemBuilder(sItem, player, source, amount, overrideData, vars, context)
    }

    /**
     * 获取物品的 Sertraline ID，不存在则返回 null
     * Get Sertraline ID from ItemStack, null if not a Sertraline item
     */
    public fun getId(itemStack: ItemStack): String? {
        return itemStack.getSertralineId()
    }

    /**
     * 获取 Sertraline 物品的 val 数据
     * Get a val value from a Sertraline item object
     * @param [sItem] Sertraline 物品对象 / Sertraline item object
     * @param [valId] val 键名 / Val key
     * @return 对应的 val 值，不存在则返回 null / The val value, or null if not found
     */
    public fun getVal(sItem: ModernSItem, valId: String): Any? {
        return (sItem.getDeepData("sertraline:vals") as? Map<String, Any?>)?.get(valId)
    }

    /**
     * 获取 Sertraline 物品（ItemStack）的 var 数据
     * 从物品 NBT 中读取持久化的 var 值
     * Get a var value from a Sertraline ItemStack
     * Reads the persisted var value from the item's NBT
     * @param [itemStack] Sertraline 物品 / Sertraline item
     * @param [varId] var 键名 / Var key
     * @return 对应的 var 值，不存在则返回 null / The var value, or null if not found
     */
    public fun getVar(itemStack: ItemStack, varId: String): Any? {
        return itemStack.getItemTag(true)["sertraline_data"]?.parseMapNBT()?.get(varId)
    }

    /**
     * 判断物品是否为 Sertraline 物品
     * Check if ItemStack is a Sertraline item
     */
    public fun isValidItem(itemStack: ItemStack): Boolean {
        return itemStack.getSertralineId() != null
    }

    /**
     * 判断物品是否已在 itemMap 中注册
     * Check if ItemStack is registered in itemMap
     * `true` - 已注册 / Registered
     * `false` - 未注册 / Not registered
     */
    public fun isRegisteredItem(itemStack: ItemStack): Boolean {
        return Sertraline.itemMap[itemStack.getSertralineId()] != null
    }

    /**
     * 判断指定 ID 是否已在 itemMap 中注册
     * Check if the given ID is registered in itemMap
     * `true` - 已注册 / Registered
     * `false` - 未注册 / Not registered
     */
    public fun isRegisteredItem(s: String): Boolean {
        return Sertraline.itemMap[s] != null
    }

    // ==================== 玩家背包物品 / Player Inventory Items ====================

    /**
     * 统计玩家背包中的 Sertraline 物品数量。
     * 私有范围会验证该 ID 当前属于目标玩家的私有管理器（临时优先、持久回退）。
     * 此同步方法必须在玩家所属线程调用。
     */
    fun countItem(
        id: String,
        player: Player,
        range: ManagerRange = ManagerRange.PUBLIC,
        slots: InventorySlotRange = InventorySlotRange.STORAGE
    ): Int = countInventoryItems(id, player, range, slots, verifyPrivateOwner = true)

    /**
     * 判断玩家背包中的 Sertraline 物品是否不少于指定数量。
     * 非正数量始终返回 false；此同步方法必须在玩家所属线程调用。
     */
    fun hasItem(
        id: String,
        player: Player,
        amount: Int = 1,
        range: ManagerRange = ManagerRange.PUBLIC,
        slots: InventorySlotRange = InventorySlotRange.STORAGE
    ): Boolean = hasInventoryItems(id, player, amount, range, slots, verifyPrivateOwner = true)

    /**
     * 从玩家背包中尽可能扣除 Sertraline 物品，并返回实际扣除数量。
     * 非正数量返回 0；此同步方法必须在玩家所属线程调用。
     */
    fun takeItem(
        id: String,
        player: Player,
        amount: Int = 1,
        range: ManagerRange = ManagerRange.PUBLIC,
        slots: InventorySlotRange = InventorySlotRange.STORAGE
    ): Int = takeInventoryItems(id, player, amount, range, slots, verifyPrivateOwner = true, exactly = false)

    /**
     * 仅在数量充足时从玩家背包中扣除全部 Sertraline 物品。
     * 数量不足或非正数量时不修改背包并返回 false；此同步方法必须在玩家所属线程调用。
     */
    fun takeItemExactly(
        id: String,
        player: Player,
        amount: Int = 1,
        range: ManagerRange = ManagerRange.PUBLIC,
        slots: InventorySlotRange = InventorySlotRange.STORAGE
    ): Boolean = amount > 0 &&
        takeInventoryItems(id, player, amount, range, slots, verifyPrivateOwner = true, exactly = true) == amount

    /**
     * 统计玩家背包中的 Sertraline 物品数量，不验证私有物品是否属于该玩家。
     * 此同步方法必须在玩家所属线程调用。
     */
    fun countItemAnyOwner(
        id: String,
        player: Player,
        range: ManagerRange = ManagerRange.PUBLIC,
        slots: InventorySlotRange = InventorySlotRange.STORAGE
    ): Int = countInventoryItems(id, player, range, slots, verifyPrivateOwner = false)

    /**
     * 判断玩家背包中的 Sertraline 物品是否不少于指定数量，不验证私有物品归属。
     * 非正数量始终返回 false；此同步方法必须在玩家所属线程调用。
     */
    fun hasItemAnyOwner(
        id: String,
        player: Player,
        amount: Int = 1,
        range: ManagerRange = ManagerRange.PUBLIC,
        slots: InventorySlotRange = InventorySlotRange.STORAGE
    ): Boolean = hasInventoryItems(id, player, amount, range, slots, verifyPrivateOwner = false)

    /**
     * 从玩家背包中尽可能扣除 Sertraline 物品，不验证私有物品归属。
     * 返回实际扣除数量；此同步方法必须在玩家所属线程调用。
     */
    fun takeItemAnyOwner(
        id: String,
        player: Player,
        amount: Int = 1,
        range: ManagerRange = ManagerRange.PUBLIC,
        slots: InventorySlotRange = InventorySlotRange.STORAGE
    ): Int = takeInventoryItems(id, player, amount, range, slots, verifyPrivateOwner = false, exactly = false)

    /**
     * 仅在数量充足时扣除全部 Sertraline 物品，不验证私有物品归属。
     * 数量不足或非正数量时不修改背包并返回 false；此同步方法必须在玩家所属线程调用。
     */
    fun takeItemAnyOwnerExactly(
        id: String,
        player: Player,
        amount: Int = 1,
        range: ManagerRange = ManagerRange.PUBLIC,
        slots: InventorySlotRange = InventorySlotRange.STORAGE
    ): Boolean = amount > 0 &&
        takeInventoryItems(id, player, amount, range, slots, verifyPrivateOwner = false, exactly = true) == amount

    /**
     * 重新生成 Sertraline 物品的 Lore
     * 会直接修改传入的物品
     * Re-generate lore for a Sertraline item
     * Will modify the input item in-place
     */
    fun rebuildLore(itemStack: ItemStack, player: Player?) {
        itemStack.rebuildLore(player)
    }

    /**
     * 重新生成 Sertraline 物品的显示名称
     * 会直接修改传入的物品
     * Re-generate display name for a Sertraline item
     * Will modify the input item in-place
     */
    fun rebuildName(itemStack: ItemStack, player: Player?) {
        itemStack.rebuildName(player)
    }

    /**
     * 重建整个 Sertraline 物品并返回
     * 不会修改传入的物品
     * Re-build entire Sertraline item and return a new ItemStack
     * Will NOT modify the input item
     */
    fun rebuild(itemStack: ItemStack, player: Player?): ItemStack {
        return itemStack.rebuild(player)
    }

    /**
     * 通过 ItemMeta 重建 Sertraline 物品并写入原物品
     * 会直接修改传入的物品
     * 注意：这是不安全的方法，会丢失部分 DataComponent（1.21.4 中 76 个组件丢失 3 个）
     * Re-build Sertraline item via ItemMeta and write to original item
     * Will modify the input item in-place
     * WARNING: Unsafe method — will lose some DataComponents (3 out of 76 in 1.21.4)
     */
    fun rebuildUnsafe(itemStack: ItemStack, player: Player?) {
        itemStack.rebuildUnsafe(player)
    }

    // ==================== 物品管理器 / Item Manager ====================
    // 支持公共/私有、持久/临时四种组合 / Supports public/private, persistent/temporary

    // ---- 公共物品 / Public Items ----

    /**
     * 创建公共物品
     * Create a public item
     */
    fun createPublicItem(id: String, data: Map<String, Any?>, sub: SubManagerType = SubManagerType.TEMPORARY) {
        Sertraline.manager.createItem(ManagerType.PUBLIC, sub, id, data)
    }

    /**
     * 获取公共物品
     * Get a public item
     */
    fun getPublicItem(id: String, sub: SubManagerType = SubManagerType.PERSISTENT): ModernSItem? {
        return Sertraline.manager.public.getItem(id, sub)
    }

    /**
     * 删除公共物品
     * Delete a public item
     */
    fun deletePublicItem(id: String, sub: SubManagerType): Boolean {
        return Sertraline.manager.public.remove(id, sub)
    }

    /**
     * 获取所有公共物品（指定子类型）
     * Get all public items by sub-type
     */
    fun getAllPublicItems(sub: SubManagerType): Map<String, ModernSItem> {
        return Sertraline.manager.public.getAll(sub)
    }

    // ---- 私人物品 / Private Items ----

    /**
     * 创建私人物品
     * Create a private item
     */
    fun createPrivateItem(uuid: String, id: String, data: Map<String, Any?>, sub: SubManagerType = SubManagerType.TEMPORARY) {
        Sertraline.manager.createItem(ManagerType.PRIVATE, sub, id, data, uuid)
    }

    /**
     * 获取私人物品
     * Get a private item
     */
    fun getPrivateItem(uuid: String, id: String, sub: SubManagerType = SubManagerType.PERSISTENT): ModernSItem? {
        return Sertraline.manager.privateManager.getItem(uuid, id, sub)
    }

    /**
     * 删除私人物品
     * Delete a private item
     */
    fun deletePrivateItem(uuid: String, id: String, sub: SubManagerType) {
        Sertraline.manager.deleteItem(ManagerType.PRIVATE, sub, id, uuid)
    }

    /**
     * 获取指定 UUID 的所有私人物品
     * Get all private items for the given UUID
     */
    fun getAllPrivateItems(uuid: String, sub: SubManagerType): Map<String, ModernSItem> {
        return Sertraline.manager.privateManager.getAll(uuid, sub)
    }

    // ---- 通用管理 / General Management ----

    /**
     * 直接注册物品到 itemMap（持久化公共物品）
     * Register an item directly into itemMap (public-persistent)
     */
    fun registerItem(id: String, item: ModernSItem) {
        Sertraline.itemMap[id] = item
    }

    /**
     * 从 itemMap 移除物品
     * Remove an item from itemMap
     * @return 被移除的物品，不存在则返回 null / The removed item, or null if not found
     */
    fun unregisterItem(id: String): ModernSItem? {
        return Sertraline.itemMap.remove(id)
    }

    /**
     * 获取 itemMap 中物品总数
     * Get total item count in itemMap
     */
    fun getItemCount(): Int = Sertraline.itemMap.size

    /**
     * 解析私有 UUID（自动兜底）
     * Resolve a private UUID with automatic fallback
     */
    fun resolvePrivateUuid(uuid: String?, playerUuid: String?): String {
        return Sertraline.manager.resolvePrivateUuid(uuid, playerUuid)
    }

    // ==================== 模板系统 / Template System ====================

    /**
     * 获取已加载的模板（不触发解析）
     * Get a loaded template by name (no parsing)
     */
    fun getTemplate(name: String): Map<String, Any?>? {
        return TemplateManager.getTemplate(name)
    }

    /**
     * 获取所有已加载的模板名
     * Get all loaded template names
     */
    fun getTemplateNames(): Set<String> {
        return TemplateManager.getTemplateNames()
    }

    /**
     * 获取所有已加载的模板（不可变快照）
     * Get all loaded templates (immutable snapshot)
     */
    fun getAllTemplates(): Map<String, Map<String, Any?>> {
        return TemplateManager.getAllTemplates()
    }

    /**
     * 手动解析模板：深拷贝 → 参数替换 → 递归解析
     * Manually resolve a template: deep-copy → param substitution → recursive resolve
     */
    fun resolveTemplate(name: String, args: Map<String, String>): Map<String, Any?>? {
        return TemplateManager.resolveTemplate(name, args)
    }

    /**
     * 获取已加载模板数量
     * Get the number of loaded templates
     */
    fun getTemplateCount(): Int = TemplateManager.templateCount()

    // ==================== 模板处理器注册 / Template Processor Registration ====================
    // 用于注册自定义 $t 变换器、$c 转换器和参数级指令
    // Register custom $t transformers, $c converters, and argument-level directives

    /**
     * 注册自定义 $t 变换器类型
     * Register a custom $t transformer type
     */
    fun registerTransformer(type: String, provider: TemplateManager.TransformerProvider) {
        TemplateManager.registerTransformer(type, provider)
    }

    /**
     * 注销自定义 $t 变换器类型
     * Unregister a custom $t transformer type
     */
    fun unregisterTransformer(type: String) {
        TemplateManager.unregisterTransformer(type)
    }

    /**
     * 注册自定义 $c 转换器类型
     * Register a custom $c converter type
     */
    fun registerConverter(type: String, provider: TemplateManager.ConverterProvider) {
        TemplateManager.registerConverter(type, provider)
    }

    /**
     * 注销自定义 $c 转换器类型
     * Unregister a custom $c converter type
     */
    fun unregisterConverter(type: String) {
        TemplateManager.unregisterConverter(type)
    }

    /**
     * 注册自定义参数级指令（与 $t/$c 同级，如 $myDirective）
     * Register a custom argument-level directive (same level as $t/$c, e.g. $myDirective)
     */
    fun registerDirective(name: String, provider: TemplateManager.DirectiveProvider) {
        TemplateManager.registerDirective(name, provider)
    }

    /**
     * 注销自定义参数级指令
     * Unregister a custom argument-level directive
     */
    fun unregisterDirective(name: String) {
        TemplateManager.unregisterDirective(name)
    }
}
