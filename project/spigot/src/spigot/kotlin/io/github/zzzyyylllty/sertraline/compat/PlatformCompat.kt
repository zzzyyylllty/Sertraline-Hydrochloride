package io.github.zzzyyylllty.sertraline.compat

import io.github.zzzyyylllty.embiancomponent.EmbianComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.ChatMessageType
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Merchant
import org.bukkit.inventory.SmithingInventory
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Level

/**
 * Sertraline 平台适配层（Spigot 实现，与 paper 源集的同名类二进制兼容）。
 * 仅依赖 Bukkit API + 捆绑的 adventure，零反射，纯字符串降级。
 * 同名方法与 Paper 实现保持完全一致的签名（common 代码按构建平台编译其中一个）。
 */
object PlatformCompat {

    val isPaper: Boolean by lazy {
        try {
            Class.forName("io.papermc.paper.ServerBuildInfo")
            true
        } catch (_: Throwable) {
            false
        }
    }

    // 不能用 legacySection()（adventure 4.17+ 默认不启用 hex，hex 会被降级为最近 16 色）；
    // 也不能用默认 hexColors()（输出 §#RRGGBB 简化格式，Spigot 服务端 CraftChatMessage
    // 的 legacy parser 不识别 §#，只会当字面文本）；必须用 §x 全格式，Spigot 与 Paper 服务端均支持
    private val LEGACY: LegacyComponentSerializer by lazy {
        LegacyComponentSerializer.builder()
            .character('§')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build()
    }
    private val warnedItemModel = AtomicBoolean(false)
    private val warnedRestart = AtomicBoolean(false)

    private fun warnOnce(flag: AtomicBoolean, feature: String) {
        if (flag.compareAndSet(false, true)) {
            Bukkit.getLogger().warning("[Sertraline] '$feature' is a Paper-only feature, ignored on this server type.")
        }
    }

    // ── 消息 ──────────────────────────────────────────────────────────────
    // 消息/ActionBar 走 bungee BaseComponent（hex 颜色、装饰、事件保真），不降级为 § 字符串；
    // Title 无 BaseComponent API 仍走 LEGACY（§x 全格式，hex 经服务端解析后保真）；
    // LEGACY serializer 仅留给 Title/ItemMeta/Inventory 等无 BaseComponent API 的地方

    fun sendComponent(sender: CommandSender, component: Component) {
        sender.spigot().sendMessage(BungeeComponentConverter.convert(component))
    }

