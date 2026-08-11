package io.github.zzzyyylllty.sertraline.compat

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.event.player.PlayerTradeEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import java.time.Duration
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Merchant
import org.bukkit.inventory.SmithingInventory
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.UUID

/**
 * Sertraline 平台适配层（Paper 实现，与 main 源集的同名类签名完全一致）。
 * 所有方法直接调用 Paper API，零反射，零降级。
 */
object PlatformCompat {

    val isPaper: Boolean = true

    // ── 消息 ──────────────────────────────────────────────────────────────

    fun sendComponent(sender: CommandSender, component: Component) {
        (sender as Audience).sendMessage(component)
    }

    fun sendActionBar(player: Player, component: Component) {
        player.sendActionBar(component)
    }

    fun sendTitle(player: Player, title: Component?, subtitle: Component?, fadeIn: Int, stay: Int, fadeOut: Int) {
        // adventure 4.18+ 移除了 sendTitle(Component, Component, int, int, int)，
        // 统一走 Audience.showTitle(Title)；fade 参数单位为 tick
        player.showTitle(
            Title.title(
                title ?: Component.empty(),
                subtitle ?: Component.empty(),
                Title.Times.times(
                    Duration.ofMillis(fadeIn * 50L),
                    Duration.ofMillis(stay * 50L),
                    Duration.ofMillis(fadeOut * 50L)
                )
            )
        )
    }

    fun kick(player: Player, component: Component?) {
        player.kick(component)
    }

    fun setPlayerDisplayName(player: Player, component: Component?) {
        player.displayName(component)
    }

    fun setPlayerListName(player: Player, component: Component?) {
        player.playerListName(component)
    }

    fun getPlayerListName(player: Player): Component? = player.playerListName()

    // ── ItemMeta / ItemStack ──────────────────────────────────────────────

    fun setDisplayName(meta: ItemMeta, component: Component): ItemMeta {
        meta.displayName(component)
        return meta
    }

    fun setCustomName(meta: ItemMeta, component: Component): ItemMeta {
        meta.customName(component)
        return meta
    }

    fun setItemName(meta: ItemMeta, component: Component): ItemMeta {
        meta.itemName(component)
        return meta
    }

    fun setLore(meta: ItemMeta, components: List<Component>?): ItemMeta {
        meta.lore(components)
        return meta
    }

    fun setItemModel(meta: ItemMeta, key: NamespacedKey?): ItemMeta {
        meta.itemModel = key
        return meta
    }

    fun getDisplayName(item: ItemStack): Component = item.displayName()

    fun getLore(item: ItemStack): List<Component>? = item.lore()

    fun setLore(item: ItemStack, components: List<Component>?): ItemStack {
        item.lore(components)
        return item
    }

    @Suppress("UNCHECKED_CAST")
    fun setDataComponent(item: ItemStack, typeName: String, value: Any): ItemStack {
        when (typeName) {
            "CUSTOM_NAME" -> item.setData(DataComponentTypes.CUSTOM_NAME, value as Component)
            else -> throw IllegalArgumentException("Unsupported data component type: $typeName")
        }
        return item
    }

    fun createInventory(owner: InventoryHolder?, type: InventoryType, component: Component): Inventory {
        return Bukkit.createInventory(owner, type, component)
    }

    fun createInventory(owner: InventoryHolder?, size: Int, component: Component): Inventory {
        return Bukkit.createInventory(owner, size, component)
    }

    fun createMerchant(component: Component?): Merchant {
        return Bukkit.createMerchant(component)
    }

    // ── 脚本桥：Server 的 Paper 扩展 ───────────────────────────────────────

    fun getMinecraftVersion(): String? = Bukkit.getMinecraftVersion()

    fun getTPS(): DoubleArray? = Bukkit.getTPS()

    fun getAverageTickTime(): Double? = Bukkit.getAverageTickTime()

    fun getCurrentTick(): Int? = Bukkit.getCurrentTick()

    fun getOfflinePlayerIfCached(name: String): OfflinePlayer? = Bukkit.getOfflinePlayerIfCached(name)

    fun getPlayerUniqueId(name: String): UUID? = Bukkit.getPlayerUniqueId(name)

    fun broadcast(component: Component): Unit? {
        Bukkit.broadcast(component)
        return Unit
    }

    fun broadcast(component: Component, permission: String): Unit? {
        Bukkit.broadcast(component, permission)
        return Unit
    }

    fun getCommandMap(): Any? = Bukkit.getCommandMap()

    fun isTickingWorlds(): Boolean? = Bukkit.isTickingWorlds()

    fun getWorldByKey(key: NamespacedKey): World? = Bukkit.getWorld(key)

    fun updateResources(): Unit? {
        Bukkit.updateResources()
        return Unit
    }

