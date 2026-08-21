package io.github.zzzyyylllty.sertraline.compat

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.event.player.PlayerTradeEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import java.time.Duration
import org.bukkit.BanList
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.Block
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.command.CommandSender
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
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
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.File
import java.lang.reflect.Method
import java.util.UUID
import java.util.function.Consumer

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

    fun setCustomModelData(meta: ItemMeta, value: Int): ItemMeta {
        meta.setCustomModelData(value)
        return meta
    }

    fun getDisplayName(item: ItemStack): Component = item.displayName()

    // effectiveName() 是 Paper 新 API（"name shown to player in inventory"，1.21.4+ ITEM_NAME 优先、
    // 渲染不带 []，而 displayName/CUSTOM_NAME 渲染带白色 []），编译面 paper-api 1.21.4-R0.1 尚无此方法，
    // 反射调用，运行时缺失时降级为 itemName → displayName → 类型翻译名
    private val effectiveNameMethod: Method? by lazy {
        try { ItemStack::class.java.getMethod("effectiveName") } catch (_: Throwable) { null }
    }

    fun getEffectiveName(item: ItemStack): Component {
        return try {
            effectiveNameMethod?.invoke(item) as? Component ?: fallbackEffectiveName(item)
        } catch (_: Throwable) {
            fallbackEffectiveName(item)
        }
    }

    private fun fallbackEffectiveName(item: ItemStack): Component {
        return item.itemMeta?.let { it.itemName() ?: it.displayName() }
            ?: Component.translatable(item.type.translationKey())
    }

    fun getLore(item: ItemStack): List<Component>? = item.lore()

    fun setLore(item: ItemStack, components: List<Component>?): ItemStack {
        item.lore(components)
        return item
    }

    @Suppress("UNCHECKED_CAST")
    fun setDataComponent(item: ItemStack, typeName: String, value: Any): ItemStack {
        when (typeName) {
            "CUSTOM_NAME" -> item.setData(DataComponentTypes.CUSTOM_NAME, value as Component)
            // ITEM_NAME 1.21.4+ 渲染不带 [] 括号，与 effectiveName 语义配套
            "ITEM_NAME" -> item.setData(DataComponentTypes.ITEM_NAME, value as Component)
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

    fun createMerchant(): Merchant {
        return Bukkit.createMerchant()
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

    fun getStructureManager(): Any? = Bukkit.getStructureManager()

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

    // org.bukkit.profile.PlayerProfile（1.18.2+ API），与 createProfile 的 com.destroystokyo 版是不同类型
    fun createPlayerProfile(uuid: UUID?, name: String?): Any? =
        uuid?.let { if (name == null) Bukkit.createPlayerProfile(it) else Bukkit.createPlayerProfile(it, name) }

    fun createPlayerProfile(name: String): Any? = Bukkit.createPlayerProfile(name)

    // ── 脚本桥：1.13+ Bukkit API（与 spigot 源集同名方法签名一致） ──────────

    fun setMaxPlayers(value: Int): Unit? {
        Bukkit.setMaxPlayers(value)
        return Unit
    }

    fun getSimulationDistance(): Int? = Bukkit.getSimulationDistance()

    fun getMaxWorldSize(): Int? = Bukkit.getMaxWorldSize()

    fun isLoggingIPs(): Boolean? = Bukkit.isLoggingIPs()

    fun isWhitelistEnforced(): Boolean? = Bukkit.isWhitelistEnforced()

    fun setWhitelistEnforced(value: Boolean): Unit? {
        Bukkit.setWhitelistEnforced(value)
        return Unit
    }

    fun getTicksPerWaterSpawns(): Int? = Bukkit.getTicksPerWaterSpawns()

    fun getTicksPerWaterAmbientSpawns(): Int? = Bukkit.getTicksPerWaterAmbientSpawns()

    fun getTicksPerAmbientSpawns(): Int? = Bukkit.getTicksPerAmbientSpawns()

    fun createWorldBorder(): Any? = Bukkit.createWorldBorder()

    fun getRecipe(key: NamespacedKey): Any? = Bukkit.getRecipe(key)

    fun removeRecipe(key: NamespacedKey): Unit? {
        Bukkit.removeRecipe(key)
        return Unit
    }

    fun getBanListIP(): Any? = Bukkit.getBanList(BanList.Type.IP)

    fun getBanListProfile(): Any? = Bukkit.getBanList(BanList.Type.PROFILE)

    fun getBanListName(): Any? = Bukkit.getBanList(BanList.Type.NAME)

    fun createBossBar(key: NamespacedKey, title: String, color: BarColor, style: BarStyle, flags: Array<BarFlag>): BossBar? =
        Bukkit.createBossBar(key, title, color, style, *flags)

    fun getBossBars(): Any? = Bukkit.getBossBars()

    fun getBossBar(key: NamespacedKey): Any? = Bukkit.getBossBar(key)

    fun removeBossBar(key: NamespacedKey): Unit? {
        Bukkit.removeBossBar(key)
        return Unit
    }

    fun createBlockData(materialOrData: Any, consumerOrData: Any?): Any? {
        return when (materialOrData) {
            is org.bukkit.Material -> when (consumerOrData) {
                null -> Bukkit.createBlockData(materialOrData)
                is String -> Bukkit.createBlockData(materialOrData, consumerOrData)
                is Consumer<*> -> Bukkit.createBlockData(materialOrData, consumerOrData as Consumer<org.bukkit.block.data.BlockData>)
                else -> null
            }
            is String -> Bukkit.createBlockData(materialOrData)
            else -> null
        }
    }

    fun selectEntities(sender: CommandSender, selector: String): List<Entity>? = Bukkit.selectEntities(sender, selector)

    fun getLootTable(key: NamespacedKey): Any? = Bukkit.getLootTable(key)

    @Suppress("UNCHECKED_CAST")
    fun getRegistry(clazz: Class<*>): Any? = Bukkit.getRegistry(clazz as Class<out org.bukkit.Keyed>)

    @Suppress("UNCHECKED_CAST")
    fun getTag(registry: String, tagKey: NamespacedKey, clazz: Class<*>): Any? =
        Bukkit.getTag(registry, tagKey, clazz as Class<out org.bukkit.Keyed>)

    @Suppress("UNCHECKED_CAST")
    fun getTags(registry: String, clazz: Class<*>): Any? =
        Bukkit.getTags(registry, clazz as Class<out org.bukkit.Keyed>)

    fun setMotdLegacy(motd: String): Unit? {
        Bukkit.setMotd(motd)
        return Unit
    }

    fun fixMmoAttackSpeed(meta: ItemMeta): ItemMeta {
        meta.addAttributeModifier(
            Attribute.ATTACK_SPEED,
            AttributeModifier("mmoitems:decoy", 0.0, AttributeModifier.Operation.ADD_NUMBER)
        )
        return meta
    }

    fun addPotionEffect(player: Player, type: String, duration: Int, amplifier: Int, ambient: Boolean, particles: Boolean, icon: Boolean): Unit? {
        val effectType = PotionEffectType.getByName(type) ?: return Unit
        player.addPotionEffect(PotionEffect(effectType, duration, amplifier, ambient, particles, icon))
        return Unit
    }

    // ── 攻击信息（damageSource 1.13+ / AbstractArrow.weapon 1.16+） ─────────

    fun getDamageCausingPlayer(e: EntityDamageByEntityEvent): Player? = e.damageSource.causingEntity as? Player

    fun getDamageDirectEntity(e: EntityDamageByEntityEvent): Entity? = e.damageSource.directEntity

    fun getProjectileWeapon(entity: Entity?): ItemStack? = (entity as? AbstractArrow)?.weapon

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
