package io.github.zzzyyylllty.sertraline.util.minimessage

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.util.VersionHelper
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.DataComponentValue
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.momirealms.sparrow.message.MiniMessage as SparrowMiniMessage

val mmUtil = MiniMessage.miniMessage()
val mmStrictUtil = MiniMessage.builder().strict(true).build()
val mmLegacyAmpersandUtil = LegacyComponentSerializer.legacyAmpersand()
val mmLegacySectionUtil = LegacyComponentSerializer.legacySection()
val mmJsonUtil = GsonComponentSerializer.gson()

/**
 * 递归剔除 ShowItem hover 中无法序列化的 Removed 值。
 * Paper 的 getDisplayName() 会在名字上附加 ShowItem hover（dataComponents 拷贝自物品 components patch，
 * 可能含 YAML 中 minecraft:xxx: null 产生的移除补丁），而 adventure 的 DataComponentValueConverterRegistry
 * 无 Removed→TagSerializable 转换（ServiceLoader 静态加载，无运行时注册），直接 serialize 会抛异常。
 */
fun Component.cleanRemovedDataComponents(): Component {
    var cleaned = this
    cleaned.style().hoverEvent()?.let { hover ->
        if (hover.action() == HoverEvent.Action.SHOW_ITEM) {
            // hoverEvent() 返回 HoverEvent<?>，星投影下 value() 是 Any?，需显式转型
            val showItem = hover.value() as HoverEvent.ShowItem
            if (showItem.dataComponents().values.any { it is DataComponentValue.Removed }) {
                val clean = showItem.dataComponents().filterValues { it !is DataComponentValue.Removed }
                cleaned = cleaned.style(
                    cleaned.style().hoverEvent(
                        HoverEvent.showItem(HoverEvent.ShowItem.showItem(showItem.item(), showItem.count(), clean))
                    )
                )
            }
        }
    }
    val children = cleaned.children()
    if (children.isNotEmpty()) {
        cleaned = cleaned.children(children.map { it.cleanRemovedDataComponents() })
    }
    return cleaned
}

fun Component.toMiniMessageString(strict: Boolean = false): String =
    cleanRemovedDataComponents().let { if (strict) mmStrictUtil.serialize(it) else mmUtil.serialize(it) }

/**
 * fast-minimessage 解析门面（experimental.yml 开启，默认关闭）。
 * 关闭时仅一次布尔分支，行为与原版完全一致；开启后走 sparrow-minimessage。
 * sparrow 实例懒加载；sparrow 为 Java 21 字节码，legacy 平台不捆绑且 applyConfig 强制关闭，懒引用永不触达。
 */
object FastMiniMessage {

    @Volatile
    var enabled: Boolean = false
        private set

    /** sparrow 无法在当前环境运行（Adventure 版本过旧 / 初始化抛出 LinkageError）后置为 true */
    @Volatile
    private var broken = false

    private val sparrow: SparrowMiniMessage by lazy { SparrowMiniMessage.miniMessage() }

    @Volatile
    private var cache: Cache<String, Component>? = null

    fun applyConfig(enable: Boolean, cacheSize: Int) {
        if (!sparrowSupported()) {
            broken = true
            enabled = false
            cache = null
            if (enable) warningL("Warning_Fast_MiniMessage_Adventure_Unsupported")
            return
        }
        enabled = enable && !VersionHelper().isLegacy() && !broken
        cache = if (enabled && cacheSize > 0) {
            Caffeine.newBuilder().maximumSize(cacheSize.toLong()).build()
        } else {
            null
        }
    }

    fun deserialize(str: String): Component {
        if (!enabled) return mmUtil.deserialize(str)
        val c = cache
        if (c != null) {
            c.getIfPresent(str)?.let { return it }
            val parsed = safeSparrowDeserialize(str) ?: return mmUtil.deserialize(str)
            c.put(str, parsed)
            return parsed
        }
        return safeSparrowDeserialize(str) ?: mmUtil.deserialize(str)
    }

    /** sparrow 依赖 Adventure 4.18+ 的 ClickEvent.Action.TextCarrier；LinkageError 时降级为原版解析器 */
    private fun safeSparrowDeserialize(str: String): Component? {
        return try {
            sparrow.deserialize(str)
        } catch (e: LinkageError) {
            broken = true
            enabled = false
            cache = null
            warningL("Warning_Fast_MiniMessage_Failed", e.message ?: e.javaClass.simpleName)
            null
        }
    }

    /** 探测当前服务器 Adventure 是否包含 sparrow 所需的 TextCarrier 类 */
    private fun sparrowSupported(): Boolean {
        return try {
            Class.forName("net.kyori.adventure.text.event.ClickEvent\$Action\$TextCarrier")
            true
        } catch (_: ClassNotFoundException) {
            false
        } catch (_: NoClassDefFoundError) {
            false
        }
    }
}