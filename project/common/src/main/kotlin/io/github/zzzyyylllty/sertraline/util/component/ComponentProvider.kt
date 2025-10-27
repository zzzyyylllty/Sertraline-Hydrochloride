package io.github.zzzyyylllty.sertraline.util.component

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.util.VersionHelper
import io.papermc.paper.datacomponent.DataComponentType
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.module.lang.asLangText

interface ItemComponentProvider {
    val key: String
    val componentType: Any?
    fun applyComponent(item: ItemStack, value: Any?): ItemStack
}



class ItemComponentRegistry {
    private val providers: MutableMap<String, ItemComponentProvider> = mutableMapOf()

    fun register(provider: ItemComponentProvider) {
        if (providers.containsKey(provider.key)) {
            severeS(console.asLangText("Warning_Component_Provider_Overwrite", provider.key))
        }
        providers[provider.key] = provider
    }

    fun unregister(key: String) {
        providers.remove(key)
    }

    fun getProvider(key: String): ItemComponentProvider? {
        return providers[key]
    }

    fun addCustomData(item: ItemStack, key: String, value: Any?): ItemStack {
        val provider = getProvider(key)
        return if (provider != null) {
            provider.applyComponent(item, value)
        } else {
            severeS(console.asLangText("Error_Component_Provider_Not_Found", key))
            item
        }
    }
}

fun initComponentProvider() {
    val ver = VersionHelper().getVer()
    if (ver <= 12004) {

    }
}