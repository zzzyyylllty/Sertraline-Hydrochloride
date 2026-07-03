package io.github.zzzyyylllty.sertraline.gui

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.Sertraline.craftingStations
import io.github.zzzyyylllty.sertraline.data.*
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherBoolean
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherString
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import io.github.zzzyyylllty.sertraline.util.GraalJsUtil
import io.github.zzzyyylllty.sertraline.util.ScriptHelper
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.service.PlatformExecutor.PlatformTask
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Chest
import taboolib.platform.util.giveItem
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * 合成站 GUI 管理器。
 * 负责：打开合成站界面、显示配方、处理合成流程（条件检查→等待→产出）。
 */
object CraftingStationManager {

    /** 活跃的合成会话: playerId → CraftingSession */
    private val activeSessions = ConcurrentHashMap<String, CraftingSession>()

    /** 唯一会话令牌生成器 */
    private val nextSessionToken = AtomicLong(0)

    /**
     * 打开合成站 GUI。
     */
    fun openStation(player: Player, stationId: String) {
        val station = craftingStations[stationId]
        if (station == null) {
            player.sendMessage("<red>Station '$stationId' not found.</red>".toComponent())
            return
        }
        openStation(player, station, stationId, 0)
    }

    /**
     * 打开合成站 GUI（指定页面）。
     */
    fun openStation(player: Player, station: CraftingStation, stationId: String, page: Int) {
        // 检查旧的合成会话
        val oldSession = activeSessions[player.uniqueId.toString()]
        if (oldSession != null) {
            val oldRecipe = station.recipes.values.find { it.displayName == oldSession.recipeId }
            val cancellable = oldRecipe?.options?.get("cancellable") as? Boolean ?: true
            if (!cancellable) {
                player.sendMessage("<yellow>Current craft cannot be cancelled!</yellow>".toComponent())
                return
            }
            // 取消旧的合成会话并返还已消耗的材料
            activeSessions.remove(player.uniqueId.toString())
            oldSession.itemsReturned.set(true)
            oldSession.cancel() // 取消调度任务，防止延迟执行后重复产出
            CraftingStationDataManager.clearSession(player)
            returnConsumedItems(player, oldSession)
        }

        val display = station.display
        val recipes = station.recipes.values.toList()
        val totalRecipes = recipes.size
        val pageSize = display.layout.joinToString("").count { it.toString() == display.key }

        val totalPages = if (pageSize > 0) ((totalRecipes - 1) / pageSize) + 1 else 1
        val currentPage = page.coerceIn(0, (totalPages - 1).coerceAtLeast(0))
        val pageRecipes = if (pageSize > 0) {
            val from = currentPage * pageSize
            val to = (from + pageSize).coerceAtMost(totalRecipes)
            if (from < totalRecipes) recipes.subList(from, to) else emptyList()
        } else emptyList()

        // 计算所有可用的物品槽位
        val itemSlots = mutableListOf<Int>()
        for (rowIdx in display.layout.indices) {
            val row = display.layout[rowIdx]
            for (colIdx in row.indices) {
                if (row[colIdx].toString() == display.key) {
                    itemSlots.add(rowIdx * 9 + colIdx)
                }
            }
        }

        // 检测配方槽位与元素槽位冲突
        val elementSlots = mutableSetOf<Int>()
        for ((_, elementConfig) in display.elements) {
            if (elementConfig.slot != null) {
                elementSlots.add(elementConfig.slot)
            } else if (elementConfig.char != null) {
                val c = elementConfig.char[0].toString()
                if (c != display.key) {
                    for (rowIdx in display.layout.indices) {
                        val row = display.layout[rowIdx]
                        for (colIdx in row.indices) {
                            if (row[colIdx].toString() == c) {
                                elementSlots.add(rowIdx * 9 + colIdx)
                            }
                        }
                    }
                }
            }
        }
        val overlap = itemSlots.toSet().intersect(elementSlots)
        if (overlap.isNotEmpty()) {
            warningS("CraftingStation '$stationId': slot collision between element and recipe slots: $overlap")
        }

        player.openMenu<Chest>(display.title) {
            rows(display.layout.size)

            // 渲染布局
            map(*display.layout.toTypedArray())

            // 渲染背景元素
            for ((elementId, elementConfig) in display.elements) {
                if (elementConfig.slot != null) {
                    val item = buildElementItem(elementConfig, elementId, player, currentPage, totalPages)
                    set(elementConfig.slot, item) {
                        handleElementClick(clicker, station, stationId, elementConfig, elementId, currentPage, totalPages)
                    }
                } else if (elementConfig.char != null) {
                    val char = elementConfig.char[0]
                    val item = buildElementItem(elementConfig, elementId, player, currentPage, totalPages)
                    set(char, item) {
                        handleElementClick(clicker, station, stationId, elementConfig, elementId, currentPage, totalPages)
                    }
                }
            }

            // 填充配方物品
            for ((index, recipe) in pageRecipes.withIndex()) {
                if (index < itemSlots.size) {
                    val recipeSlot = itemSlots[index]
                    val item = buildRecipeItem(recipe, player)
                    set(recipeSlot, item) {
                        handleRecipeClick(clicker, station, stationId, recipe, recipeSlot)
                    }
                }
            }
        }
    }

