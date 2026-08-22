package io.github.zzzyyylllty.sertraline.listener.custom

import io.github.zzzyyylllty.sertraline.config.CustomEventsConfig
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.debugMode.devLogBypassCheck
import io.github.zzzyyylllty.sertraline.listener.action.ThrottleActionLink
import io.github.zzzyyylllty.sertraline.listener.action.ThrottleActionParam
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.util.ActionHelper.throttleAction
import io.github.zzzyyylllty.sertraline.util.ClassAliases
import io.github.zzzyyylllty.sertraline.util.EventPlayerResolver
import io.github.zzzyyylllty.sertraline.util.isNotExist
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.EventExecutor
import taboolib.library.configuration.ConfigurationSection
import taboolib.platform.BukkitPlugin

/**
 * 自定义事件注册
 *
 * 让物品动作可以监听任意 Bukkit 事件，而不仅限于 [io.github.zzzyyylllty.sertraline.listener.action.itemAction]
 * 中内置的 onClick / onConsume 等触发。参考 Vulpecula 的 dispatcher 注册方式：
 * 在 config.yml 的 custom-events 段注册「触发名 -> 事件类 / 优先级 / accept-cancelled / 物品来源」，
 * 物品在 sertraline:actions 下直接使用该触发名即可。
 *
 * config.yml 示例：
 * ```
 * custom-events:
 *   onPlayerDeath:
 *     class: PlayerDeathEvent
 *     priority: NORMAL
 *     accept-cancelled: false
 *     item-source:
 *       - main-hand
 *       - off-hand
 * ```
 *
 * 可选：用 custom-source 以事件表达式显式绑定玩家与触发物品，覆盖 item-source 的启发式解析。
 * 注意：Bukkit 事件里没有 "killer" getter，EntityDeathEvent 拿不到击杀者；绑定近战攻击方请用
 * EntityDamageByEntityEvent 的 event.damager（攻击者必须是玩家才生效）。
 * ```
 * custom-events:
 *   onPlayerAttack:
 *     class: EntityDamageByEntityEvent
 *     custom-source:
 *       player: event.damager
 *       bItem: event.damager.itemInMainHand
 * ```
 */
object CustomEventListener {

    enum class ItemSource(val key: String) {
        MAIN_HAND("main-hand"),
        OFF_HAND("off-hand"),
        BOTH_HANDS("both-hands"),
        ARMOR("armor"),
        EVENT("event"),
        INVENTORY("inventory");

        companion object {
            private val byKey = entries.associateBy { it.key }
            fun parse(value: String): ItemSource? = byKey[value.lowercase().replace('_', '-')]
        }
    }

    /** 内置触发名（itemAction / itemActionRiptide 已注册），custom-events.yml 禁止注册同名，否则同一 Bukkit 事件会被双重触发 */
    private val builtInTriggers = setOf(
        "onClick", "onRightClick", "onLeftClick", "onShoot", "onLogin", "onMine",
        "onAttack", "onConsume", "onBreak", "onDrop", "onPickUp", "onInventoryClick",
        "onSwap", "onShootTrident", "onSneak"
    )

    /** 小写化后的内置触发名集合，用于大小写不敏感的冲突检测 */
    private val builtInTriggersLower = builtInTriggers.map { it.lowercase() }.toSet()

    private data class Registration(
        val trigger: String,
        val eventClass: Class<out Event>,
        val priority: EventPriority,
        val ignoreCancelled: Boolean,
        val itemSources: List<ItemSource>,
        val customSource: CustomSource? = null,
    )

    /**
     * custom-source：用事件表达式显式绑定玩家与触发物品，覆盖默认的启发式解析。
     * player / bItem 为「event.<getter/字段路径>」，如 event.player、event.killer、event.player.itemInMainHand。
     */
    private data class CustomSource(
        val playerExpr: String?,
        val bItemExpr: String?,
    ) {
        /** 将 "event.player.itemInMainHand" 拆成 [player, itemInMainHand]；根必须是 event */
        val playerTokens: List<String>? = parseExprTokens(playerExpr)
        val bItemTokens: List<String>? = parseExprTokens(bItemExpr)
    }

