package io.github.zzzyyylllty.sertraline.impl

import com.google.gson.JsonElement
import io.github.zzzyyylllty.embiancomponent.tools.ensureDataComponentType
import io.github.zzzyyylllty.embiancomponent.tools.getComponentJavaNMS
import io.github.zzzyyylllty.embiancomponent.tools.getComponentNMS
import io.github.zzzyyylllty.embiancomponent.tools.getComponentsNMS
import io.github.zzzyyylllty.embiancomponent.tools.getComponentsNMSFiltered
import io.github.zzzyyylllty.embiancomponent.tools.removeComponentNMS
import io.github.zzzyyylllty.embiancomponent.tools.setComponentNMS
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.NMSItemTag.Companion.asBukkitCopy
import taboolib.module.nms.NMSItemTag.Companion.asNMSCopy

fun ItemStack.getComponents(): Map<String, Any?> {
    return asNMSCopy(this).getComponentsNMS()
}

fun ItemStack.getComponentsNMSFiltered(): Map<String, Any?> {
    return asNMSCopy(this).getComponentsNMSFiltered()
}

fun Any.getComponentsNMS(): Map<String, Any?> {
    return this.getComponentsNMS()
}

fun Any.getComponentsFilteredNMS(): Map<String, Any?> {
    return this.getComponentsNMSFiltered()
}

fun ItemStack.getComponent(component: String): JsonElement? {
    return asNMSCopy(this).getComponentNMS(component)
}


fun Any.getComponentNMS(component: String): JsonElement? {
    return this.getComponentNMS(component)
}

fun <T> ItemStack.getComponentJava(component: String): T? {
    return asNMSCopy(this).getComponentJavaNMS<T>(component)
}


fun <T> Any.getComponentJavaNMS(component: String): T? {
    return this.getComponentJavaNMS<T>(component)
}


fun ItemStack.setComponent(component: String, value: Any): ItemStack? {
    return asBukkitCopy(asNMSCopy(this).setComponentNMS(component, value)) as ItemStack?
}


fun Any.setComponentNMS(component: String, value: Any): Any? {
    return this.setComponentNMS(component, value)
}

fun Any.removeComponentNMS(componentId: String): Any {
    // The extension function modifies the this in-place.
    this.removeComponentNMS(componentId)
    // Return the same this that was modified.
    return this
}
