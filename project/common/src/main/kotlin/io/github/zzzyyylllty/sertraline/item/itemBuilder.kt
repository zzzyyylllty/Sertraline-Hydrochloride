package io.github.zzzyyylllty.sertraline.item

//import io.github.projectunified.uniitem.all.AllItemProvider
//import io.github.projectunified.uniitem.api.ItemKey
import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.itemManager
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.Sertraline.manager
import io.github.zzzyyylllty.sertraline.config.asListEnhanced
import io.github.zzzyyylllty.sertraline.manager.PRIVATE_OWNER_TAG
import io.github.zzzyyylllty.sertraline.manager.SubManagerType
import io.github.zzzyyylllty.sertraline.manager.isPrivateItemId
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.compat.PlatformCompat
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.impl.getComponentsFilteredNMS
import io.github.zzzyyylllty.sertraline.item.adapter.transferBooleanToByte
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.impl.setComponentNMS
import io.github.zzzyyylllty.sertraline.util.ExternalItemHelper
import io.github.zzzyyylllty.sertraline.util.ItemTagUtil.parseMapNBT
import io.github.zzzyyylllty.sertraline.util.ItemTagUtil.parseNBT
import io.github.zzzyyylllty.sertraline.util.minimessage.cleanRemovedDataComponents
import io.github.zzzyyylllty.sertraline.util.VersionHelper
import io.github.zzzyyylllty.sertraline.util.toUpperCase
import io.github.zzzyyylllty.sertraline.function.update.getExpectedRevision
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import com.cryptomorin.xseries.XMaterial
import org.bukkit.Bukkit
import taboolib.module.lang.asLangText
import taboolib.module.nms.ItemTag
import taboolib.module.nms.NMSItemTag.Companion.asBukkitCopy
import taboolib.module.nms.NMSItemTag.Companion.asNMSCopy
import taboolib.module.nms.getItemTag
import taboolib.platform.util.buildItem


fun itemSource(str: String,player: Player?): ItemStack {
    val split = str.split(":").toMutableList()
    val key = split.first()
    split.removeFirst()
    val item = try {
        if (!str.contains(":") || str.startsWith("minecraft:")) {
            XMaterial.valueOf(
                (if (split.isNotEmpty()) split[0] else if (str != "null") str else "GRASS_BLOCK").toUpperCase()
            ).parseItem()
        } else {

//            val provider = AllItemProvider()
//            if (player != null) provider.item(ItemKey(key, split.joinToString(":")), player) else provider.item(ItemKey(key, split.joinToString(":")))
            if (player != null) {
                ExternalItemHelper.build(player, key, split.joinToString(":"))
            } else {
                ExternalItemHelper.buildNoPlayer(key, split.joinToString(":"))
            }
        }
    } catch (e: Exception) {
        severeS(console.asLangText("Error_External_ItemStack_Generation_Failed",str, e))
        e.printStackTrace()
        null
    }
    return item ?: run {
        devLog("Material is null, returning grass block")
        // 1.13+ enum name, not present on 1.12.2; XMaterial resolves by server version (1.12.2 → GRASS)
        ItemStack(XMaterial.GRASS_BLOCK.parseMaterial() ?: Material.STONE)
    }
}

/**
 * Generates a specific item.
 *
 * @param template Sertraline ID
 * @param player Player
 * @param source Item source; leave empty to not override
 * @param amount Amount
 * @param overrideData Overrides specific item data; used to preserve item variables when rebuilding an item, generally not needed by developers. Leave empty to not override.
 * */
fun sertralineItemBuilder(template: String,player: Player?,source: ItemStack? = null,amount: Int = 1,overrideData: Map<String, Any?>? = null): ItemStack? {
    return sertralineItemBuilderInternal(template, player, source, amount, overrideData)?.rebuildBypassKeepData(player)
}

/**
 * Generates the specified item with var data.
 *
 * @param template SertralineID
 * @param player the player
 * @param source the item source; leave empty to not override
 * @param amount the amount
 * @param overrideData Overrides specific item data; used to preserve item variables when rebuilding an item, generally not needed by developers. Leave empty to not override.
 * @param vars Built-in item data variables (can be persisted to NBT)
 * @param context Runtime context (objects like Player, Event), accessible via the {context:xxx} tag; not persisted to NBT
 * */
fun sertralineVarItemBuilder(template: String,player: Player?,source: ItemStack? = null,amount: Int = 1,overrideData: Map<String, Any?>? = null, vars: Map<String, Any?>? = null, context: Map<String, Any?>? = null): ItemStack? {
    return sertralineItemBuilderInternal(template, player, source, amount, overrideData,vars = vars, context = context)?.rebuildBypassKeepData(player)
}

