package io.github.zzzyyylllty.sertraline.function.kether

import io.github.zzzyyylllty.sertraline.data.SertralineItem
import org.bukkit.inventory.ItemStack
import taboolib.module.kether.ScriptFrame


fun ScriptFrame.getScriptItemStack(): ItemStack {
    return variables().get<Any?>("@SertralineItemStack").orElse(null) as? ItemStack ?: error("No SertralineItem-stream selected.")
}
fun ScriptFrame.getScriptSertralineItem(): SertralineItem {
    return variables().get<Any?>("@SertralineItem").orElse(null) as? SertralineItem ?: error("No SertralineItem-stream selected.")
}