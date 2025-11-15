package io.github.zzzyyylllty.sertraline.function.fluxon.script

import io.github.zzzyyylllty.sertraline.Sertraline.fluxonInst
import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import io.papermc.paper.ban.BanListType
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
import org.bukkit.ban.IpBanList
import org.bukkit.block.data.BlockData
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.boss.KeyedBossBar
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.*
import org.bukkit.loot.LootTable
import org.bukkit.plugin.Plugin
import java.io.File
import java.net.InetAddress
import java.util.*
import java.util.function.Consumer
import java.util.logging.Logger

@Awake(LifeCycle.ENABLE)
fun registerFunctionBukkit() {
    FunctionBukkit.init(fluxonInst)
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
                is String -> Bukkit.broadcast(mmUtil.deserialize(arg))
                is net.kyori.adventure.text.Component -> Bukkit.broadcast(arg)
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
        fun getPluginsFolder(): File = Bukkit.getPluginsFolder()

        @Export
        fun getName(): String = Bukkit.getName()

        @Export
        fun getVersion(): String = Bukkit.getVersion()

        @Export
        fun getBukkitVersion(): String = Bukkit.getBukkitVersion()

        @Export
        fun getMinecraftVersion(): String = Bukkit.getMinecraftVersion() // Paper API

        @Export
        fun getVersionMessage(): String = Bukkit.getVersionMessage()

        // --- Player Management ---

        @Export
        fun getOnlinePlayers(): Collection<Player> = Bukkit.getOnlinePlayers()

        @Export
        fun getMaxPlayers(): Int = Bukkit.getMaxPlayers()

        @Export
        fun setMaxPlayers(max: Int) {
            Bukkit.setMaxPlayers(max)
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
        fun getPlayerUniqueId(name: String): UUID? = Bukkit.getPlayerUniqueId(name) // Paper API

        // --- Server Configuration ---

        @Export
        fun getPort(): Int = Bukkit.getPort()

        @Export
        fun getViewDistance(): Int = Bukkit.getViewDistance()

        @Export
        fun getSimulationDistance(): Int = Bukkit.getSimulationDistance()

        @Export
        fun getIp(): String = Bukkit.getIp()

        @Export
        fun getWorldType(): String = Bukkit.getWorldType()

        @Export
        fun getGenerateStructures(): Boolean = Bukkit.getGenerateStructures()

        @Export
        fun getMaxWorldSize(): Int = Bukkit.getMaxWorldSize()

        @Export
        fun getAllowEnd(): Boolean = Bukkit.getAllowEnd()

        @Export
        fun getAllowNether(): Boolean = Bukkit.getAllowNether()

        @Export
        fun isLoggingIPs(): Boolean = Bukkit.isLoggingIPs()

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
            Bukkit.restart() // Spigot API
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
            componentMessage?.let { Bukkit.broadcast(it, permission) }
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
        fun isWhitelistEnforced(): Boolean = Bukkit.isWhitelistEnforced()

        @Export
        fun setWhitelistEnforced(value: Boolean) {
            Bukkit.setWhitelistEnforced(value)
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
        fun isTickingWorlds(): Boolean = Bukkit.isTickingWorlds() // Paper API

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
                is NamespacedKey -> Bukkit.getWorld(identifier) // Paper API
                is net.kyori.adventure.key.Key -> Bukkit.getWorld(identifier) // Paper API
                else -> throw IllegalArgumentException("Argument for getWorld must be a String, UUID, NamespacedKey, or Key.")
            }
        }

        @Export
        fun getWorldContainer(): File = Bukkit.getWorldContainer()

        @Export
        fun createWorldBorder(): WorldBorder = Bukkit.createWorldBorder()

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
        fun getCommandMap() = Bukkit.getCommandMap() // Paper API

        // --- Recipes ---

        @Export
        fun addRecipe(recipe: Recipe?): Boolean = Bukkit.addRecipe(recipe)

        @Export
        fun getRecipesFor(result: ItemStack): List<Recipe> = Bukkit.getRecipesFor(result)

        @Export
        fun getRecipe(key: NamespacedKey): Recipe? = Bukkit.getRecipe(key)

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
        fun removeRecipe(key: NamespacedKey): Boolean = Bukkit.removeRecipe(key)

        // --- Banning ---

        @Export
        fun getIPBans(): Set<String> = Bukkit.getIPBans()

        @Export
        fun banIP(address: Any) {
            when (address) {
                is String -> Bukkit.banIP(address)
                is InetAddress -> Bukkit.banIP(address)
                else -> throw IllegalArgumentException("Argument for banIP must be a String or InetAddress.")
            }
        }

        @Export
        fun unbanIP(address: Any) {
            when (address) {
                is String -> Bukkit.unbanIP(address)
                is InetAddress -> Bukkit.unbanIP(address)
                else -> throw IllegalArgumentException("Argument for unbanIP must be a String or InetAddress.")
            }
        }

        @Export
        fun getBannedPlayers(): Set<OfflinePlayer> = Bukkit.getBannedPlayers()

        @Export
        fun getBanListIP(): IpBanList {
            return Bukkit.getBanList<IpBanList>(BanList.Type.IP)
        }
        @Export
        fun getBanListProfile(): IpBanList {
            return Bukkit.getBanList<IpBanList>(BanList.Type.PROFILE)
        }
        @Export
        fun getBanListName(): IpBanList {
            return Bukkit.getBanList<IpBanList>(BanList.Type.NAME)
        }

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
        fun getOfflinePlayerIfCached(name: String): OfflinePlayer? = Bukkit.getOfflinePlayerIfCached(name) // Paper API

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
                        is InventoryType -> Bukkit.createInventory(owner, typeOrSize, componentTitle)
                        is Int -> Bukkit.createInventory(owner, typeOrSize, componentTitle)
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
            return Bukkit.createMerchant(componentTitle)
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
            Bukkit.updateResources() // Paper API
        }

        @Export
        fun savePlayers() {
            Bukkit.savePlayers()
        }

        @Export
        fun isPrimaryThread(): Boolean = Bukkit.isPrimaryThread()

        @Export
        fun isStopping(): Boolean = Bukkit.isStopping() // Paper API

        // --- MOTD and Server Icon ---

        @Export
        fun getLegacyMotd(): String = Bukkit.getMotd()

        @Export
        fun setLegacyMotd(motd: String) {
            Bukkit.setMotd(motd)
        }

        @Export
        fun motd(@Optional motd: Any? = null): net.kyori.adventure.text.Component? {
            return if (motd == null) {
                Bukkit.motd()
            } else {
                val componentMotd = when (motd) {
                    is String -> mmUtil.deserialize(motd)
                    is Component -> motd
                    else -> throw IllegalArgumentException("MOTD must be a String or Component.")
                }
                Bukkit.motd(componentMotd)
                null
            }
        }

        @Export
        fun getShutdownMessageLegacy(): String? = Bukkit.getShutdownMessage()

        @Export
        fun getShutdownMessage(): Component? = Bukkit.shutdownMessage() // Paper API

        @Export
        fun getServerIcon() = Bukkit.getServerIcon()

        @Export
        fun loadServerIcon(file: File) = Bukkit.loadServerIcon(file)

        // --- Boss Bars ---

        @Export
        fun createBossBar(keyOrTitle: Any, titleOrColor: Any, colorOrStyle: Any, styleOrFlags: Any? = null, flags: Array<BarFlag>? = null): BossBar {
            return when (keyOrTitle) {
                is NamespacedKey -> {
                    val key = keyOrTitle
                    val title = titleOrColor as String
                    val color = colorOrStyle as BarColor
                    val style = styleOrFlags as BarStyle
                    Bukkit.createBossBar(key, title, color, style, *(flags ?: emptyArray()))
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
        fun getBossBars(): Iterator<KeyedBossBar> = Bukkit.getBossBars()

        @Export
        fun getBossBar(key: NamespacedKey): KeyedBossBar? = Bukkit.getBossBar(key)

        @Export
        fun removeBossBar(key: NamespacedKey) {
            Bukkit.removeBossBar(key)
        }

        // --- BlockData ---

        @Export
        fun createBlockData(materialOrData: Any,@Optional  consumerOrData: Any? = null): BlockData {
            return when(materialOrData) {
                is Material -> {
                    when(consumerOrData) {
                        null -> Bukkit.createBlockData(materialOrData)
                        is Consumer<*> -> Bukkit.createBlockData(materialOrData, consumerOrData as Consumer<BlockData>)
                        is String -> Bukkit.createBlockData(materialOrData, consumerOrData)
                        else -> throw IllegalArgumentException("Second argument for createBlockData must be a Consumer or String.")
                    }
                }
                is String -> Bukkit.createBlockData(materialOrData)
                else -> throw IllegalArgumentException("First argument for createBlockData must be Material or String.")
            }
        }

        // --- Entity and Advancements ---

        @Export
        fun getEntity(uuid: UUID): Entity? = Bukkit.getEntity(uuid)

        @Export
        fun getAdvancement(key: NamespacedKey): Advancement? = Bukkit.getAdvancement(key)

        @Export
        fun advancementIterator(): Iterator<Advancement> = Bukkit.advancementIterator()

        @Export
        fun selectEntities(sender: CommandSender, selector: String): List<Entity> = Bukkit.selectEntities(sender, selector)

        // --- TPS and Ticks ---

        @Export
        fun getTPS(): DoubleArray = Bukkit.getTPS() // Paper API

        @Export
        fun getAverageTickTime(): Double = Bukkit.getAverageTickTime() // Paper API

        @Export
        fun getCurrentTick(): Int = Bukkit.getCurrentTick() // Paper API

        // --- Miscellaneous ---

        @Export
        fun getLogger(): Logger = Bukkit.getLogger()

        @Export
        fun getScoreboardManager() = Bukkit.getScoreboardManager()

        @Export
        fun getStructureManager() = Bukkit.getStructureManager()

        @Export
        fun getLootTable(key: NamespacedKey): LootTable? = Bukkit.getLootTable(key)

        @Export
        fun getWarningState(): Warning.WarningState = Bukkit.getWarningState()

        // Folia Schedulers are handled dynamically at registration time if needed.
        // Assuming the new system can handle missing classes gracefully or has a conditional registration mechanism.

        // --- Deprecated or Unsafe Methods ---

        @Export
        fun getUnsafe() = Bukkit.getUnsafe()

        @Export
        fun getTicksPerAnimalSpawns(): Int = Bukkit.getTicksPerAnimalSpawns()

        @Export
        fun getTicksPerMonsterSpawns(): Int = Bukkit.getTicksPerMonsterSpawns()

        @Export
        fun getTicksPerWaterSpawns(): Int = Bukkit.getTicksPerWaterSpawns()

        @Export
        fun getTicksPerWaterAmbientSpawns(): Int = Bukkit.getTicksPerWaterAmbientSpawns()

        @Export
        fun getTicksPerAmbientSpawns(): Int = Bukkit.getTicksPerAmbientSpawns()

        @Export
        fun getMonsterSpawnLimit(): Int = Bukkit.getMonsterSpawnLimit()

        @Export
        fun getAnimalSpawnLimit(): Int = Bukkit.getAnimalSpawnLimit()

        @Export
        fun getWaterAnimalSpawnLimit(): Int = Bukkit.getWaterAnimalSpawnLimit()

        @Export
        fun getAmbientSpawnLimit(): Int = Bukkit.getAmbientSpawnLimit()

        @Export
        fun createPlayerProfile(uuid: UUID,@Optional name: String? = null): org.bukkit.profile.PlayerProfile {
            return if (name == null) Bukkit.createPlayerProfile(uuid) else Bukkit.createPlayerProfile(uuid, name)
        }

        @Export
        fun getRegistry(clazz: Class<out Keyed>): Registry<out Keyed>? = Bukkit.getRegistry(clazz)

        @Export
        fun getPermissionMessage(): String = Bukkit.getPermissionMessage()

        // --- Tags ---

        @Export
        fun <T : Keyed> getTag(registry: String, tagKey: NamespacedKey, clazz: Class<T>): Tag<T>? {
            return Bukkit.getTag(registry, tagKey, clazz)
        }

        @Export
        fun <T : Keyed> getTags(registry: String, clazz: Class<T>): Iterable<Tag<T?>?> {
            return Bukkit.getTags(registry, clazz)
        }

        // --- Paper-specific Profile Creation ---

        @Export
        fun createProfile(uuid: UUID, @Optional name: String? = null): com.destroystokyo.paper.profile.PlayerProfile {
            return if (name == null) Bukkit.createProfile(uuid) else Bukkit.createProfile(uuid, name)
        }

        @Export
        fun createProfileExact(uuid: UUID, name: String?): com.destroystokyo.paper.profile.PlayerProfile {
            return Bukkit.createProfileExact(uuid, name)
        }

        // --- Remaining Paper API functions ---

        @Export
        fun reloadPermissions() {
            Bukkit.reloadPermissions()
        }

        @Export
        fun reloadCommandAliases() {
            Bukkit.reloadCommandAliases()
        }

        @Export
        fun suggestPlayerNamesWhenNullTabCompletions(): Boolean = Bukkit.suggestPlayerNamesWhenNullTabCompletions()

        @Export
        fun permissionMessage(): Component = Bukkit.permissionMessage()

        @Export
        fun getMobGoals() = Bukkit.getMobGoals()

        @Export
        fun getDatapackManager() = Bukkit.getDatapackManager()

        @Export
        fun getPotionBrewer() = Bukkit.getPotionBrewer()
    }

}