/**
 * To generate an item, please use [sertralineItemBuilder]
 * */
fun sertralineItemBuilderInternal(template: String, player: Player?, source: ItemStack? = null, amount: Int = 1, overrideData: Map<String, Any?>? = null, rebuild: ItemStack? = null, vars: Map<String, Any?>? = null, context: Map<String, Any?>? = null): ItemStack? {
    val isPrivateTemplate = isPrivateItemId(template)
    val rebuildOwner = if (isPrivateTemplate) rebuild?.let(::privateOwnerUuid) else null
    val privateUuid = if (isPrivateTemplate) {
        rebuildOwner ?: player?.uniqueId?.toString() ?: run {
            try { manager.resolvePrivateUuid(null, null) } catch (_: Exception) { return null }
        }
    } else null

    val pTemplate: ModernSItem
    if (isPrivateTemplate) {
        pTemplate = (manager.privateManager.getItem(privateUuid!!, template, SubManagerType.TEMPORARY)
            ?: manager.privateManager.getItem(privateUuid, template, SubManagerType.PERSISTENT)
            ?: return null).deepCopy()
    } else {
        pTemplate = itemMap[template]?.deepCopy() ?: return null
    }
    overrideData?.let {
        it.forEach {
            pTemplate.setDeepData(it.key, it.value)
        }
    }
    vars?.let {
        val ovars = (pTemplate.getDeepData("sertraline:vars") as? Map<String, Any?>?)?.toMutableMap() ?: mutableMapOf()
        ovars.putAll(it)
        pTemplate.setDeepData("sertraline:vars", ovars)
    }
    context?.let {
        val ocontext = pTemplate.getDeepData("sertraline:context") as? MutableMap<String, Any?>? ?: mutableMapOf()
        ocontext.putAll(it)
        pTemplate.setDeepData("sertraline:context", ocontext)
        // keep-context: persist the matched context into a var so {var:xxx} still works after rebuilding
        pTemplate.getDeepData("sertraline:keep-context").asListEnhanced()?.let { keepContext ->
            if (keepContext.isNotEmpty()) {
                val vars = (pTemplate.getDeepData("sertraline:vars") as? Map<String, Any?>?)?.toMutableMap() ?: mutableMapOf()
                keepContext.forEach { key ->
                    ocontext[key]?.let { value -> vars[key] = value.toNbtPersistable() }
                }
                pTemplate.setDeepData("sertraline:vars", vars)
            }
        }
    }
    val processedTemplate = rebuild?.let { itemSerializer(it, player) } ?: itemSerializer(pTemplate, player)  ?: return null
    val itemSource = source ?: itemSource((processedTemplate.getDeepData("xbuilder:material") ?: processedTemplate.getDeepData("minecraft:material")).toString(), player)
    val item = itemManager.processItem(processedTemplate, itemSource, player)
    item.amount = amount
    val ownerUuid = if (isPrivateItemId(processedTemplate.key)) {
        if (rebuild != null) rebuildOwner else privateUuid
    } else null

    if (processedTemplate.getDeepData("sertraline:no-sertraline-id") as? Boolean? ?: false) {
        val tag = item.getItemTag()
        tag.remove(PRIVATE_OWNER_TAG)
        tag.saveTo(item)
        return item
    } else {
        val tag = item.getItemTag()
        tag["sertraline_id"] = processedTemplate.key
        if (ownerUuid != null) tag[PRIVATE_OWNER_TAG] = ownerUuid else tag.remove(PRIVATE_OWNER_TAG)
        // Write the type to NBT
        val typeData = processedTemplate.getDeepData("sertraline:type")
        if (typeData != null) {
            val typeId = when (typeData) {
                is io.github.zzzyyylllty.sertraline.data.Type -> typeData.id
                else -> typeData.toString()
            }
            tag["sertraline_type"] = typeId
        }
        // Write the revision to NBT
        val revision = getExpectedRevision(processedTemplate.key, ownerUuid = ownerUuid)
        if (revision > 0) tag["sertraline_revision"] = revision
        tag.saveTo(item)
        return item
    }
}

/**
 * Uses a temporary ModernSItem, for removing some data in recipes.
 * To generate an item, please use [sertralineItemBuilder]
 * */
