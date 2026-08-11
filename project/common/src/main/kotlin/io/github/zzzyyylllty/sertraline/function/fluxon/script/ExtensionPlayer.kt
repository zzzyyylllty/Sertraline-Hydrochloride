package io.github.zzzyyylllty.sertraline.function.fluxon.script

import io.github.zzzyyylllty.sertraline.Sertraline.fluxonInst
import io.github.zzzyyylllty.sertraline.compat.PlatformCompat
import io.github.zzzyyylllty.sertraline.function.fluxon.script.FunctionBukkit.FluxonBukkitObject
import io.github.zzzyyylllty.sertraline.util.minimessage.mmJsonUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmLegacyAmpersandUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmLegacySectionUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmStrictUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.FunctionContext
import org.tabooproject.fluxon.runtime.NativeFunction.NativeCallable
import java.util.*
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake


@Awake(LifeCycle.ENABLE)
fun registerExtensionPlayer() {
    fluxonInst?.let { ExtensionPlayer.init(it) }
}

object ExtensionPlayer {

    fun init(runtime: FluxonRuntime) {
        runtime.registerExtension(Player::class.java)
            // sendMessage(String | Component)
            .function("sendMessage", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val p: Player = Objects.requireNonNull<Player>(context!!.getTarget())
                when (val arg = context.getArgument(0)) {
                    is String -> PlatformCompat.sendComponent(p, mmUtil.deserialize(arg))
                    is Component -> PlatformCompat.sendComponent(p, arg)
                    else -> throw IllegalArgumentException("Argument for sendMessage must be a String or Component.")
                }
            })

            // sendComponentMessage(String | Component) - 推荐使用这个，语义更清晰
            .function("sendComponentMessage", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val p: Player = Objects.requireNonNull<Player>(context!!.getTarget())
                when (val arg = context.getArgument(0)) {
                    is String -> PlatformCompat.sendComponent(p, mmUtil.deserialize(arg))
                    is Component -> PlatformCompat.sendComponent(p, arg)
                    else -> throw IllegalArgumentException("Argument for sendComponentMessage must be a String or Component.")
                }
            })

            .function("sendLegacyMessage", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val p: Player = Objects.requireNonNull<Player>(context!!.getTarget())
                val arg = context.getString(0)
                arg?.let { p.sendMessage(it) }
            })

            // getName() -> String
            .function("getName", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.name // 使用属性访问
            })

            // getDisplayName() -> String (Legacy)
            .function("getLegacyDisplayName", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.getDisplayName()
            })

            // setDisplayName(String) -> void (Legacy)
            .function("setLegacyDisplayName", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val name = context.getString(0)
                player.setDisplayName(name)
            })

            // displayName(Component) -> void
            .function("setDisplayName", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                when (val arg = context.getArgument(0)) {
                    is String -> PlatformCompat.setPlayerDisplayName(player, mmUtil.deserialize(arg))
                    is Component -> PlatformCompat.setPlayerDisplayName(player, arg)
                    null -> PlatformCompat.setPlayerDisplayName(player, null) // 允许设置为 null 来重置
                    else -> throw IllegalArgumentException("Argument for setDisplayName must be a String, Component, or null.")
                }
            })

            // playerListName(Component) -> void
            .function("setPlayerListName", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                when (val arg = context.getArgument(0)) {
                    is String -> PlatformCompat.setPlayerListName(player, mmUtil.deserialize(arg))
                    is Component -> PlatformCompat.setPlayerListName(player, arg)
                    null -> PlatformCompat.setPlayerListName(player, null) // 允许设置为 null 来重置
                    else -> throw IllegalArgumentException("Argument for setPlayerListName must be a String, Component, or null.")
                }
            })

            // playerListName() -> Component
            .function("getPlayerListName", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                PlatformCompat.getPlayerListName(player)
            })

            // kickPlayer(String) -> void (Legacy)
            .function("kickPlayerLegacy", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val message = context.getString(0)
                player.kickPlayer(message)
            })

            // kick(Component) -> void
            .function("kickPlayer", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                when (val arg = context.getArgument(0)) {
                    is String -> PlatformCompat.kick(player, mmUtil.deserialize(arg))
                    is Component -> PlatformCompat.kick(player, arg)
                    null -> PlatformCompat.kick(player, null) // 如果为 null，调用默认踢出
                    else -> throw IllegalArgumentException("Argument for kickPlayer must be a String, Component, or null.")
                }
            })

            // chat(String) -> void
            .function("sendChatMessage", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val message = context.getString(0)
                message?.let { player.chat(it) }
            })

            // performCommand(String) -> boolean
            .function("performCommand", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val command = context.getString(0)
                command?.let { player.performCommand(it) }
            })

            // isSneaking() -> boolean
            .function("isSneaking", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.isSneaking
            })

            // setSneaking(boolean) -> void
            .function("setSneaking", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val sneaking = context.getBoolean(0)
                player.isSneaking = sneaking
            })

            // isSprinting() -> boolean
            .function("isSprinting", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.isSprinting
            })

            // setSprinting(boolean) -> void
            .function("setSprinting", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val sprinting = context.getBoolean(0)
                player.isSprinting = sprinting
            })

            // saveData() -> void
            .function("saveData", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.saveData()
            })

            // loadData() -> void
            .function("loadData", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.loadData()
            })

            // setSleepingIgnored(boolean) -> void
            .function("setSleepingIgnored", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val ignored = context.getBoolean(0)
                player.isSleepingIgnored = ignored
            })

            // isSleepingIgnored() -> boolean
            .function("isSleepingIgnored", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.isSleepingIgnored
            })
    }
}
