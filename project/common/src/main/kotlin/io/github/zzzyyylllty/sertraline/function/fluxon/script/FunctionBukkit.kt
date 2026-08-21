package io.github.zzzyyylllty.sertraline.function.fluxon.script

import io.github.zzzyyylllty.sertraline.Sertraline.fluxonInst
import io.github.zzzyyylllty.sertraline.compat.PlatformCompat
import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.FunctionContext
import org.tabooproject.fluxon.runtime.NativeFunction.NativeCallable
import org.tabooproject.fluxon.runtime.java.Export
import org.tabooproject.fluxon.runtime.java.Optional
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import java.util.*
import org.bukkit.*
import org.bukkit.advancement.Advancement
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.*
import org.bukkit.plugin.Plugin
import java.io.File
import java.net.InetAddress
import java.util.*
import java.util.logging.Logger

@Awake(LifeCycle.ENABLE)
fun registerFunctionBukkit() {
    fluxonInst?.let { FunctionBukkit.init(it) }
}


object FunctionBukkit {
    fun init(runtime: FluxonRuntime) {
        runtime.registerFunction(
            "bukkit",
            0,
            NativeCallable { context: FunctionContext<Any?>? -> FluxonBukkitObject.INSTANCE })
        runtime.exportRegistry.registerClass(FluxonBukkitObject::class.java, "sertraline:bukkit")
    }

    @Suppress("UNUSED")
    class FluxonBukkitObject {

        @Export
        fun broadcast(arg: Any) {
            when (arg) {
                is String -> PlatformCompat.broadcast(mmUtil.deserialize(arg))
                is net.kyori.adventure.text.Component -> PlatformCompat.broadcast(arg)
                else -> throw IllegalArgumentException("Argument for broadcast must be a String or Component.")
            }
        }

        companion object {
            val INSTANCE: FluxonBukkitObject = FluxonBukkitObject()
        }
        // --- Server Information and Properties ---

        @Export
        fun getServer(): Server = Bukkit.getServer()

        @Export
        fun getPluginsFolder(): File = PlatformCompat.getPluginsFolder() ?: File("")

        @Export
        fun getName(): String = Bukkit.getName()

        @Export
        fun getVersion(): String = Bukkit.getVersion()

        @Export
        fun getBukkitVersion(): String = Bukkit.getBukkitVersion()

        @Export
        fun getMinecraftVersion(): String = PlatformCompat.getMinecraftVersion() ?: ""

        @Export
        fun getVersionMessage(): String = PlatformCompat.getVersionMessage() ?: ""

        // --- Player Management ---

        @Export
        fun getOnlinePlayers(): Collection<Player> = Bukkit.getOnlinePlayers()

        @Export
        fun getMaxPlayers(): Int = Bukkit.getMaxPlayers()

        @Export
        fun setMaxPlayers(max: Int) {
            PlatformCompat.setMaxPlayers(max)
        }

        @Export
        fun getPlayer(identifier: Any): Player? {
            return when (identifier) {
                is String -> Bukkit.getPlayer(identifier)
                is UUID -> Bukkit.getPlayer(identifier)
                else -> throw IllegalArgumentException("Argument for getPlayer must be a String (name) or UUID.")
            }
        }

        @Export
        fun getPlayerByName(name: String): Player? = Bukkit.getPlayer(name)

        @Export
        fun getPlayerByUUID(uuid: Any): Player? {
            return when (uuid) {
                is UUID -> Bukkit.getPlayer(uuid)
                is String -> Bukkit.getPlayer(UUID.fromString(uuid))
                else -> throw IllegalArgumentException("Argument for getPlayerByUUID must be a String or UUID.")
            }
        }

        @Export
        fun getPlayerExact(name: String): Player? = Bukkit.getPlayerExact(name)

        @Export
        fun matchPlayer(name: String): List<Player> = Bukkit.matchPlayer(name)

        @Export
        fun getPlayerUniqueId(name: String): UUID? = PlatformCompat.getPlayerUniqueId(name)

        // --- Server Configuration ---

        @Export
        fun getPort(): Int = Bukkit.getPort()

        @Export
        fun getViewDistance(): Int = Bukkit.getViewDistance()

        @Export
        fun getSimulationDistance(): Int = PlatformCompat.getSimulationDistance() ?: -1

        @Export
        fun getIp(): String = Bukkit.getIp()

        @Export
        fun getWorldType(): String = Bukkit.getWorldType()