    // ==================== 元素物品构建 ====================

    private fun buildElementItem(
        config: ElementConfig,
        elementId: String,
        player: Player,
        currentPage: Int,
        totalPages: Int
    ): ItemStack {
        val material = try {
            org.bukkit.Material.valueOf(config.material.uppercase())
        } catch (_: Exception) {
            org.bukkit.Material.GRAY_STAINED_GLASS_PANE
        }
        val item = ItemStack(material)
        val meta = item.itemMeta ?: return item

        val resolvedName = config.name
            .replace("{page}", (currentPage + 1).toString())
            .replace("{total_pages}", totalPages.toString())
            .replace("{next_page}", (currentPage + 2).coerceAtMost(totalPages).toString())
            .replace("{previous_page}", currentPage.toString())
            .toComponent()
        meta.displayName(resolvedName)

        if (config.lore != null) {
            val resolvedLore = config.lore
                .replace("{page}", (currentPage + 1).toString())
                .replace("{total_pages}", totalPages.toString())
                .replace("{next_page}", (currentPage + 2).coerceAtMost(totalPages).toString())
                .replace("{previous_page}", currentPage.toString())
            // 支持多行 lore（用 \n 分隔）
            val loreLines = resolvedLore.split("\n").map { it.toComponent() }
            meta.lore(loreLines)
        }

        item.itemMeta = meta
        return item
    }

    private fun buildRecipeItem(recipe: StationRecipe, player: Player): ItemStack {
        val displayItem = recipe.display
        if (displayItem != null) {
            return displayItem.build(player)
        }
        // 使用第一个输出作为显示
        if (recipe.outputs.isNotEmpty()) {
            return recipe.outputs.first().build(player)
        }
        return ItemStack(org.bukkit.Material.GRASS_BLOCK)
    }

    // ==================== 元素点击处理 ====================

    private fun handleElementClick(
        player: Player,
        station: CraftingStation,
        stationId: String,
        config: ElementConfig,
        elementId: String,
        currentPage: Int,
        totalPages: Int
    ) {
        val roll = config.roll ?: false
        when (elementId) {
            "next_page", "next-page" -> {
                if (currentPage + 1 < totalPages) {
                    openStation(player, station, stationId, currentPage + 1)
                } else if (roll && totalPages > 1) {
                    // roll: true 时循环回第一页
                    openStation(player, station, stationId, 0)
                }
            }
            "previous_page", "previous-page", "prev_page", "prev-page" -> {
                if (currentPage > 0) {
                    openStation(player, station, stationId, currentPage - 1)
                } else if (roll && totalPages > 1) {
                    // roll: true 时循环到最后一页
                    openStation(player, station, stationId, totalPages - 1)
                }
            }
        }

        // 执行 agents
        if (config.agents != null) {
            for ((key, script) in config.agents) {
                executeAgentScript(key, script, player, mapOf(
                    "page" to (currentPage + 1),
                    "total_pages" to totalPages,
                    "station_id" to stationId
                ))
            }
        }
    }

    // ==================== 配方点击 & 合成流程 ====================