    fun sendActionBar(player: Player, component: Component) {
        // spigot-api 1.21.4 已移除 sendActionBar(String)，仅存 bungee 旧 API
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, BungeeComponentConverter.convert(component))
    }

    fun sendTitle(player: Player, title: Component?, subtitle: Component?, fadeIn: Int, stay: Int, fadeOut: Int) {
        // spigot-api 1.21.4 无 BaseComponent 标题 API，只能走 String 重载；
        // LEGACY 为 §x 全格式，hex 颜色经 CraftChatMessage 解析后依然保真
        player.sendTitle(title?.let { LEGACY.serialize(it) }, subtitle?.let { LEGACY.serialize(it) }, fadeIn, stay, fadeOut)
    }

    fun kick(player: Player, component: Component?) {
        player.kickPlayer(component?.let { LEGACY.serialize(it) })
    }

    fun setPlayerDisplayName(player: Player, component: Component?) {
        player.setDisplayName(component?.let { LEGACY.serialize(it) })
    }

    fun setPlayerListName(player: Player, component: Component?) {
        player.setPlayerListName(component?.let { LEGACY.serialize(it) })
    }

    fun getPlayerListName(player: Player): Component? = LEGACY.deserialize(player.playerListName)

    // ── ItemMeta / ItemStack ──────────────────────────────────────────────

    fun setDisplayName(meta: ItemMeta, component: Component): ItemMeta {
        meta.setDisplayName(LEGACY.serialize(component))
        return meta
    }

    fun setCustomName(meta: ItemMeta, component: Component): ItemMeta {
        meta.setDisplayName(LEGACY.serialize(component))
        return meta
    }

    fun setItemName(meta: ItemMeta, component: Component): ItemMeta {
        meta.setDisplayName(LEGACY.serialize(component))
        return meta
    }

    fun setLore(meta: ItemMeta, components: List<Component>?): ItemMeta {
        meta.lore = components?.map { LEGACY.serialize(it) }
        return meta
    }

    fun setItemModel(meta: ItemMeta, key: NamespacedKey?): ItemMeta {
        warnOnce(warnedItemModel, "item model")
        return meta
    }

    fun getDisplayName(item: ItemStack): Component {
        return item.itemMeta?.displayName?.let { LEGACY.deserialize(it) }
            ?: Component.translatable(item.type.translationKey)
    }

    fun getLore(item: ItemStack): List<Component>? {
        return item.itemMeta?.lore?.map { LEGACY.deserialize(it) }
    }

    fun setLore(item: ItemStack, components: List<Component>?): ItemStack {
        val meta = item.itemMeta ?: return item
        meta.lore = components?.map { LEGACY.serialize(it) }
        item.setItemMeta(meta)
        return item
    }

    fun setDataComponent(item: ItemStack, typeName: String, value: Any): ItemStack {
        // data component 统一由 EmbianComponent 负责（NMS 反射路径，组件不存在时安全忽略）
        return EmbianComponent.SafetyComponentSetter.setComponent(item, typeName.lowercase(), value) ?: item
    }

    fun createInventory(owner: InventoryHolder?, type: InventoryType, component: Component): Inventory {
        return Bukkit.createInventory(owner, type, LEGACY.serialize(component))
    }

    fun createInventory(owner: InventoryHolder?, size: Int, component: Component): Inventory {
        return Bukkit.createInventory(owner, size, LEGACY.serialize(component))
    }

    fun createMerchant(component: Component?): Merchant {
        return Bukkit.createMerchant(component?.let { LEGACY.serialize(it) })
    }

    // ── 脚本桥：Server 的 Paper 扩展（Spigot 降级值） ──────────────────────

    fun getMinecraftVersion(): String? = Bukkit.getVersion().substringBefore("-")

    fun getTPS(): DoubleArray? = doubleArrayOf(-1.0, -1.0, -1.0)

    fun getAverageTickTime(): Double? = -1.0

    fun getCurrentTick(): Int? = -1

    fun getOfflinePlayerIfCached(name: String): OfflinePlayer? = Bukkit.getOfflinePlayer(name)

    fun getPlayerUniqueId(name: String): UUID? = Bukkit.getOfflinePlayer(name).uniqueId

    fun broadcast(component: Component): Unit? {
        Bukkit.broadcastMessage(LEGACY.serialize(component))
        return Unit
    }

    fun broadcast(component: Component, permission: String): Unit? {
        Bukkit.broadcast(LEGACY.serialize(component), permission)
        return Unit
    }

    fun getCommandMap(): Any? = null

    fun isTickingWorlds(): Boolean? = null

    fun getWorldByKey(key: NamespacedKey): World? =
        if (key.namespace == "minecraft") Bukkit.getWorld(key.key) else null

    fun updateResources(): Unit? = Unit

    fun getPluginsFolder(): File? = java.io.File(Bukkit.getWorldContainer(), "plugins")

    fun restart(): Unit? {
        // Spigot 无 restart API；shutdown 后由启动脚本重启（与 Paper 行为近似）
        warnOnce(warnedRestart, "server restart (falling back to shutdown)")
        Bukkit.shutdown()
        return Unit
    }

    fun isStopping(): Boolean? = false

    fun getMotdComponent(): Component? = null

    fun setMotdComponent(component: Component) {
        Bukkit.setMotd(LEGACY.serialize(component))
    }

    fun getShutdownMessage(): Component? = null

    fun getPermissionMessageComponent(): Component? = null

    fun getMobGoals(): Any? = null

    fun getDatapackManager(): Any? = null

    fun getPotionBrewer(): Any? = null

    fun getUnsafe(): Any? = null

    fun getVersionMessage(): String? = null

    fun getPermissionMessage(): String? = null

    fun reloadPermissions(): Unit? = Unit

    fun reloadCommandAliases(): Unit? = Unit

    fun suggestPlayerNamesWhenNullTabCompletions(): Boolean? = null

    fun createProfile(uuid: UUID?, name: String?): Any? =
        uuid?.let { if (name == null) Bukkit.createPlayerProfile(it) else Bukkit.createPlayerProfile(it, name) }

    fun createProfile(name: String): Any? = Bukkit.createPlayerProfile(name)

    fun createProfileExact(uuid: UUID?, name: String?): Any? = uuid?.let { createProfile(it, name) }

    // ── Folia 调度器（Spigot 上不存在） ────────────────────────────────────

    fun getRegionScheduler(): Any? = null

    fun getAsyncScheduler(): Any? = null

    fun getGlobalRegionScheduler(): Any? = null

    fun isGlobalTickThread(): Boolean? = false

    fun isOwnedByCurrentRegion(obj: Any, vararg args: Any?): Boolean = false

    // ── 事件 ──────────────────────────────────────────────────────────────

    fun callEvent(event: Event): Boolean {
        // spigot-api 的 PluginManager.callEvent 返回 void，paper 的 Event.callEvent 返回 boolean
        Bukkit.getPluginManager().callEvent(event)
        return true
    }

    fun getConsumeReplacement(event: PlayerItemConsumeEvent): ItemStack? = null

    fun getSmithingInputs(inventory: SmithingInventory): List<ItemStack> =
        inventory.contents.take(2).filterNotNull()

    fun registerPaperEvents(plugin: Plugin, hooks: PaperEventHooks) {
        Bukkit.getLogger().warning(
            "[Sertraline] PlayerArmorChangeEvent / PlayerTradeEvent are Paper-only. " +
                "Armor-change attribute refresh is disabled; enable 'attribute.armor-change-polling' in config.yml to simulate it."
        )
    }

    fun registerArmorPolling(plugin: Plugin, enabled: Boolean, onArmorChange: (Player) -> Unit) {
        if (!enabled) return
        val cache = ConcurrentHashMap<UUID, String>()
        plugin.server.scheduler.runTaskTimer(plugin, Runnable {
            val online = plugin.server.onlinePlayers
            if (online.isEmpty()) {
                cache.clear()
                return@Runnable
            }
            for (p in online) {
                val key = p.inventory.armorContents.joinToString("|") { item ->
                    "${item.type.name}:${item.amount}:${item.itemMeta?.hashCode()}"
                }
                val prev = cache.put(p.uniqueId, key)
                if (prev != null && prev != key) {
                    try {
                        onArmorChange(p)
                    } catch (e: Exception) {
                        plugin.logger.log(Level.WARNING, "Sertraline armor polling handler failed", e)
                    }
                }
            }
            cache.keys.removeIf { id -> plugin.server.getPlayer(id) == null }
        }, 20L, 20L)
    }
}