    fun getPluginsFolder(): File? = Bukkit.getPluginsFolder()

    fun restart(): Unit? {
        Bukkit.restart()
        return Unit
    }

    fun isStopping(): Boolean? = Bukkit.isStopping()

    fun getMotdComponent(): Component? = Bukkit.motd()

    fun setMotdComponent(component: Component) {
        Bukkit.motd(component)
    }

    fun getShutdownMessage(): Component? = Bukkit.shutdownMessage()

    fun getPermissionMessageComponent(): Component? = Bukkit.permissionMessage()

    fun getMobGoals(): Any? = Bukkit.getMobGoals()

    fun getDatapackManager(): Any? = Bukkit.getDatapackManager()

    fun getPotionBrewer(): Any? = Bukkit.getPotionBrewer()

    fun getUnsafe(): Any? = Bukkit.getUnsafe()

    fun getVersionMessage(): String? = Bukkit.getVersionMessage()

    fun getPermissionMessage(): String? = Bukkit.getPermissionMessage()

    fun reloadPermissions(): Unit? {
        Bukkit.reloadPermissions()
        return Unit
    }

    fun reloadCommandAliases(): Unit? {
        Bukkit.reloadCommandAliases()
        return Unit
    }

    fun suggestPlayerNamesWhenNullTabCompletions(): Boolean? = Bukkit.suggestPlayerNamesWhenNullTabCompletions()

    fun createProfile(uuid: UUID?, name: String?): Any? =
        uuid?.let { if (name == null) Bukkit.createProfile(it) else Bukkit.createProfile(it, name) }

    fun createProfile(name: String): Any? = Bukkit.createProfile(name)

    fun createProfileExact(uuid: UUID?, name: String?): Any? = uuid?.let { Bukkit.createProfileExact(it, name) }

    // ── Folia 调度器 ──────────────────────────────────────────────────────

    fun getRegionScheduler(): Any? = Bukkit.getRegionScheduler()

    fun getAsyncScheduler(): Any? = Bukkit.getAsyncScheduler()

    fun getGlobalRegionScheduler(): Any? = Bukkit.getGlobalRegionScheduler()

    fun isGlobalTickThread(): Boolean? = Bukkit.isGlobalTickThread()

    fun isOwnedByCurrentRegion(obj: Any, vararg args: Any?): Boolean = when (obj) {
        is Location -> when (args.size) {
            0 -> Bukkit.isOwnedByCurrentRegion(obj)
            1 -> Bukkit.isOwnedByCurrentRegion(obj, (args[0] as Number).toInt())
            else -> throw IllegalArgumentException("Invalid argument count for isOwnedByCurrentRegion with Location.")
        }
        is Entity -> Bukkit.isOwnedByCurrentRegion(obj)
        is Block -> Bukkit.isOwnedByCurrentRegion(obj)
        is World -> when (args.size) {
            2 -> Bukkit.isOwnedByCurrentRegion(obj, (args[0] as Number).toInt(), (args[1] as Number).toInt())
            3 -> Bukkit.isOwnedByCurrentRegion(obj, (args[0] as Number).toInt(), (args[1] as Number).toInt(), (args[2] as Number).toInt())
            4 -> Bukkit.isOwnedByCurrentRegion(
                obj,
                (args[0] as Number).toInt(),
                (args[1] as Number).toInt(),
                (args[2] as Number).toInt(),
                (args[3] as Number).toInt()
            )
            else -> throw IllegalArgumentException("Invalid argument count for isOwnedByCurrentRegion with World.")
        }
        else -> throw IllegalArgumentException("Unsupported type for isOwnedByCurrentRegion: ${obj.javaClass.name}")
    }

    // ── 事件（Paper 专属，直接注册真实事件） ────────────────────────────────

    fun callEvent(event: Event): Boolean = event.callEvent()

    fun getConsumeReplacement(event: PlayerItemConsumeEvent): ItemStack? = event.replacement

    fun getSmithingInputs(inventory: SmithingInventory): List<ItemStack> =
        listOfNotNull(inventory.inputEquipment, inventory.inputMineral)

    fun registerPaperEvents(plugin: Plugin, hooks: PaperEventHooks) {
        plugin.server.pluginManager.registerEvents(object : Listener {
            @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
            fun onArmor(e: PlayerArmorChangeEvent) {
                hooks.onArmorChange(e.player)
            }

            @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
            fun onTrade(e: PlayerTradeEvent) {
                if (hooks.onPlayerTrade(e.trade.ingredients)) {
                    e.isCancelled = true
                }
            }
        }, plugin)
    }

    fun registerArmorPolling(plugin: Plugin, enabled: Boolean, onArmorChange: (Player) -> Unit) {
        // Paper 有真实事件，无需轮询
    }
}