    /** 已解析出的物品；discriminator 用于区分同一次事件里不同来源/槽位的物品，作为节流 key 的一部分 */
    private data class ResolvedItem(val discriminator: String, val item: ItemStack)

    /** 共享 Listener 实例，仅用于自定义事件注册，可整体反注册 */
    private val dummyListener: Listener = object : Listener {}

    private val registrations = mutableListOf<Registration>()

    private val itemGetterNames = arrayOf(
        "getItem", "getItemStack", "getItemInHand", "getItemInMainHand", "getItemInOffHand",
        "getCurrentItem", "getBow", "getArrow", "getWeapon", "getItemDrop", "getBrokenItem"
    )

    /** 读取 custom-events.yml 并注册全部自定义事件监听器 */
    fun registerAll() {
        unregisterAll()
        val root = CustomEventsConfig.config
        val plugin = BukkitPlugin.getInstance()
        for (key in root.getKeys(false)) {
            val sub = root.getConfigurationSection(key) ?: run {
                severeS("Invalid custom event \"$key\": expected a section.")
                continue
            }
            try {
                val reg = parseRegistration(key, sub)
                plugin.server.pluginManager.registerEvent(
                    reg.eventClass,
                    dummyListener,
                    reg.priority,
                    EventExecutor { _, event -> onEvent(reg, event) },
                    plugin,
                    reg.ignoreCancelled
                )
                registrations += reg
                devLog("Registered custom event \"$key\" -> ${reg.eventClass.name}")
            } catch (ex: Exception) {
                severeS("Failed to register custom event \"$key\": ${ex.message}")
            }
        }
        if (registrations.isNotEmpty()) {
            devLog("Registered ${registrations.size} custom event listener(s) in total")
        }
    }

    /** 反注册所有自定义事件监听器 */
    fun unregisterAll() {
        if (registrations.isEmpty()) return
        // HandlerList.unregisterAll(Listener) 为全版本 API（cancelEvents 在 legacy12 编译面不存在）
        try {
            HandlerList.unregisterAll(dummyListener)
        } catch (_: Throwable) {
        }
        registrations.clear()
    }

    private fun parseRegistration(trigger: String, section: ConfigurationSection): Registration {
        // 内置触发名已在 itemAction / itemActionRiptide 注册，同名自定义触发会导致同一事件双重执行
        if (trigger.lowercase() in builtInTriggersLower) {
            error("trigger \"$trigger\" collides with a built-in trigger")
        }
        val className = section.getString("class") ?: section.getString("listen")
            ?: error("missing \"class\"")
        val clazz = ClassAliases.getClass(className)
            ?: error("invalid event class \"$className\"")
        if (!Event::class.java.isAssignableFrom(clazz)) {
            error("\"$className\" is not a Bukkit Event")
        }
        @Suppress("UNCHECKED_CAST")
        val eventClass = clazz as Class<out Event>
        val priority = section.getString("priority")?.let { name ->
            EventPriority.values().firstOrNull { it.name.equals(name, true) }
        } ?: EventPriority.NORMAL
        val acceptCancelled = section.getBoolean("accept-cancelled", false)
        val itemSources = parseItemSources(section.get("item-source"))
        val customSource = parseCustomSource(section.getConfigurationSection("custom-source"))
        return Registration(trigger, eventClass, priority, !acceptCancelled, itemSources, customSource)
    }

    /**
     * 解析 custom-source：player / bItem 用表达式显式绑定。
     * 存在 custom-source 时，item-source 的启发式解析被覆盖（两者同时配置以 custom-source 为准）。
     */
    private fun parseCustomSource(section: ConfigurationSection?): CustomSource? {
        if (section == null) return null
        val player = section.getString("player")?.trim()
        val bItemRaw = section.getString("bItem") ?: section.getString("item")
        val bItem = bItemRaw?.trim()
        if (player.isNullOrBlank() && bItem.isNullOrBlank()) return null
        // 表达式必须从 event 开始，否则运行时恒解析为 null，直接报错便于配置排查
        if (player != null && !player.startsWith("event")) {
            error("custom-source.player must start with \"event.\", got \"$player\"")
        }
        if (player != null && player == "event") {
            error("custom-source.player must include a field path after \"event.\", got \"$player\"")
        }
        if (bItem != null && !bItem.startsWith("event")) {
            error("custom-source.bItem must start with \"event.\", got \"$bItem\"")
        }
        if (bItem != null && bItem == "event") {
            error("custom-source.bItem must include a field path after \"event.\", got \"$bItem\"")
        }
        return CustomSource(player, bItem)
    }