fun sertralineItemBuilderTemporary(template: ModernSItem, player: Player?, source: ItemStack? = null, amount: Int = 1, overrideData: Map<String, Any?>? = null, rebuild: ItemStack? = null): ItemStack? {
    val pTemplate = template.deepCopy()
    val isPrivateTemplate = isPrivateItemId(pTemplate.key)
    val rebuildOwner = if (isPrivateTemplate) rebuild?.let(::privateOwnerUuid) else null
    val privateUuid = if (isPrivateTemplate) {
        rebuildOwner ?: player?.uniqueId?.toString() ?: run {
            try { manager.resolvePrivateUuid(null, null) } catch (_: Exception) { return null }
        }
    } else null
    overrideData?.let {
        it.forEach {
            pTemplate.setDeepData(it.key, it.value)
        }
    }
    val processedTemplate = rebuild?.let { itemSerializer(it, player) } ?: itemSerializer(pTemplate, player)  ?: return null
    val itemSource = source ?: itemSource((processedTemplate.getDeepData("xbuilder:material") ?: processedTemplate.getDeepData("minecraft:material")).toString(), player)
    val item = itemManager.processItem(processedTemplate, itemSource, player)
    item.amount = amount
    val ownerUuid = if (isPrivateItemId(processedTemplate.key)) {
        if (rebuild != null) rebuildOwner else privateUuid
    } else null

    val tag = item.getItemTag()
    tag["sertraline_id"] = processedTemplate.key
    if (ownerUuid != null) tag[PRIVATE_OWNER_TAG] = ownerUuid else tag.remove(PRIVATE_OWNER_TAG)
    // Write the type to NBT
    val typeData = processedTemplate.getDeepData("sertraline:type")
    if (typeData != null) {
        val typeId = when (typeData) {
            is io.github.zzzyyylllty.sertraline.data.Type -> typeData.id
            else -> typeData.toString()
        }
        tag["sertraline_type"] = typeId
    }
    // Write the revision to NBT
    val revision = getExpectedRevision(processedTemplate.key, ownerUuid = ownerUuid)
    if (revision > 0) tag["sertraline_revision"] = revision
    tag.saveTo(item)
    return item
}

fun ItemStack.rebuild(player: Player?): ItemStack {

    val tag = this.clone().getItemTag(true)
    val sID = tag["sertraline_id"]?.asString() ?: return this
    val overrideData = mutableMapOf<String, Any?>()
    overrideData["sertraline:vars"] = tag["sertraline_data"]?.parseMapNBT()
    val regen = sertralineItemBuilderInternal(sID, player,overrideData = overrideData, amount = this.amount, rebuild = this) ?: throw NullPointerException("Item $sID Not Exist")
    val keep = config["rebuild.keep-data"].asListEnhanced() ?: listOf("sertraline_data","sertraline_revision")
    val newTag = regen.getItemTag()
    keep.forEach {
        newTag[it] = transferBooleanToByte(tag[it]?.parseNBT())
    }
    newTag.saveTo(regen)
    var rewrited = regen
    var rewritedNMS = asNMSCopy(rewrited)

    val keepComp = config["rebuild.keep-component"].asListEnhanced() ?: listOf()
    if (!keepComp.isEmpty()) {
        val orgComponent = asNMSCopy(this).getComponentsFilteredNMS()
        keepComp.forEach {
            orgComponent[it]?.let { value -> rewritedNMS.setComponentNMS(it, value)?.let { rewritedNMS = it } }
        }
        rewrited = asBukkitCopy(rewritedNMS)
    }
    return rewrited
}

fun ItemStack.rebuildBypassKeepData(player: Player?): ItemStack {

    val tag = this.clone().getItemTag(true)
    val sID = tag["sertraline_id"]?.asString() ?: return this

    // Fast path: if there is no dynamics and no NBT instance data, rebuilding is redundant
    val template = itemTemplateForStack(this, player)
    if (template != null && template.getDeepData("sertraline:dynamics") == null) {
        val nbtData = tag["sertraline_data"]?.parseMapNBT()
        if (nbtData == null) {
            return this // First build, no dynamic content → rebuild would not change the result
        }
    }

    val overrideData = mutableMapOf<String, Any?>()
    overrideData["sertraline:vars"] = tag["sertraline_data"]?.parseMapNBT()
    val regen = sertralineItemBuilderInternal(sID, player,overrideData = overrideData, amount = this.amount, rebuild = this) ?: throw NullPointerException("Item $sID Not Exist")

    return regen
}

