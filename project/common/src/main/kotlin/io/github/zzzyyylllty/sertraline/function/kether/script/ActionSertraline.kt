package io.github.zzzyyylllty.sertraline.function.kether.script

import io.github.zzzyyylllty.sertraline.function.data.getSertralineId
import io.github.zzzyyylllty.sertraline.function.kether.getBukkitPlayer
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import org.bukkit.inventory.ItemStack
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestReader
import taboolib.module.kether.*
import taboolib.platform.util.giveItem
import taboolib.platform.util.isNotAir
import java.util.concurrent.CompletableFuture

/**
 * Sertraline Kether actions.
 *
 * Usage:
 *   sertraline give <itemId> [to <player>] [amount <n>]
 *   sertraline get <itemId> [amount <n>]
 *   sertraline take <itemId> [from <player>] [amount <n>]
 *   sertraline has <itemId> [amount <n>]
 *   sertraline count <itemId>
 *   sertraline id
 */
class ActionSertraline {

    class Give(val itemId: String, val playerName: ParsedAction<*>?, val amount: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val player = frame.getBukkitPlayer(playerName)
            return frame.run(amount).int { amt ->
                val item = sertralineItemBuilder(itemId, player, amount = amt)
                    ?: error("Sertraline item not found: $itemId")
                player.giveItem(item)
            }.thenApply { null }
        }
    }

    class Take(val itemId: String, val playerName: ParsedAction<*>?, val amount: ParsedAction<*>) : ScriptAction<Int>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Int> {
            val player = frame.getBukkitPlayer(playerName)
            return frame.run(amount).int { amt ->
                var remaining = amt
                val inv = player.inventory
                for (i in 0 until inv.size) {
                    val stack = inv.getItem(i) ?: continue
                    if (!stack.isNotAir()) continue
                    if (stack.getSertralineId() == itemId) {
                        val take = minOf(remaining, stack.amount)
                        stack.amount -= take
                        remaining -= take
                        if (remaining <= 0) break
                    }
                }
                amt - remaining
            }
        }
    }

    class Has(val itemId: String, val playerName: ParsedAction<*>?, val amount: ParsedAction<*>) : ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            val player = frame.getBukkitPlayer(playerName)
            return frame.run(amount).int { amt ->
                player.inventory.contents
                    .filterNotNull()
                    .filter { it.isNotAir() && it.getSertralineId() == itemId }
                    .sumOf { it.amount } >= amt
            }
        }
    }

    class Count(val itemId: String, val playerName: ParsedAction<*>?) : ScriptAction<Int>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Int> {
            val player = frame.getBukkitPlayer(playerName)
            return CompletableFuture.completedFuture(
                player.inventory.contents
                    .filterNotNull()
                    .filter { it.isNotAir() && it.getSertralineId() == itemId }
                    .sumOf { it.amount }
            )
        }
    }

    class Get(val itemId: String, val playerName: ParsedAction<*>?, val amount: ParsedAction<*>) : ScriptAction<ItemStack>() {
        override fun run(frame: ScriptFrame): CompletableFuture<ItemStack> {
            val player = frame.getBukkitPlayer(playerName)
            return frame.run(amount).int { amt ->
                sertralineItemBuilder(itemId, player, amount = amt)
                    ?: error("Sertraline item not found: $itemId")
            }
        }
    }

    sealed class IdSource {
        data object Hand : IdSource()
        data object Offhand : IdSource()
        data object Helmet : IdSource()
        data object Chestplate : IdSource()
        data object Leggings : IdSource()
        data object Boots : IdSource()
        data class FromAction(val action: ParsedAction<*>) : IdSource()
    }

    class Id(val playerName: ParsedAction<*>?, val source: IdSource) : ScriptAction<String?>() {
        override fun run(frame: ScriptFrame): CompletableFuture<String?> {
            val player = frame.getBukkitPlayer(playerName)
            val stack = when (val s = source) {
                is IdSource.Hand -> player.inventory.itemInMainHand
                is IdSource.Offhand -> player.inventory.itemInOffHand
                is IdSource.Helmet -> player.inventory.helmet
                is IdSource.Chestplate -> player.inventory.chestplate
                is IdSource.Leggings -> player.inventory.leggings
                is IdSource.Boots -> player.inventory.boots
                is IdSource.FromAction -> return frame.run(s.action).thenApply {
                    (it as? ItemStack)?.getSertralineId()
                }
            }
            return CompletableFuture.completedFuture(stack?.getSertralineId())
        }
    }

    companion object {

        @KetherParser(["sertraline"], shared = true)
        fun parser() = scriptParser {
            when (val action = it.nextToken()) {
                "give" -> {
                    val itemId = it.nextToken()
                    val (playerName, amount) = parseOptionalPlayerAndAmount(it)
                    Give(itemId, playerName, amount)
                }
                "take", "remove" -> {
                    val itemId = it.nextToken()
                    val (playerName, amount) = parseOptionalPlayerAndAmount(it)
                    Take(itemId, playerName, amount)
                }
                "has", "have", "check" -> {
                    val itemId = it.nextToken()
                    val (playerName, amount) = parseOptionalPlayerAndAmount(it)
                    Has(itemId, playerName, amount)
                }
                "count", "amount" -> {
                    val itemId = it.nextToken()
                    val playerName = parseOptionalPlayer(it)
                    Count(itemId, playerName)
                }
                "get" -> {
                    val itemId = it.nextToken()
                    val (playerName, amount) = parseOptionalPlayerAndAmount(it)
                    Get(itemId, playerName, amount)
                }
                "id" -> {
                    parseIdAction(it)
                }
                else -> error("Unknown sertraline action: $action (expected: give, get, take, has, count, id)")
            }
        }

        private fun parseIdAction(reader: QuestReader): Id {
            return try {
                reader.mark()
                reader.expects("by", "from")
                // matched — parse explicit source
                reader.reset()
                reader.mark()
                when (reader.nextToken()) {
                    "by" -> {
                        val slot = reader.nextToken().lowercase()
                        val source = when (slot) {
                            "hand" -> IdSource.Hand
                            "offhand", "off_hand" -> IdSource.Offhand
                            "helmet", "head" -> IdSource.Helmet
                            "chestplate", "chest" -> IdSource.Chestplate
                            "leggings", "legs" -> IdSource.Leggings
                            "boots", "feet" -> IdSource.Boots
                            else -> error("Unknown equipment slot: $slot")
                        }
                        val playerName = parseOptionalPlayer(reader)
                        Id(playerName, source)
                    }
                    "from" -> {
                        val action = reader.nextParsedAction()
                        Id(null, IdSource.FromAction(action))
                    }
                    else -> error("out of case")
                }
            } catch (_: Exception) {
                reader.reset()
                Id(parseOptionalPlayer(reader), IdSource.Hand)
            }
        }

        private fun parseOptionalPlayer(reader: QuestReader): ParsedAction<*>? = try {
            reader.mark()
            reader.expects("from", "to", "of")
            val name = reader.nextParsedAction()
            name
        } catch (_: Exception) {
            reader.reset()
            null
        }

        private fun parseOptionalPlayerAndAmount(reader: QuestReader): Pair<ParsedAction<*>?, ParsedAction<*>> {
            var playerName: ParsedAction<*>? = null
            var amount: ParsedAction<*> = literalAction(1)
            try {
                reader.mark()
                when (reader.expects("from", "to", "of", "amount")) {
                    "from", "to", "of" -> {
                        playerName = reader.nextParsedAction()
                        reader.mark()
                        try {
                            reader.expect("amount")
                            amount = reader.nextParsedAction()
                        } catch (_: Exception) {
                            reader.reset()
                        }
                    }
                    "amount" -> {
                        amount = reader.nextParsedAction()
                    }
                }
            } catch (_: Exception) {
                reader.reset()
            }
            return playerName to amount
        }
    }
}