    private fun handleRecipeClick(player: Player, station: CraftingStation, stationId: String, recipe: StationRecipe, slot: Int) {
        val sessionKey = player.uniqueId.toString()

        // 检查是否已有合成中
        val existing = activeSessions[sessionKey]
        if (existing != null) {
            val msg = Sertraline.config.getString("messages.crafting-already-active", "<yellow>You already have an active crafting process!</yellow>") ?: "<yellow>You already have an active crafting process!</yellow>"
            player.sendMessage(msg.toComponent())
            return
        }

        // 检查条件
        val conditions = recipe.conditions
        if (conditions != null && conditions.isNotEmpty()) {
            val conditionResults = checkConditions(conditions, player, recipe)
            val failed = conditionResults.filter { !it.passed }
            if (failed.isNotEmpty()) {
                for (fail in failed) {
                    val conditionType = fail.condition.type.lowercase()
                    val message = recipe.messages?.let { msgs ->
                        msgs["ConditionNotMet${fail.condition.type}"]
                            ?: msgs["ConditionNotMet${conditionType.replaceFirstChar { it.uppercase() }}"]
                            ?: msgs["ConditionNotMet${conditionType}"]
                    } ?: Sertraline.config.getString("messages.condition-not-met")
                        ?: "<red>Condition not met: {condition_name}</red>"
                    player.sendMessage(message
                        .replace("{condition_name}", fail.condition.name)
                        .replace("{condition_amount}", fail.condition.amount)
                        .replace("{condition_required}", fail.condition.required)
                        .replace("{condition_type}", fail.condition.type)
                        .toComponent()
                    )
                    if (fail.condition.agents != null) {
                        for ((key, script) in fail.condition.agents) {
                            if (key.equals("onFail", ignoreCase = true)) {
                                executeAgentScript(key, script, player, mapOf("recipe" to recipe.displayName))
                            }
                        }
                    }
                }
                return
            }
            for (condition in conditions) {
                if (condition.agents != null) {
                    for ((key, script) in condition.agents) {
                        if (key.equals("onSuccess", ignoreCase = true)) {
                            executeAgentScript(key, script, player, mapOf("recipe" to recipe.displayName))
                        }
                    }
                }
            }
        }

        // 原子化检查并消耗输入物品（修复 TOCTOU 竞态条件）
        val consumed = tryConsumeInputs(player, recipe)
        if (consumed == null) {
            val missingName = recipe.inputs.firstOrNull()?.displayName?.ifEmpty { recipe.inputs.firstOrNull()?.input?.item } ?: "unknown"
            val msg = Sertraline.config.getString("messages.missing-ingredients", "<red>Missing ingredients: {display_name}</red>") ?: "<red>Missing ingredients: {display_name}</red>"
            player.sendMessage(msg.replace("{display_name}", missingName).toComponent())
            return
        }

        // 解析合成时间
        val timeSeconds = try {
            recipe.time.evalKetherString(player)?.toDoubleOrNull() ?: 0.0
        } catch (_: Exception) {
            try { recipe.time.toDouble() } catch (_: Exception) { 0.0 }
        }
        val timeTicks = (timeSeconds * 20).toInt().coerceAtLeast(1)

        // 执行 onStart agents（配方级 → 合成站级）
        fireRecipeAgents(recipe, "onStart", player)
        fireStationAgents(station, recipe, "onStart", player)

        // 取消标记 + 会话令牌
        val itemsReturned = AtomicBoolean(false)
        val sessionToken = nextSessionToken.incrementAndGet()

        // 使用调度延迟替代 Thread.sleep()，避免占用线程池线程
        val task = submit(async = true, delay = timeTicks.toLong()) {
            // 检查会话是否已被取消（openStation / cancelAll 已处理归还）
            val currentSession = activeSessions[sessionKey]
            if (currentSession == null || currentSession.sessionToken != sessionToken) {
                return@submit
            }

            // 移除会话，防止重复处理
            activeSessions.remove(sessionKey, currentSession)

            // 产出物品（必须在主线程）
            submit(async = false) {
                try {
                    for (output in recipe.outputs) {
                        val outputStack = output.build(player)
                        player.giveItem(outputStack)
                    }

                    fireRecipeAgents(recipe, "onClaim", player)
                    fireStationAgents(station, recipe, "onClaim", player)

                    val msg = Sertraline.config.getString("messages.crafting-complete", "<green>Crafting complete!</green>") ?: "<green>Crafting complete!</green>"
                    player.sendMessage(msg.toComponent())
                } finally {
                    CraftingStationDataManager.clearSession(player)
                }
            }
        }

        activeSessions[sessionKey] = CraftingSession(
            playerId = sessionKey,
            sessionToken = sessionToken,
            stationId = stationId,
            recipeId = recipe.displayName,
            totalTicks = timeTicks,
            task = task,
            consumedItems = consumed.toMutableList(),
            itemsReturned = itemsReturned
        )

        // 持久化合成会话到数据库
        val startTime = System.currentTimeMillis()
        CraftingStationDataManager.saveSession(
            player = player,
            stationId = stationId,
            recipeId = recipe.displayName,
            startTime = startTime,
            totalSeconds = timeSeconds,
            consumedItems = consumed.map { it.item }
        )

        val msg = Sertraline.config.getString("messages.crafting-started", "<yellow>Crafting started... ({time} s)</yellow>") ?: "<yellow>Crafting started... ({time} s)</yellow>"
        player.sendMessage(msg.replace("{time}", "%.1f".format(timeSeconds)).toComponent())
    }