        @Export
        fun getGenerateStructures(): Boolean = Bukkit.getGenerateStructures()

        @Export
        fun getMaxWorldSize(): Int = PlatformCompat.getMaxWorldSize() ?: 0

        @Export
        fun getAllowEnd(): Boolean = Bukkit.getAllowEnd()

        @Export
        fun getAllowNether(): Boolean = Bukkit.getAllowNether()

        @Export
        fun isLoggingIPs(): Boolean = PlatformCompat.isLoggingIPs() ?: false

        @Export
        fun getOnlineMode(): Boolean = Bukkit.getOnlineMode()

        @Export
        fun getAllowFlight(): Boolean = Bukkit.getAllowFlight()

        @Export
        fun isHardcore(): Boolean = Bukkit.isHardcore()

        @Export
        fun shutdown() {
            Bukkit.shutdown()
        }

        @Export
        fun restart() {
            PlatformCompat.restart()
        }

        // --- Broadcasting ---

        @Export
        fun broadcastPermission(message: Any, permission: String) {
            val message = message
            val componentMessage = when (message) {
                is String -> mmUtil.deserialize(message)
                is net.kyori.adventure.text.Component -> message
                else -> null
            }
            componentMessage?.let { PlatformCompat.broadcast(it, permission) }
        }

        @Export
        fun broadcastMessage(message: String) {
            Bukkit.broadcastMessage(message)
        }

        // --- Whitelist ---

        @Export
        fun hasWhitelist(): Boolean = Bukkit.hasWhitelist()

        @Export
        fun setWhitelist(value: Boolean) {
            Bukkit.setWhitelist(value)
        }

        @Export
        fun isWhitelistEnforced(): Boolean = PlatformCompat.isWhitelistEnforced() ?: false

        @Export
        fun setWhitelistEnforced(value: Boolean) {
            PlatformCompat.setWhitelistEnforced(value)
        }

        @Export
        fun getWhitelistedPlayers(): Set<OfflinePlayer> = Bukkit.getWhitelistedPlayers()

        @Export
        fun reloadWhitelist() {
            Bukkit.reloadWhitelist()
        }

        // --- Plugin and Services ---

        @Export
        fun getPluginManager() = Bukkit.getPluginManager()

        @Export
        fun getScheduler() = Bukkit.getScheduler()

        @Export
        fun getServicesManager() = Bukkit.getServicesManager()

        @Export
        fun getMessenger() = Bukkit.getMessenger()

        @Export
        fun getHelpMap() = Bukkit.getHelpMap()

        // --- Worlds ---

        @Export
        fun getWorlds(): List<World> = Bukkit.getWorlds()

        @Export
        fun isTickingWorlds(): Boolean = PlatformCompat.isTickingWorlds() ?: false

        @Export
        fun createWorld(creator: WorldCreator): World? = Bukkit.createWorld(creator)

        @Export
        fun unloadWorld(world: Any, save: Boolean): Boolean {
            return when (world) {
                is String -> Bukkit.unloadWorld(world, save)
                is World -> Bukkit.unloadWorld(world, save)
                else -> throw IllegalArgumentException("Argument for unloadWorld must be a String (name) or World object.")
            }
        }

        @Export
        fun getWorld(identifier: Any): World? {
            return when (identifier) {
                is String -> Bukkit.getWorld(identifier)
                is UUID -> Bukkit.getWorld(identifier)
                is NamespacedKey -> PlatformCompat.getWorldByKey(identifier)
                is net.kyori.adventure.key.Key -> PlatformCompat.getWorldByKey(NamespacedKey(identifier.namespace(), identifier.value()))
                else -> throw IllegalArgumentException("Argument for getWorld must be a String, UUID, NamespacedKey, or Key.")
            }
        }

        @Export
        fun getWorldContainer(): File = Bukkit.getWorldContainer()

        @Export
        fun createWorldBorder(): WorldBorder = PlatformCompat.createWorldBorder() as? WorldBorder
            ?: throw IllegalStateException("createWorldBorder is not supported on this server version.")

        // --- Commands ---

        @Export
        fun getPluginCommand(name: String) = Bukkit.getPluginCommand(name)

        @Export
        fun dispatchCommand(sender: CommandSender, commandLine: String): Boolean {
            return Bukkit.dispatchCommand(sender, commandLine)
        }

        @Export
        fun getCommandAliases(): Map<String, Array<String>> = Bukkit.getCommandAliases()

