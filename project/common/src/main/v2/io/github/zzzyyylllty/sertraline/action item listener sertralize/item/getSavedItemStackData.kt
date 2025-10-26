package io.github.zzzyyylllty.sertraline.function.item

import com.google.gson.GsonBuilder
import io.github.zzzyyylllty.sertraline.function.sertralize.ConfigurationSerializableAdapter
import io.github.zzzyyylllty.sertraline.function.sertralize.PatternTypeAdapter
import io.github.zzzyyylllty.sertraline.function.sertralize.TimeZoneTypeAdapter
import org.bukkit.block.banner.Pattern
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag
import java.util.TimeZone


val gsonBuilder = GsonBuilder()
    .setVersion(1.0)
    .disableHtmlEscaping()
    .disableInnerClassSerialization()
    .setPrettyPrinting()
    .excludeFieldsWithModifiers()
    .setLenient()
    .registerTypeAdapter(TimeZone::class.java, TimeZoneTypeAdapter())
    .registerTypeAdapter(Pattern::class.java, PatternTypeAdapter())
    .registerTypeAdapter(ConfigurationSerializableAdapter::class.java, PatternTypeAdapter())
    .create()


fun ItemStack.getSavedData(): LinkedHashMap<String, Any?> {
    val tag = this.getItemTag()

    val dataMap = gsonBuilder.fromJson(tag["SERTRALINE_DATA"]?.toJsonSimplified(), LinkedHashMap::class.java)

    return dataMap as LinkedHashMap<String, Any?>
}