    // ==================== 条件检查 ====================

    private data class ConditionResult(val condition: ConditionConfig, val passed: Boolean)

    private fun checkConditions(conditions: List<ConditionConfig>, player: Player, recipe: StationRecipe): List<ConditionResult> {
        return conditions.map { condition ->
            val passed = try {
                when (condition.type.lowercase()) {
                    "scaled" -> {
                        val amount = condition.amount.evalKetherString(player)
                        val required = condition.required.evalKetherString(player)
                        amount?.toDoubleOrNull() ?: 0.0 >= required?.toDoubleOrNull() ?: 0.0
                    }
                    "boolean", "kether" -> {
                        condition.condition.evalKetherBoolean(player)
                    }
                    "permission" -> {
                        player.hasPermission(condition.condition)
                    }
                    else -> {
                        condition.condition.evalKetherBoolean(player)
                    }
                }
            } catch (_: Exception) {
                false
            }
            ConditionResult(condition, passed)
        }
    }

    // ==================== 输入检查 & 消耗 ====================

    data class ConsumedItem(
        val slot: Int,
        val item: ItemStack
    )

    /**
     * 原子化检查并消耗输入物品。
     * 遍历所有配方输入，若能全部满足则消耗并返回 ConsumedItem 列表；
     * 若任一输入不足则回滚所有已做的修改并返回 null（修复 TOCTOU 竞态条件）。
     */
    private fun tryConsumeInputs(player: Player, recipe: StationRecipe): List<ConsumedItem>? {
        val inventory = player.inventory
        val snapshot = mutableMapOf<Int, ItemStack>()
        val allConsumed = mutableListOf<ConsumedItem>()

        for (input in recipe.inputs) {
            val targetStack = input.input.build(player)
            var remaining = targetStack.amount
            val toRemove = mutableListOf<Int>()

            val contents = inventory.contents
            for ((i, item) in contents.withIndex()) {
                if (remaining <= 0) break
                if (item != null && item.isSimilar(targetStack)) {
                    if (i !in snapshot) snapshot[i] = item.clone()

                    val takeAmount = minOf(item.amount, remaining)
                    allConsumed.add(ConsumedItem(i, item.clone().also { it.amount = takeAmount }))

                    if (item.amount <= remaining) {
                        remaining -= item.amount
                        toRemove.add(i)
                    } else {
                        item.amount -= remaining
                        remaining = 0
                    }
                }
            }

            if (remaining > 0) {
                for ((slot, original) in snapshot) {
                    inventory.setItem(slot, original)
                }
                return null
            }

            for (slot in toRemove) {
                inventory.setItem(slot, null)
            }
        }

        return allConsumed
    }

    /**
     * 返还被消耗的物品到玩家背包（合成取消/中断时调用）。
     */
    private fun returnConsumedItems(player: Player, consumed: List<ConsumedItem>, recipeName: String? = null) {
        if (consumed.isEmpty()) return
        if (!player.isOnline) {
            warningS("Cannot return items to offline player ${player.name} (recipe: ${recipeName ?: "unknown"})")
            return
        }
        for (ci in consumed) {
            player.inventory.addItem(ci.item).values.forEach { leftover ->
                // 如果背包满了，掉落在地上
                player.world.dropItem(player.location, leftover)
            }
        }
    }

    /**
     * 从旧会话中返还已消耗的物品（取消旧合成时调用）。
     */
    private fun returnConsumedItems(player: Player, session: CraftingSession) {
        if (!player.isOnline) return
        returnConsumedItems(player, session.consumedItems, session.recipeId)
    }

    // ==================== Agent 执行 ====================

    /**
     * 执行合成站级 agents。
     */
    private fun fireStationAgents(station: CraftingStation, recipe: StationRecipe, action: String, player: Player) {
        station.agents?.forEach { (key, script) ->
            if (key.equals(action, ignoreCase = true)) {
                executeAgentScript(key, script, player, mapOf(
                    "recipe" to recipe.displayName,
                    "station_id" to (station.display.title)
                ))
            }
        }
    }

