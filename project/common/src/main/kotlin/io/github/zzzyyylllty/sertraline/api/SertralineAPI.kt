package io.github.zzzyyylllty.sertraline.api

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.config.TemplateManager
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.function.data.getSertralineId
import io.github.zzzyyylllty.sertraline.item.itemSerializer
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
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag

public class SertralineAPIImpl : SertralineAPI {
}

public interface SertralineAPI {

    // ==================== Item Query & Build ====================

    /**
     * Get Sertraline Item Object
     */
    public fun getItem(s: String): ModernSItem? {
        return Sertraline.itemMap[s]
    }

    /**
     * Get Sertraline Item Object from ItemStack
     */
    public fun getItemFromStack(item: ItemStack): ModernSItem? {
        return Sertraline.itemMap[item.getSertralineId()]
    }

    /**
     * Get all registered items
     */
    public fun getAllItems(): Map<String, ModernSItem> {
        return Sertraline.itemMap
    }

    /**
     * Build an item for a player
     * @param [sItem] Sertraline item ID
     * @param [player] Player
     * @param [source] Source item, null for auto
     * @param [amount] Item amount
     * @param [overrideData] Override item data
     */
    public fun buildItem(sItem: String, player: Player?, source: ItemStack? = null, amount: Int = 1, overrideData: Map<String, Any?>? = null): ItemStack? {
        return sertralineItemBuilder(sItem, player, source, amount, overrideData)
    }

    /**
     * Build an item for a player with variables
     * @param [sItem] Sertraline item ID
     * @param [player] Player
     * @param [source] Source item, null for auto
     * @param [amount] Item amount
     * @param [overrideData] Override item data
     * @param [vars] Template variables (persisted to NBT)
     * @param [context] Runtime context (Player, Event, etc.), accessible via {context:xxx}, NOT persisted to NBT
     */
    public fun buildDataItem(sItem: String, player: Player?, source: ItemStack? = null, amount: Int = 1, overrideData: Map<String, Any?>? = null, vars: Map<String, Any?>? = null, context: Map<String, Any?>? = null): ItemStack? {
        return sertralineVarItemBuilder(sItem, player, source, amount, overrideData, vars, context)
    }

    /**
     * Get Sertraline ID from ItemStack, null if not a Sertraline item
     */
    public fun getId(itemStack: ItemStack): String? {
        return itemStack.getSertralineId()
    }

    /**
     * Get a val value from a Sertraline item object
     * @param [sItem] Sertraline item object
     * @param [valId] Val key
     * @return The val value, or null if not found
     */
    public fun getVal(sItem: ModernSItem, valId: String): Any? {
        return (sItem.getDeepData("sertraline:vals") as? Map<String, Any?>)?.get(valId)
    }

    /**
     * Get a val value from a Sertraline ItemStack
     * Reads the val by first resolving the underlying ModernSItem from the item (full serialization + tag processing)
     * @param [itemStack] Sertraline item
     * @param [valId] Val key
     * @param [player] Player context for dynamic/template resolution, required only when the item contains placeholders/dynamics
     * @return The val value, or null if the item is not a Sertraline item or the val is absent
     */
    public fun getValItemStack(itemStack: ItemStack, valId: String, player: Player? = null): Any? {
        val sItem = itemSerializer(itemStack, player) ?: return null
        return getVal(sItem, valId)
    }

    /**
     * Get a var value from a Sertraline ItemStack
     * Reads the persisted var value from the item's NBT
     * @param [itemStack] Sertraline item
     * @param [varId] Var key
     * @return The var value, or null if not found
     * @param [itemStack] Sertraline item
     * @param [varId] Var key
     * @return The var value, or null if not found
     */
    public fun getVar(itemStack: ItemStack, varId: String): Any? {
        return itemStack.getItemTag(true)["sertraline_data"]?.parseMapNBT()?.get(varId)
    }

    /**
     * Check if ItemStack is a Sertraline item
     */
    public fun isValidItem(itemStack: ItemStack): Boolean {
        return itemStack.getSertralineId() != null
    }

    /**
     * Check if ItemStack is registered in itemMap
     * `true` - Registered
     * `false` - Not registered
     */
    public fun isRegisteredItem(itemStack: ItemStack): Boolean {
        return Sertraline.itemMap[itemStack.getSertralineId()] != null
    }

    /**
     * Check if the given ID is registered in itemMap
     * `true` - Registered
     * `false` - Not registered
     */
    public fun isRegisteredItem(s: String): Boolean {
        return Sertraline.itemMap[s] != null
    }

    // ==================== Player Inventory Items ====================

