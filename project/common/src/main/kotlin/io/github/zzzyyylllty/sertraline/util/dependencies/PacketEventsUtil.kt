package io.github.zzzyyylllty.sertraline.util.dependencies

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.util.bukkitPlugin

/*
@Awake(LifeCycle.LOAD)
fun onInitPacketEvents() {
    PacketEvents.setAPI(SpigotPacketEventsBuilder.build(bukkitPlugin))
    PacketEvents.getAPI().load()
    /*
    devLog("Registering packet listeners...")
    PacketEvents.getAPI().eventManager.registerListener(
        PacketEventsPacketListener(), PacketListenerPriority.NORMAL
    )
     */
}

@Awake(LifeCycle.ENABLE)
fun onEnablePacketEvents() {
    //Initialize!
    PacketEvents.getAPI().init()
}

@Awake(LifeCycle.DISABLE)
fun onDisablePacketEvents() {
    //Terminate the instance (clean up process)
    PacketEvents.getAPI().terminate()
}
*/