    /** 把 "event.player.itemInMainHand" 拆成求值路径 [player, itemInMainHand]；根必须是 event，否则返回 null */
    private fun parseExprTokens(expr: String?): List<String>? {
        if (expr == null) return null
        val parts = expr.trim().split('.')
        if (parts.firstOrNull() != "event") return null
        val tokens = parts.drop(1)
        // 空列表（如 "event" 无后续字段）视为无效
        if (tokens.isEmpty()) return null
        return tokens
    }

    private fun parseItemSources(raw: Any?): List<ItemSource> {
        val list = mutableListOf<ItemSource>()
        when (raw) {
            null -> {
                list += ItemSource.MAIN_HAND
                list += ItemSource.OFF_HAND
            }
            is String -> ItemSource.parse(raw)?.let { list += it }
            is List<*> -> raw.forEach { ItemSource.parse(it?.toString() ?: "")?.let { v -> list += v } }
        }
        return if (list.isEmpty()) listOf(ItemSource.MAIN_HAND, ItemSource.OFF_HAND) else list
    }

    private fun onEvent(reg: Registration, event: Event) {
        val cs = reg.customSource
        if (cs != null) {
            onEventWithCustomSource(reg, event, cs)
            return
        }
        val player = EventPlayerResolver.resolvePlayer(event)
        if (player == null) {
            // 惰性求值：devMode 关闭时不构造字符串，避免高频事件开销
            devLogBypassCheck { "Custom event \"${reg.trigger}\" (${event.eventName}): no player resolved, skipped." }
            return
        }
        for (resolved in resolveItems(player, event, reg.itemSources)) {
            triggerActions(reg, event, player, resolved.item, resolved.discriminator)
        }
    }

    /** custom-source：用表达式显式绑定 player 与触发物品，彻底绕开启发式解析 */
    private fun onEventWithCustomSource(reg: Registration, event: Event, cs: CustomSource) {
        val player = resolveExpression(event, cs.playerTokens) as? Player
        if (player == null) {
            devLogBypassCheck { "Custom event \"${reg.trigger}\" (${event.eventName}): custom-source.player \"${cs.playerExpr}\" did not resolve to a Player, skipped." }
            return
        }
        val item = when (val v = resolveExpression(event, cs.bItemTokens)) {
            is ItemStack -> v
            is Item -> v.itemStack
            else -> null
        }
        if (item == null) {
            devLogBypassCheck { "Custom event \"${reg.trigger}\" (${event.eventName}): custom-source.bItem \"${cs.bItemExpr}\" did not resolve to an ItemStack, skipped." }
            return
        }
        triggerActions(reg, event, player, item, "custom")
    }

    /** 统一入口：过滤非 Sertraline 物品并触发节流动作 */
    private fun triggerActions(reg: Registration, event: Event, player: Player, item: ItemStack, discriminator: String) {
        if (item.isNotExist()) return
        // 与内置 itemAction 一致：不提前读 NBT。sertraline_id 解析 + actions 门控放在节流回调内的
        // applyActions 里（applyActions 内部只读一次 NBT）；否则 inventory/armor 源在高频事件下
        // 会对每个槽位每次事件都做一次深度 NBT 解析，而其中绝大多数槽位根本不是 Sertraline 物品
        throttleAction(
            ThrottleActionLink(player.uniqueId.toString(), reg.trigger, discriminator),
            ThrottleActionParam(player, event, event as? Cancellable, item)
        )
    }