    /**
     * Count Sertraline items in the player's inventory.
     * In private scope, verifies the ID currently belongs to the target player's private manager (temporary first, persistent fallback).
     * This synchronous method must be called on the player's thread.
     */
    fun countItem(
        id: String,
        player: Player,
        range: ManagerRange = ManagerRange.PUBLIC,
        slots: InventorySlotRange = InventorySlotRange.STORAGE
    ): Int = countInventoryItems(id, player, range, slots, verifyPrivateOwner = true)

    /**
     * Check whether the player's inventory has at least the specified amount of Sertraline items.
     * Non-positive amounts always return false; this synchronous method must be called on the player's thread.
     */
    fun hasItem(
        id: String,
        player: Player,
        amount: Int = 1,
        range: ManagerRange = ManagerRange.PUBLIC,
        slots: InventorySlotRange = InventorySlotRange.STORAGE
    ): Boolean = hasInventoryItems(id, player, amount, range, slots, verifyPrivateOwner = true)

    /**
     * Remove as many Sertraline items as possible from the player's inventory and return the amount actually removed.
     * Non-positive amounts return 0; this synchronous method must be called on the player's thread.
     */
    fun takeItem(
        id: String,
        player: Player,
        amount: Int = 1,
        range: ManagerRange = ManagerRange.PUBLIC,
        slots: InventorySlotRange = InventorySlotRange.STORAGE
    ): Int = takeInventoryItems(id, player, amount, range, slots, verifyPrivateOwner = true, exactly = false)

    /**
     * Only remove Sertraline items from the player's inventory when the full amount is available.
     * If the amount is insufficient or non-positive, the inventory is left unchanged and false is returned; this synchronous method must be called on the player's thread.
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
     * Count Sertraline items in the player's inventory, without verifying whether a private item belongs to that player.
     * This synchronous method must be called on the player's thread.
     */
    fun countItemAnyOwner(
        id: String,
        player: Player,
        range: ManagerRange = ManagerRange.PUBLIC,
        slots: InventorySlotRange = InventorySlotRange.STORAGE
    ): Int = countInventoryItems(id, player, range, slots, verifyPrivateOwner = false)

    /**
     * Check whether the player's inventory has at least the specified amount of Sertraline items, without verifying private-item ownership.
     * Non-positive amounts always return false; this synchronous method must be called on the player's thread.
     */
    fun hasItemAnyOwner(
        id: String,
        player: Player,
        amount: Int = 1,
        range: ManagerRange = ManagerRange.PUBLIC,
        slots: InventorySlotRange = InventorySlotRange.STORAGE
    ): Boolean = hasInventoryItems(id, player, amount, range, slots, verifyPrivateOwner = false)

    /**
     * Remove as many Sertraline items as possible from the player's inventory, without verifying private-item ownership.
     * Returns the amount actually removed; this synchronous method must be called on the player's thread.
     */
    fun takeItemAnyOwner(
        id: String,
        player: Player,
        amount: Int = 1,
        range: ManagerRange = ManagerRange.PUBLIC,
        slots: InventorySlotRange = InventorySlotRange.STORAGE
    ): Int = takeInventoryItems(id, player, amount, range, slots, verifyPrivateOwner = false, exactly = false)

    /**
     * Only deducts the full Sertraline item amount when it is sufficient, without verifying private-item ownership.
     * Returns false without touching the inventory when the amount is insufficient or non-positive; this synchronous method must be called on the player's thread.
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
     * Re-generate lore for a Sertraline item
     * Will modify the input item in-place
     */
    fun rebuildLore(itemStack: ItemStack, player: Player?) {
        itemStack.rebuildLore(player)
    }

    /**
     * Re-generate display name for a Sertraline item
     * Will modify the input item in-place
     */
    fun rebuildName(itemStack: ItemStack, player: Player?) {
        itemStack.rebuildName(player)
    }

    /**
     * Re-build entire Sertraline item and return a new ItemStack
     * Will NOT modify the input item
     */
    fun rebuild(itemStack: ItemStack, player: Player?): ItemStack {
        return itemStack.rebuild(player)
    }

    /**
     * Re-build Sertraline item via ItemMeta and write to original item
     * Will modify the input item in-place
     * WARNING: Unsafe method — will lose some DataComponents (3 out of 76 in 1.21.4)
     */
    fun rebuildUnsafe(itemStack: ItemStack, player: Player?) {
        itemStack.rebuildUnsafe(player)
    }

    // ==================== Item Manager ====================
    // Supports public/private, persistent/temporary

    // ---- Public Items ----

    /**
     * Create a public item
     */
    fun createPublicItem(id: String, data: Map<String, Any?>, sub: SubManagerType = SubManagerType.TEMPORARY) {
        Sertraline.manager.createItem(ManagerType.PUBLIC, sub, id, data)
    }