        @Export
        fun getConsoleSender() = Bukkit.getConsoleSender()

        @Export
        fun getCommandMap() = PlatformCompat.getCommandMap()

        // --- Recipes ---

        @Export
        fun addRecipe(recipe: Recipe?): Boolean = Bukkit.addRecipe(recipe)

        @Export
        fun getRecipesFor(result: ItemStack): List<Recipe> = Bukkit.getRecipesFor(result)

        @Export
        fun getRecipe(key: NamespacedKey): Recipe? = PlatformCompat.getRecipe(key) as? Recipe

        @Export
        fun recipeIterator(): Iterator<Recipe> = Bukkit.recipeIterator()

        @Export
        fun clearRecipes() {
            Bukkit.clearRecipes()
        }

        @Export
        fun resetRecipes() {
            Bukkit.resetRecipes()
        }

        @Export
        fun removeRecipe(key: NamespacedKey) {
            PlatformCompat.removeRecipe(key)
        }

        // --- Banning ---

        @Export
        fun getIPBans(): Set<String> = Bukkit.getIPBans()

        @Export
        fun banIP(address: Any) {
            when (address) {
                is String -> Bukkit.banIP(address)
                // banIP(InetAddress) 是 1.13+ API，legacy12（v11200）只有 String 版；统一转 hostAddress
                is InetAddress -> Bukkit.banIP(address.hostAddress)
                else -> throw IllegalArgumentException("Argument for banIP must be a String or InetAddress.")
            }
        }

        @Export
        fun unbanIP(address: Any) {
            when (address) {
                is String -> Bukkit.unbanIP(address)
                is InetAddress -> Bukkit.unbanIP(address.hostAddress)
                else -> throw IllegalArgumentException("Argument for unbanIP must be a String or InetAddress.")
            }
        }

        @Export
        fun getBannedPlayers(): Set<OfflinePlayer> = Bukkit.getBannedPlayers()

        // getBanList 泛型版 / BanList.Type.PROFILE / IpBanList 是 1.13+/1.20.2+ API，走 PlatformCompat 返回 Any?
        @Export
        fun getBanListIP(): Any? = PlatformCompat.getBanListIP()

        @Export
        fun getBanListProfile(): Any? = PlatformCompat.getBanListProfile()

        @Export
        fun getBanListName(): Any? = PlatformCompat.getBanListName()

        // --- Operators ---

        @Export
        fun getOperators(): Set<OfflinePlayer> = Bukkit.getOperators()

        // --- Offline Players ---

        @Export
        fun getOfflinePlayer(identifier: Any): OfflinePlayer {
            return when (identifier) {
                is String -> Bukkit.getOfflinePlayer(identifier)
                is UUID -> Bukkit.getOfflinePlayer(identifier)
                else -> throw IllegalArgumentException("Argument for getOfflinePlayer must be a String or UUID.")
            }
        }

        @Export
        fun getOfflinePlayerIfCached(name: String): OfflinePlayer? = PlatformCompat.getOfflinePlayerIfCached(name)

        @Export
        fun getOfflinePlayers(): Array<OfflinePlayer> = Bukkit.getOfflinePlayers()

        // --- Game Mode ---

        @Export
        fun getDefaultGameMode(): GameMode = Bukkit.getDefaultGameMode()

        @Export
        fun setDefaultGameMode(mode: GameMode) {
            Bukkit.setDefaultGameMode(mode)
        }

        // --- Inventory and Items ---

        @Export
        fun createInventory(owner: InventoryHolder?, typeOrSize: Any, @Optional title: Any? = null): Inventory {
            return when {
                title != null -> {
                    val componentTitle = when (title) {
                        is String -> mmUtil.deserialize(title)
                        is Component -> title
                        else -> throw IllegalArgumentException("Title must be a String or Component.")
                    }
                    when (typeOrSize) {
                        is InventoryType -> PlatformCompat.createInventory(owner, typeOrSize, componentTitle)
                        is Int -> PlatformCompat.createInventory(owner, typeOrSize, componentTitle)
                        else -> throw IllegalArgumentException("Second argument must be InventoryType or Integer size.")
                    }
                }
                else -> {
                    when (typeOrSize) {
                        is InventoryType -> Bukkit.createInventory(owner, typeOrSize)
                        is Int -> Bukkit.createInventory(owner, typeOrSize)
                        else -> throw IllegalArgumentException("Second argument must be InventoryType or Integer size.")
                    }
                }
            }
        }