    /**
     * 解析物品并附带槽位描述（discriminator）。
     * discriminator 会进入节流 key，同一玩家同一触发名下不同槽位的物品各自独立节流，
     * 避免多物品来源（both-hands / armor / inventory）只有第一个物品的动作被触发。
     */
    private fun resolveItems(player: Player, event: Event, sources: List<ItemSource>): List<ResolvedItem> {
        val items = ArrayList<ResolvedItem>()
        val inv = player.inventory
        for (source in sources) {
            when (source) {
                ItemSource.MAIN_HAND -> items.add(ResolvedItem(ItemSource.MAIN_HAND.key, inv.itemInMainHand))
                ItemSource.OFF_HAND -> items.add(ResolvedItem(ItemSource.OFF_HAND.key, inv.itemInOffHand))
                ItemSource.BOTH_HANDS -> {
                    items.add(ResolvedItem(ItemSource.MAIN_HAND.key, inv.itemInMainHand))
                    items.add(ResolvedItem(ItemSource.OFF_HAND.key, inv.itemInOffHand))
                }
                ItemSource.ARMOR -> {
                    // armorContents 顺序固定为 boots, leggings, chestplate, helmet
                    val slotNames = arrayOf("boots", "leggings", "chestplate", "helmet")
                    val armor = inv.armorContents
                    for (i in armor.indices) {
                        val slot = armor[i] ?: continue
                        items.add(ResolvedItem("armor:${slotNames[i]}", slot))
                    }
                }
                ItemSource.INVENTORY -> {
                    val contents = inv.contents
                    for (i in contents.indices) {
                        val slot = contents[i] ?: continue
                        items.add(ResolvedItem("inventory:$i", slot))
                    }
                }
                ItemSource.EVENT -> resolveEventItem(event)?.let { items.add(ResolvedItem(ItemSource.EVENT.key, it)) }
            }
        }
        // 同一来源/槽位可能被重复列出（如 both-hands + main-hand 同时配置），按 discriminator 去重
        return items.distinctBy { it.discriminator }
    }

    private fun resolveEventItem(event: Event): ItemStack? {
        for (name in itemGetterNames) {
            when (val v = EventPlayerResolver.invokeGetter(event, name)) {
                is ItemStack -> return v
                is Item -> return v.itemStack
            }
        }
        return null
    }

    /** 按点分路径求值事件表达式（如 "event.player.itemInMainHand"）；根必须是 event */
    private fun resolveExpression(event: Event, tokens: List<String>?): Any? {
        if (tokens == null) return null
        var current: Any? = event
        for (token in tokens) {
            current = resolveToken(current, token) ?: return null
        }
        return current
    }

    /**
     * 单步解析：依次尝试 getX / isX / 原样方法名；对象上找不到时经 getInventory / getEquipment 容器再找一次
     * （Player 没有 getItemInMainHand，但 player.inventory.itemInMainHand 有），最后兜底读取字段。
     * 方法名可带括号（如 getItemInMainHand()）。
     */
    private fun resolveToken(target: Any?, rawToken: String): Any? {
        if (target == null) return null
        val token = rawToken.removeSuffix("()")
        val capitalized = token.replaceFirstChar { it.uppercase() }
        for (name in arrayOf("get$capitalized", "is$capitalized", token)) {
            EventPlayerResolver.invokeGetter(target, name)?.let { return it }
        }
        // 常见 Bukkit 模式：物品相关 getter 挂在 inventory/equipment 上（如 event.player.itemInMainHand）
        for (container in arrayOf("getInventory", "getEquipment")) {
            val holder = EventPlayerResolver.invokeGetter(target, container) ?: continue
            EventPlayerResolver.invokeGetter(holder, "get$capitalized")?.let { return it }
        }
        return readField(target, token)
    }

    /** 读取字段（含私有字段），找不到返回 null */
    private fun readField(target: Any, name: String): Any? {
        var clazz: Class<*>? = target.javaClass
        while (clazz != null && clazz != Any::class.java) {
            for (field in clazz.declaredFields) {
                if (field.name != name) continue
                try {
                    field.isAccessible = true
                    return field.get(target)
                } catch (_: Throwable) {
                    return null
                }
            }
            clazz = clazz.superclass
        }
        return null
    }
}