fun ItemStack.rebuildLore(player: Player?) {

    val tag = this.clone().getItemTag(true)
    val sID = tag["sertraline_id"]?.asString() ?: return
    val overrideData = mutableMapOf<String, Any?>()
    overrideData["sertraline:vars"] = tag["sertraline_data"]?.parseMapNBT()
    val regen = sertralineItemBuilderInternal(sID, player,overrideData = overrideData, rebuild = this) ?: throw NullPointerException("Item $sID Not Exist")
    PlatformCompat.setLore(this, PlatformCompat.getLore(regen))
}


/**
 * This will lost about 2%(3 in 76) Component data.
 * */
fun ItemStack.rebuildUnsafe(player: Player?) {

    val tag = this.clone().getItemTag(true)
    val sID = tag["sertraline_id"]?.asString() ?: return
    val overrideData = mutableMapOf<String, Any?>()
    overrideData["sertraline:vars"] = tag["sertraline_data"]?.parseMapNBT()
    var regen = sertralineItemBuilderInternal(sID, player,overrideData = overrideData, rebuild = this) ?: throw NullPointerException("Item $sID Not Exist")

    var rewritedNMS = asNMSCopy(regen)
    val keepComp = config["rebuild.keep-component"].asListEnhanced() ?: listOf()
    if (!keepComp.isEmpty()) {
        val orgComponent = asNMSCopy(this).getComponentsFilteredNMS()
        keepComp.forEach {
            orgComponent[it]?.let { value -> rewritedNMS.setComponentNMS(it, value)?.let { rewritedNMS = it } }
        }
        regen = asBukkitCopy(rewritedNMS)
    }

    val keep = config["rebuild.keep-data"].asListEnhanced() ?: listOf("sertraline_data","sertraline_revision")
    val newTag = regen.getItemTag()
    keep.forEach {
        newTag[it] = transferBooleanToByte(tag[it]?.parseNBT())
    }
    newTag.saveTo(regen)
    this.setItemMeta(regen.itemMeta)
}

fun ItemStack.rebuildName(player: Player?) {
    val tag = this.clone().getItemTag(true)
    val sID = tag["sertraline_id"]?.asString() ?: return
    val overrideData = mutableMapOf<String, Any?>()
    overrideData["sertraline:vars"] = tag["sertraline_data"]?.parseMapNBT()
    val regen = sertralineItemBuilderInternal(sID, player,overrideData = overrideData, rebuild = this) ?: throw NullPointerException("Item $sID Not Exist")
    val name = PlatformCompat.getEffectiveName(regen)
    if (VersionHelper().isOrAbove12005()) {
        PlatformCompat.setDataComponent(this, "CUSTOM_NAME", name)
    } else {
        this.itemMeta?.let { meta ->
            PlatformCompat.setDisplayName(meta, name)
            this.setItemMeta(meta)
        }
    }
}

fun ItemStack.rebuildDisplay(player: Player?) {

    val tag = this.clone().getItemTag(true)
    val sID = tag["sertraline_id"]?.asString() ?: return
    val overrideData = mutableMapOf<String, Any?>()
    overrideData["sertraline:vars"] = tag["sertraline_data"]?.parseMapNBT()
    val regen = sertralineItemBuilderInternal(sID, player,overrideData = overrideData, rebuild = this) ?: throw NullPointerException("Item $sID Not Exist")
    PlatformCompat.setLore(this, PlatformCompat.getLore(regen))
    val name = PlatformCompat.getEffectiveName(regen)
    if (VersionHelper().isOrAbove12005()) {
//        val component = if (VersionHelper().isOrAbove12104()) "ITEM_NAME" else "CUSTOM_NAME"
        val component =  "CUSTOM_NAME"
        PlatformCompat.setDataComponent(this, component, name)
    } else {
        this.itemMeta?.let { meta ->
            PlatformCompat.setDisplayName(meta, name)
            PlatformCompat.setLore(meta, PlatformCompat.getLore(regen))
            this.setItemMeta(meta)
        }
    }

}

/**
 * Convert a context value into a type persistable to NBT.
 * String/Number/Boolean/arrays stay as-is; Player/Entity become name; Map/Collection recurse;
 * other objects become strings when possible. Used by keep-context to store runtime context as a var.
 */
private fun Any?.toNbtPersistable(): Any? = when (this) {
    null -> null
    is String, is Number, is Boolean -> this
    is ByteArray, is IntArray, is LongArray -> this
    is Player -> name
    is Entity -> name
    is Map<*, *> -> entries.associate { it.key.toString() to it.value.toNbtPersistable() }
    is Collection<*> -> map { it.toNbtPersistable() }
    else -> toString()
}