        @Export
        fun createMerchant(title: Any?): Merchant {
            val componentTitle = when (title) {
                is String -> mmUtil.deserialize(title)
                is Component -> title
                null -> null
                else -> throw IllegalArgumentException("Title must be a String or Component.")
            }
            return PlatformCompat.createMerchant(componentTitle)
        }

        @Export
        fun getItemFactory(): ItemFactory = Bukkit.getItemFactory()

        // --- Server State & Reloading ---

        @Export
        fun reload() {
            Bukkit.reload()
        }

        @Export
        fun reloadData() {
            Bukkit.reloadData()
        }

        @Export
        fun updateResources() {
            PlatformCompat.updateResources()
        }

        @Export
        fun savePlayers() {
            Bukkit.savePlayers()
        }

        @Export
        fun isPrimaryThread(): Boolean = Bukkit.isPrimaryThread()

        @Export
        fun isStopping(): Boolean = PlatformCompat.isStopping() ?: false

        // --- MOTD and Server Icon ---

        @Export
        fun getLegacyMotd(): String = Bukkit.getMotd()

        @Export
        fun setLegacyMotd(motd: String) {
            PlatformCompat.setMotdLegacy(motd)
        }

        @Export
        fun motd(@Optional motd: Any? = null): net.kyori.adventure.text.Component? {
            return if (motd == null) {
                PlatformCompat.getMotdComponent()
            } else {
                val componentMotd = when (motd) {
                    is String -> mmUtil.deserialize(motd)
                    is Component -> motd
                    else -> throw IllegalArgumentException("MOTD must be a String or Component.")
                }
                PlatformCompat.setMotdComponent(componentMotd)
                null
            }
        }

        @Export
        fun getShutdownMessageLegacy(): String? = Bukkit.getShutdownMessage()

        @Export
        fun getShutdownMessage(): Component? = PlatformCompat.getShutdownMessage()

        @Export
        fun getServerIcon() = Bukkit.getServerIcon()

        @Export
        fun loadServerIcon(file: File) = Bukkit.loadServerIcon(file)

        // --- Boss Bars ---

        @Export
        fun createBossBar(keyOrTitle: Any, titleOrColor: Any, colorOrStyle: Any, styleOrFlags: Any? = null, flags: Array<BarFlag>? = null): BossBar? {
            return when (keyOrTitle) {
                // keyed 版是 1.16+ API，走 PlatformCompat（低版本降级为 String 版）
                is NamespacedKey -> {
                    val title = titleOrColor as String
                    val color = colorOrStyle as BarColor
                    val style = styleOrFlags as BarStyle
                    PlatformCompat.createBossBar(keyOrTitle, title, color, style, flags ?: emptyArray())
                }
                is String -> {
                    val title = keyOrTitle
                    val color = titleOrColor as BarColor
                    val style = colorOrStyle as BarStyle
                    val barFlags = styleOrFlags as? Array<BarFlag> ?: emptyArray()
                    Bukkit.createBossBar(title, color, style, *barFlags)
                }
                else -> throw IllegalArgumentException("Invalid arguments for createBossBar.")
            }
        }

        @Export
        fun getBossBars(): Any? = PlatformCompat.getBossBars()

        @Export
        fun getBossBar(key: NamespacedKey): Any? = PlatformCompat.getBossBar(key)

        @Export
        fun removeBossBar(key: NamespacedKey) {
            PlatformCompat.removeBossBar(key)
        }

        // --- BlockData ---

        @Export
        fun createBlockData(materialOrData: Any, @Optional consumerOrData: Any? = null): Any? {
            // BlockData 是 1.13+ API，走 PlatformCompat（低版本返回 null）
            return PlatformCompat.createBlockData(materialOrData, consumerOrData)
        }

        // --- Entity and Advancements ---

        @Export
        fun getEntity(uuid: UUID): Entity? = Bukkit.getEntity(uuid)

        @Export
        fun getAdvancement(key: NamespacedKey): Advancement? = Bukkit.getAdvancement(key)

        @Export
        fun advancementIterator(): Iterator<Advancement> = Bukkit.advancementIterator()

        @Export
        fun selectEntities(sender: CommandSender, selector: String): List<Entity> = PlatformCompat.selectEntities(sender, selector) ?: emptyList()

        // --- TPS and Ticks ---