    /**
     * 执行配方级 agents。
     */
    private fun fireRecipeAgents(recipe: StationRecipe, action: String, player: Player) {
        recipe.agents?.forEach { (key, script) ->
            if (key.equals(action, ignoreCase = true)) {
                executeAgentScript(key, script, player, mapOf("recipe" to recipe.displayName))
            }
        }
    }

    /**
     * 公开入口：供 restoreSession 在会话恢复时触发生命周期 agents。
     */
    fun fireLifecycleAgents(station: CraftingStation, recipe: StationRecipe, action: String, player: Player) {
        fireRecipeAgents(recipe, action, player)
        fireStationAgents(station, recipe, action, player)
    }

    private fun executeAgentScript(key: String, script: String, player: Player, vars: Map<String, Any?>) {
        submitAsync {
            try {
                val trimmed = script.trimStart()
                when {
                    // GraalJS: prefix with "gjs:" or "graaljs:"
                    trimmed.startsWith("gjs:") || trimmed.startsWith("graaljs:") -> {
                        val jsVars = defaultData + vars + mapOf("player" to player, "bukkitPlayer" to player)
                        GraalJsUtil.cachedEval(trimmed.substringAfter(":").trimStart(), jsVars)
                    }
                    // Standard JSR 223 JavaScript: prefix with "js:" or "javascript:"
                    trimmed.startsWith("js:") || trimmed.startsWith("javascript:") -> {
                        val jsVars = defaultData + vars + mapOf("player" to player, "bukkitPlayer" to player)
                        val jsScript = trimmed.substringAfter(":").trimStart()
                        val engine = ScriptHelper.engineManager.getEngineByName("js")
                        if (engine != null) {
                            val bindings = engine.createBindings()
                            jsVars.forEach { (k, v) -> bindings[k] = v }
                            engine.eval(jsScript, bindings)
                        } else {
                            severeS("JavaScript engine not available for crafting station agent '$key'")
                        }
                    }
                    // Default: Kether (backward compatible)
                    else -> {
                        script.lines().evalKether(player, vars)
                    }
                }
            } catch (e: Exception) {
                severeS("CraftingStation agent '$key' execution failed: ${e.message}")
            }
        }
    }

    // ==================== 合成会话 ====================

    data class CraftingSession(
        val playerId: String,
        val sessionToken: Long,
        val stationId: String,
        val recipeId: String,
        val totalTicks: Int,
        private val task: PlatformTask,
        val consumedItems: MutableList<ConsumedItem> = mutableListOf(),
        /** 标记已取消/已返还材料，防止异步任务重复返还 */
        val itemsReturned: AtomicBoolean = AtomicBoolean(false)
    ) {
        fun cancel() {
            task.cancel()
        }
    }

    /** 取消所有在线的合成任务并归还物品（用于 reload） */
    fun cancelAll() {
        val onlinePlayers = mutableMapOf<String, CraftingSession>()
        activeSessions.values.forEach { session ->
            session.cancel()
            val player = org.bukkit.Bukkit.getPlayer(java.util.UUID.fromString(session.playerId))
            if (player != null) {
                onlinePlayers[session.playerId] = session
            }
            // 离线玩家：保留 DB 数据，加入时 restoreSession 处理
        }
        activeSessions.clear()

        if (onlinePlayers.isEmpty()) return

        // 同步归还 + 清 DB，阻塞直到完成。防止后续 craftingStations.clear() 与 restoreSession 竞态。
        try {
            val plugin = org.bukkit.Bukkit.getPluginManager().getPlugin("Sertraline")
            if (plugin != null) {
                org.bukkit.Bukkit.getScheduler().callSyncMethod(plugin, java.util.concurrent.Callable {
                    for ((playerId, session) in onlinePlayers) {
                        val player = org.bukkit.Bukkit.getPlayer(java.util.UUID.fromString(playerId))
                        if (player != null) {
                            returnConsumedItems(player, session)
                            CraftingStationDataManager.clearSession(player)
                        }
                    }
                    null
                }).get()
            }
        } catch (e: Exception) {
            severeS("Failed to synchronously cancel crafting sessions: ${e.message}")
        }
    }

    /** 关闭所有合成任务但不清除持久化数据（用于服务器关闭） */
    fun shutdownAll() {
        activeSessions.values.forEach { it.cancel() }
        activeSessions.clear()
    }

    /** 取消指定玩家的合成会话（玩家退出时调用） */
    fun cancelPlayerSession(playerId: String) {
        val session = activeSessions.remove(playerId) ?: return
        session.cancel()
    }
}