    /**
     * Get a public item
     */
    fun getPublicItem(id: String, sub: SubManagerType = SubManagerType.PERSISTENT): ModernSItem? {
        return Sertraline.manager.public.getItem(id, sub)
    }

    /**
     * Delete a public item
     */
    fun deletePublicItem(id: String, sub: SubManagerType): Boolean {
        return Sertraline.manager.public.remove(id, sub)
    }

    /**
     * Get all public items by sub-type
     */
    fun getAllPublicItems(sub: SubManagerType): Map<String, ModernSItem> {
        return Sertraline.manager.public.getAll(sub)
    }

    // ---- Private Items ----

    /**
     * Create a private item
     */
    fun createPrivateItem(uuid: String, id: String, data: Map<String, Any?>, sub: SubManagerType = SubManagerType.TEMPORARY) {
        Sertraline.manager.createItem(ManagerType.PRIVATE, sub, id, data, uuid)
    }

    /**
     * Get a private item
     */
    fun getPrivateItem(uuid: String, id: String, sub: SubManagerType = SubManagerType.PERSISTENT): ModernSItem? {
        return Sertraline.manager.privateManager.getItem(uuid, id, sub)
    }

    /**
     * Delete a private item
     */
    fun deletePrivateItem(uuid: String, id: String, sub: SubManagerType) {
        Sertraline.manager.deleteItem(ManagerType.PRIVATE, sub, id, uuid)
    }

    /**
     * Get all private items for the given UUID
     */
    fun getAllPrivateItems(uuid: String, sub: SubManagerType): Map<String, ModernSItem> {
        return Sertraline.manager.privateManager.getAll(uuid, sub)
    }

    // ---- General Management ----

    /**
     * Register an item directly into itemMap (public-persistent)
     */
    fun registerItem(id: String, item: ModernSItem) {
        Sertraline.itemMap[id] = item
    }

    /**
     * Remove an item from itemMap
     * @return The removed item, or null if not found
     */
    fun unregisterItem(id: String): ModernSItem? {
        return Sertraline.itemMap.remove(id)
    }

    /**
     * Get total item count in itemMap
     */
    fun getItemCount(): Int = Sertraline.itemMap.size

    /**
     * Resolve a private UUID with automatic fallback
     */
    fun resolvePrivateUuid(uuid: String?, playerUuid: String?): String {
        return Sertraline.manager.resolvePrivateUuid(uuid, playerUuid)
    }

    // ==================== Template System ====================

    /**
     * Get a loaded template by name (no parsing)
     */
    fun getTemplate(name: String): Map<String, Any?>? {
        return TemplateManager.getTemplate(name)
    }

    /**
     * Get all loaded template names
     */
    fun getTemplateNames(): Set<String> {
        return TemplateManager.getTemplateNames()
    }

    /**
     * Get all loaded templates (immutable snapshot)
     */
    fun getAllTemplates(): Map<String, Map<String, Any?>> {
        return TemplateManager.getAllTemplates()
    }

    /**
     * Manually resolve a template: deep-copy → param substitution → recursive resolve
     */
    fun resolveTemplate(name: String, args: Map<String, String>): Map<String, Any?>? {
        return TemplateManager.resolveTemplate(name, args)
    }

    /**
     * Get the number of loaded templates
     */
    fun getTemplateCount(): Int = TemplateManager.templateCount()

    // ==================== Template Processor Registration ====================
    // Register custom $t transformers, $c converters, and argument-level directives

    /**
     * Register a custom $t transformer type
     */
    fun registerTransformer(type: String, provider: TemplateManager.TransformerProvider) {
        TemplateManager.registerTransformer(type, provider)
    }

    /**
     * Unregister a custom $t transformer type
     */
    fun unregisterTransformer(type: String) {
        TemplateManager.unregisterTransformer(type)
    }

    /**
     * Register a custom $c converter type
     */
    fun registerConverter(type: String, provider: TemplateManager.ConverterProvider) {
        TemplateManager.registerConverter(type, provider)
    }

    /**
     * Unregister a custom $c converter type
     */
    fun unregisterConverter(type: String) {
        TemplateManager.unregisterConverter(type)
    }

    /**
     * Register a custom argument-level directive (same level as $t/$c, e.g. $myDirective)
     */
    fun registerDirective(name: String, provider: TemplateManager.DirectiveProvider) {
        TemplateManager.registerDirective(name, provider)
    }

    /**
     * Unregister a custom argument-level directive
     */
    fun unregisterDirective(name: String) {
        TemplateManager.unregisterDirective(name)
    }
}