        @Export
        fun getTPS(): DoubleArray = PlatformCompat.getTPS() ?: doubleArrayOf()

        @Export
        fun getAverageTickTime(): Double = PlatformCompat.getAverageTickTime() ?: -1.0

        @Export
        fun getCurrentTick(): Int = PlatformCompat.getCurrentTick() ?: -1

        // --- Miscellaneous ---

        @Export
        fun getLogger(): Logger = Bukkit.getLogger()

        @Export
        fun getScoreboardManager() = Bukkit.getScoreboardManager()

        @Export
        fun getStructureManager() = PlatformCompat.getStructureManager()

        @Export
        fun getLootTable(key: NamespacedKey): Any? = PlatformCompat.getLootTable(key)

        @Export
        fun getWarningState(): Warning.WarningState = Bukkit.getWarningState()

        // Folia Schedulers are handled dynamically at registration time if needed.
        // Assuming the new system can handle missing classes gracefully or has a conditional registration mechanism.

        // --- Deprecated or Unsafe Methods ---

        @Export
        fun getUnsafe() = PlatformCompat.getUnsafe()

        @Export
        fun getTicksPerAnimalSpawns(): Int = Bukkit.getTicksPerAnimalSpawns()

        @Export
        fun getTicksPerMonsterSpawns(): Int = Bukkit.getTicksPerMonsterSpawns()

        @Export
        fun getTicksPerWaterSpawns(): Int = PlatformCompat.getTicksPerWaterSpawns() ?: 0

        @Export
        fun getTicksPerWaterAmbientSpawns(): Int = PlatformCompat.getTicksPerWaterAmbientSpawns() ?: 0

        @Export
        fun getTicksPerAmbientSpawns(): Int = PlatformCompat.getTicksPerAmbientSpawns() ?: 0

        @Export
        fun getMonsterSpawnLimit(): Int = Bukkit.getMonsterSpawnLimit()

        @Export
        fun getAnimalSpawnLimit(): Int = Bukkit.getAnimalSpawnLimit()

        @Export
        fun getWaterAnimalSpawnLimit(): Int = Bukkit.getWaterAnimalSpawnLimit()

        @Export
        fun getAmbientSpawnLimit(): Int = Bukkit.getAmbientSpawnLimit()

        @Export
        // org.bukkit.profile.PlayerProfile 是 1.18.2+ 类，legacy12（v11200）编译面不存在；走 PlatformCompat 返回 Any?
        fun createPlayerProfile(uuid: UUID, @Optional name: String? = null): Any? {
            return PlatformCompat.createPlayerProfile(uuid, name)
        }

        // Registry / Tag 是 1.13+ API，legacy12（v11200）编译面不存在，走 PlatformCompat 返回 Any?
        @Export
        fun getRegistry(clazz: Class<out Keyed>): Any? = PlatformCompat.getRegistry(clazz)

        @Export
        fun getPermissionMessage(): String? = PlatformCompat.getPermissionMessage()

        // --- Tags ---

        @Export
        fun getTag(registry: String, tagKey: NamespacedKey, clazz: Class<out Keyed>): Any? {
            return PlatformCompat.getTag(registry, tagKey, clazz)
        }

        @Export
        fun getTags(registry: String, clazz: Class<out Keyed>): Any? {
            return PlatformCompat.getTags(registry, clazz)
        }

        // --- Paper-specific Profile Creation ---

        @Export
        fun createProfile(uuid: UUID, @Optional name: String? = null): Any? {
            return PlatformCompat.createProfile(uuid, name)
        }

        @Export
        fun createProfileExact(uuid: UUID, name: String?): Any? {
            return PlatformCompat.createProfileExact(uuid, name)
        }

        // --- Remaining Paper API functions ---

        @Export
        fun reloadPermissions() {
            PlatformCompat.reloadPermissions()
        }

        @Export
        fun reloadCommandAliases() {
            PlatformCompat.reloadCommandAliases()
        }

        @Export
        fun suggestPlayerNamesWhenNullTabCompletions(): Boolean? = PlatformCompat.suggestPlayerNamesWhenNullTabCompletions()

        @Export
        fun permissionMessage(): Component? = PlatformCompat.getPermissionMessageComponent()

        @Export
        fun getMobGoals() = PlatformCompat.getMobGoals()

        @Export
        fun getDatapackManager() = PlatformCompat.getDatapackManager()

        @Export
        fun getPotionBrewer() = PlatformCompat.getPotionBrewer()
    }

}
