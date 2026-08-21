package io.github.zzzyyylllty.sertraline.compat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.KeybindComponent as AdventureKeybind
import net.kyori.adventure.text.NBTComponent as AdventureNBT
import net.kyori.adventure.text.ScoreComponent as AdventureScore
import net.kyori.adventure.text.ScopedComponent as AdventureScoped
import net.kyori.adventure.text.SelectorComponent as AdventureSelector
import net.kyori.adventure.text.TextComponent as AdventureText
import net.kyori.adventure.text.TranslatableComponent as AdventureTranslatable
import net.kyori.adventure.text.format.TextDecoration
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.KeybindComponent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.TranslatableComponent
import java.lang.reflect.Method

/**
 * adventure Component → bungee BaseComponent（Spigot 专用）。
 * Spigot 的 sendComponent/sendActionBar 走 BaseComponent 保真传递：
 * hex 颜色、装饰、子元素、click/hover 事件均保留，不再降级为 § 字符串
 * （LEGACY serialize 只对 Title/ItemMeta/Inventory 标题等无 BaseComponent API 的地方使用）。
 * bungee 无 Selector/Score/NBT 组件，这几类降级为纯文本。
 */
object BungeeComponentConverter {

    fun convert(component: Component): BaseComponent {
        val node = convertNode(component)
        component.children().forEach { node.addExtra(convert(it)) }
        return node
    }

    fun convertOrNull(component: Component?): BaseComponent? = component?.let { convert(it) }

    private fun convertNode(component: Component): BaseComponent {
        val node: BaseComponent = when (component) {
            is AdventureText -> TextComponent(component.content())
            is AdventureTranslatable ->
                TranslatableComponent(component.key(), *component.arguments().map { translateArg(it) }.toTypedArray())
            is AdventureKeybind -> KeybindComponent(component.keybind())
            is AdventureSelector -> TextComponent(component.pattern())
            is AdventureScore -> TextComponent(listOfNotNull(component.name(), component.value()).joinToString(" "))
            is AdventureNBT<*, *> -> TextComponent(component.nbtPath())
            // adventure 4.21+ 移除 DecoratedComponent，委托组件统一为 ScopedComponent.asComponent()
            is AdventureScoped<*> -> convertNode(component.asComponent())
            else -> TextComponent(component.toString())
        }
        applyStyle(node, component.style())
        return node
    }

    /** 翻译参数：组件类转 bungee 组件；标量（String/Number/Boolean）原样交给客户端插值 */
    private fun translateArg(arg: net.kyori.adventure.text.TranslationArgument): Any {
        val value = arg.value()
        return if (value is ComponentLike) convert(value.asComponent()) else value
    }

    private fun applyStyle(node: BaseComponent, style: net.kyori.adventure.text.format.Style) {
        style.color()?.let { node.color = toBungeeColor(it.asHexString()) }

        val obf = style.decoration(TextDecoration.OBFUSCATED)
        if (obf == TextDecoration.State.TRUE) node.isObfuscated = true
        else if (obf == TextDecoration.State.FALSE) node.isObfuscated = false
        val bold = style.decoration(TextDecoration.BOLD)
        if (bold == TextDecoration.State.TRUE) node.isBold = true
        else if (bold == TextDecoration.State.FALSE) node.isBold = false
        val strike = style.decoration(TextDecoration.STRIKETHROUGH)
        if (strike == TextDecoration.State.TRUE) node.isStrikethrough = true
        else if (strike == TextDecoration.State.FALSE) node.isStrikethrough = false
        val underline = style.decoration(TextDecoration.UNDERLINED)
        if (underline == TextDecoration.State.TRUE) node.isUnderlined = true
        else if (underline == TextDecoration.State.FALSE) node.isUnderlined = false
        val italic = style.decoration(TextDecoration.ITALIC)
        if (italic == TextDecoration.State.TRUE) node.isItalic = true
        else if (italic == TextDecoration.State.FALSE) node.isItalic = false

        style.clickEvent()?.let { e ->
            val action = toBungeeAction(e.action()) ?: return@let
            node.clickEvent = ClickEvent(action, e.value())
        }
        style.hoverEvent()?.let { e ->
            if (e.action() == net.kyori.adventure.text.event.HoverEvent.Action.SHOW_TEXT) {
                val text = (e.value() as? Component) ?: return@let
                node.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(convert(text)))
            }
        }
    }

    private fun toBungeeAction(action: net.kyori.adventure.text.event.ClickEvent.Action): ClickEvent.Action? =
        when (action) {
            net.kyori.adventure.text.event.ClickEvent.Action.OPEN_URL -> ClickEvent.Action.OPEN_URL
            net.kyori.adventure.text.event.ClickEvent.Action.OPEN_FILE -> ClickEvent.Action.OPEN_FILE
            net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND -> ClickEvent.Action.RUN_COMMAND
            net.kyori.adventure.text.event.ClickEvent.Action.SUGGEST_COMMAND -> ClickEvent.Action.SUGGEST_COMMAND
            net.kyori.adventure.text.event.ClickEvent.Action.CHANGE_PAGE -> ClickEvent.Action.CHANGE_PAGE
            net.kyori.adventure.text.event.ClickEvent.Action.COPY_TO_CLIPBOARD -> copyToClipboardAction
            // bungee 1.20-R0.2 无 SHOW_DIALOG/CUSTOM，无法传递，跳过
            else -> null
        }

    // ── bungee 1.16+ API 反射桥（v11200 = MC 1.12.2 编译面不存在，正确降级） ──

    // ChatColor.of(String) 是 1.16+ API；1.12.2 无 hex 颜色模型，缺失时返回 null（无色）
    private val chatColorOfMethod: Method? by lazy {
        try { ChatColor::class.java.getMethod("of", String::class.java) } catch (_: Throwable) { null }
    }

    private fun toBungeeColor(hex: String): ChatColor? {
        return try {
            chatColorOfMethod?.invoke(null, "#$hex") as? ChatColor
        } catch (_: Throwable) {
            null
        }
    }

    // ClickEvent.Action.COPY_TO_CLIPBOARD 是 1.16+ 枚举常量；1.12.2 缺失时该 click 事件整体丢弃
    private val copyToClipboardAction: ClickEvent.Action? by lazy {
        try {
            @Suppress("UNCHECKED_CAST")
            ClickEvent.Action::class.java.getField("COPY_TO_CLIPBOARD").get(null) as? ClickEvent.Action
        } catch (_: Throwable) {
            null
        }
    }
}
