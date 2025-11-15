package io.github.zzzyyylllty.sertraline.function.fluxon.script

import io.github.zzzyyylllty.sertraline.Sertraline.fluxonInst
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
    ExtensionPlayer.init(fluxonInst)
}

object ExtensionPlayer {

    fun init(runtime: FluxonRuntime) {
        runtime.registerExtension(Player::class.java)
            // sendMessage(String | Component)
            .function("sendMessage", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val p: Player = Objects.requireNonNull<Player>(context!!.getTarget())
                when (val arg = context.getArgument(0)) {
                    is String -> p.sendMessage(mmUtil.deserialize(arg))
                    is Component -> p.sendMessage(arg)
                    else -> throw IllegalArgumentException("Argument for sendMessage must be a String or Component.")
                }
            })

            // sendComponentMessage(String | Component) - 推荐使用这个，语义更清晰
            .function("sendComponentMessage", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val p: Player = Objects.requireNonNull<Player>(context!!.getTarget())
                when (val arg = context.getArgument(0)) {
                    is String -> p.sendMessage(mmUtil.deserialize(arg))
                    is Component -> p.sendMessage(arg)
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
                    is String -> player.displayName(mmUtil.deserialize(arg))
                    is Component -> player.displayName(arg)
                    null -> player.displayName(null) // 允许设置为 null 来重置
                    else -> throw IllegalArgumentException("Argument for setDisplayName must be a String, Component, or null.")
                }
            })

            // playerListName(Component) -> void
            .function("setPlayerListName", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                when (val arg = context.getArgument(0)) {
                    is String -> player.playerListName(mmUtil.deserialize(arg))
                    is Component -> player.playerListName(arg)
                    null -> player.playerListName(null) // 允许设置为 null 来重置
                    else -> throw IllegalArgumentException("Argument for setPlayerListName must be a String, Component, or null.")
                }
            })

            // playerListName() -> Component
            .function("getPlayerListName", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.playerListName()
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
                    is String -> player.kick(mmUtil.deserialize(arg))
                    is Component -> player.kick(arg)
                    null -> player.kick() // 如果为 null，调用无参 kick
                    else -> throw IllegalArgumentException("Argument for kickPlayer must be a String, Component, or null.")
                }
            })

            // chat(String) -> void
            .function("sendChatMessage", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val message = context.getString(0)
                player.chat(message)
            })

            // performCommand(String) -> boolean
            .function("performCommand", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val command = context.getString(0)
                player.performCommand(command)
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
