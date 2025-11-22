package io.github.zzzyyylllty.sertraline.util.dependencies

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.util.DependencyHelper
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.util.bukkitPlugin

object PacketEventsUtil {
    @Awake(LifeCycle.LOAD)
    fun onInitPacketEvents() {
        if (!DependencyHelper.pe || !config.getBoolean("packet.register", true)) return
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(bukkitPlugin))
        PacketEvents.getAPI().load()

        devLog("Registering packet listeners...")
        PacketEvents.getAPI().eventManager.registerListener(
            PacketEventsSendListener(), PacketListenerPriority.HIGHEST
        )
        PacketEvents.getAPI().eventManager.registerListener(
            PacketEventsReceiveListener(), PacketListenerPriority.HIGHEST
        )


    }

    @Awake(LifeCycle.ENABLE)
    fun onEnablePacketEvents() {
        if (!DependencyHelper.pe || !config.getBoolean("packet.register", true)) return
        //Initialize!
        PacketEvents.getAPI().init()
    }

    @Awake(LifeCycle.DISABLE)
    fun onDisablePacketEvents() {
        if (!DependencyHelper.pe || !config.getBoolean("packet.register", true)) return
        //Terminate the instance (clean up process)
        PacketEvents.getAPI().terminate()
    }
}