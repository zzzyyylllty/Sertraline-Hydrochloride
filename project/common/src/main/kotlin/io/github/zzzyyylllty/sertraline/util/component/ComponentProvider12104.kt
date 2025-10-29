package io.github.zzzyyylllty.sertraline.util.component

import com.google.common.hash.HashCode
import com.google.gson.JsonElement
import com.mojang.serialization.DynamicOps
import de.tr7zw.changeme.nbtapi.NBT
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer


class ItemComponentProvider12104(item: ItemStack): UnitItemComponentProvider(item) {
    override val item: ItemStack? = null

    override val NBTS: DynamicOps<Any?>? = null
    override val JAVA: DynamicOps<Any?>? = null
    override val JSON: DynamicOps<JsonElement?>? = null
    override val HASHCODE: DynamicOps<HashCode?>? = null // 1.21.5+
    override fun getComponent(component: String) {
    }

    override fun getComponents() {
    }

    override fun setJsonComponent(component: String, value: Any?) {
    }

    override fun setNBTComponent(component: String, value: Any?) {
    }

    override fun setJavaComponent(component: String, value: Any?) {
    }

}


