package io.github.zzzyyylllty.sertraline.function.fluxon.script

import io.github.zzzyyylllty.sertraline.Sertraline.fluxonInst
import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.FunctionContext
import org.tabooproject.fluxon.runtime.NativeFunction.NativeCallable
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import java.util.*

@Awake(LifeCycle.ENABLE)
fun registerExtensionPlayer() {
    ExtensionPlayer.init(fluxonInst)
}


object ExtensionPlayer {
    fun init(runtime: FluxonRuntime) {
        runtime.registerExtension(Player::class.java) // 获取指定索引的元素
            .function("sendMessage", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val p: Player = Objects.requireNonNull<Player>(context!!.getTarget())
                when (val arg = context!!.getArgument(0)) {
                    is String -> p.sendMessage(arg)
                    is Component -> p.sendMessage(arg)
                    else -> throw IllegalArgumentException("Argument for broadcast must be a String or Component.")
                }
            })
            .function("sendComponentMessage", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val p: Player = Objects.requireNonNull<Player>(context!!.getTarget())
                val message = context.getString(0).toString()
                p.sendMessage(mmUtil.deserialize(message))
            })
            // getName()
            .function("getName", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.getName()
            })

            // getDisplayName() -> deprecated
            .function("getDeprecatedDisplayName", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.getDisplayName()
            })

            // setDisplayName(String) -> deprecated
            .function("setDeprecatedDisplayName", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val name = context.getString(0)
                player.setDisplayName(name)
            })

            // displayName(Component)
            .function("setDisplayName", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val name = context.getString(0)?.let { mmUtil.deserialize(it) }
                player.displayName(name)
            })

            // playerListName(Component)
            .function("setPlayerListName", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val name = context.getString(0)?.let { mmUtil.deserialize(it) }
                player.playerListName(name)
            })

            // getPlayerListName(Component)
            .function("getPlayerListName", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.playerListName()
            })

            // kickPlayer(String) -> deprecated
            .function("kickPlayerDeprecated", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val message = context.getString(0)
                player.kickPlayer(message)
            })

            // kick(Component)
            .function("kickPlayer", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val message = context.getString(0)?.let { mmUtil.deserialize(it) }
                player.kick(message)
            })

            // chat(String)
            .function("sendChatMessage", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val message = context.getString(0)
                player.chat(message)
            })

            // performCommand(String)
            .function("performCommand", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val command = context.getString(0)
                player.performCommand(command)
            })

            // isSneaking()
            .function("isSneaking", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.isSneaking()
            })

            // setSneaking(boolean)
            .function("setSneaking", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val sneaking = context.getBoolean(0)
                player.setSneaking(sneaking)
            })

            // isSprinting()
            .function("isSprinting", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.isSprinting()
            })

            // setSprinting(boolean)
            .function("setSprinting", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val sprinting = context.getBoolean(0)
                player.setSprinting(sprinting)
            })

            // saveData()
            .function("saveData", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.saveData()
            })

            // loadData()
            .function("loadData", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.loadData()
            })

            // setSleepingIgnored(boolean)
            .function("setSleepingIgnored", 1, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                val ignored = context.getBoolean(0)
                player.setSleepingIgnored(ignored)
            })

            // isSleepingIgnored()
            .function("isSleepingIgnored", 0, NativeCallable { context: FunctionContext<Player?>? ->
                val player = Objects.requireNonNull<Player>(context!!.getTarget())
                player.isSleepingIgnored()
            })
    }
}