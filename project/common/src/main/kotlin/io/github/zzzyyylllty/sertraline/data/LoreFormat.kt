package io.github.zzzyyylllty.sertraline.data

import org.bukkit.inventory.EquipmentSlot
import javax.script.CompiledScript
import java.util.LinkedHashMap

/**
 * Base interface for all lore format types.
 * Every lore format (plain or switchable) carries a [LoreSetting] that controls
 * overwrite/visual/skip-blank/compare behaviour.
 */
interface AbstractLoreFormat {
    val settings: LoreSetting
}

/**
 * A plain (non-switchable) lore format: a fixed list of [elements] to render.
 *
 * @param elements  Ordered list of lore elements (plain text, key-bound, or complex).
 */
data class LoreFormat(
    override val settings: LoreSetting,
    val elements: List<AbstractLoreElement> = emptyList()
) : AbstractLoreFormat

/**
 * A switchable lore format that evaluates pre-variables and then iterates through
 * [forks] until one matches. The matched fork's target [AbstractLoreFormat] is
 * applied with optional [settingsOverride] merging.
 *
 * @param preVariables  JS expressions evaluated once per render; results are bound
 *                      for fork conditions to use.
 * @param forks         Ordered list of (condition, target) pairs. The first fork
 *                      whose condition evaluates to true wins.
 * @param settingsOverride  Optional settings that override the resolved target's own settings.
 */
data class SwitchableLoreFormat(
    override val settings: LoreSetting,
    val preVariables: LinkedHashMap<String, CompiledScript> = linkedMapOf(),
    val forks: List<SwitchFork> = listOf<SwitchFork>(),
    val settingsOverride: LoreSettingOverride? = null,
) : AbstractLoreFormat

/**
 * Partial override of [LoreSetting] fields, used by switchable formats to
 * merge settings onto the resolved child format.
 */
data class LoreSettingOverride(
    val overwrite: Boolean? = null,
    val visual: Boolean? = null,
    val skipBlank: Boolean? = null,
    val compareEnabled: Boolean = false,
    val compare: CompareSetting? = null
)

/**
 * A single fork inside a switchable lore format.
 *
 * @param condition    Compiled JS; when true, [toLoreFormat] is applied.
 * @param toLoreFormat The target lore format to render when this fork matches.
 */
data class SwitchFork(
    val condition: CompiledScript,
    val toLoreFormat: AbstractLoreFormat,
)

/**
 * Controls how a lore format behaves.
 *
 * @param overwrite       If true, replaces the original item lore entirely.
 *                        If false, the generated lore is appended to the original.
 * @param visual          If true, applied only in packet-mode (client-side view).
 *                        If false, applied server-side (item meta).
 * @param skipBlank       If true, consecutive blank lines are collapsed into one.
 * @param compareEnabled  If true, per-element compare resolution is active.
 * @param compare         Global compare configuration (target resolution + condition groups).
 */
data class LoreSetting(
    val overwrite: Boolean = true,
    val visual: Boolean = true,
    val skipBlank: Boolean = true,
    val compareEnabled: Boolean = false,
    val compare: CompareSetting? = null
)

// ── Element hierarchy ──────────────────────────────────────────

/**
 * Base sealed class for a single lore line or block.
 *
 * @param content  The raw content template (MiniMessage format).
 *                 May contain placeholders like {plus}, {value}, {type:name}, etc.
 */
sealed class AbstractLoreElement(open val content: String) {
    /**
     * The data key that provides the value(s) for this element.
     * - Non-null: value is read from the item's data tree at this path.
     * - Null:     the element is always rendered using [content] as-is.
     */
    abstract val key: String?
}

/**
 * A static lore element with no key binding — always rendered as-is.
 */
class PlainLoreElement(content: String) : AbstractLoreElement(content) {
    override val key: String? = null
}

/**
 * A lore element bound to a data key, but without [LineMode] filtering or compare.
 * If the key points to a list, each list item is formatted with [content].
 */
class CommonLoreElement(
    content: String,
    override val key: String?
) : AbstractLoreElement(content)

/**
 * A lore element with optional [LineMode] filtering, per-element JS parsing,
 * and/or compare-based content override.
 *
 * @param lineMode     Controls how [lineRequire] keys are evaluated (ANY/ALL/NOT/NOT_ALL).
 * @param lineRequire  List of data keys checked against [lineMode] to decide visibility.
 * @param jsParses     Pre-compiled JS transformations keyed by name (unused currently).
 * @param compare      Per-element compare settings; when present and [LoreSetting.compareEnabled]
 *                     is true, the content may be overridden based on a diff against a target item.
 */
class ComplexLoreElement(
    content: String,
    override val key: String?,
    val lineMode: LineMode?,
    val lineRequire: List<String>?,
    val jsParses: HashMap<String, CompiledScript>?,
    val compare: CompareElementSetting? = null
) : AbstractLoreElement(content)

// ── Compare (target resolution) ────────────────────────────────

/**
 * Base sealed class for a "when" entry in the compare target resolution chain.
 * Each entry describes how to locate the target item for comparison.
 */
sealed class CompareTargetWhen {
    /** Placeholder string that identifies the resolved target (e.g. "%fixed%", "%equipment-slot%"). */
    abstract val then: String
}

/**
 * Maps a user-facing equipment slot name to a Bukkit [EquipmentSlot].
 *
 * Accepts the common verbatim names (HEAD, CHEST, LEGS, FEET, OFF_HAND, ...) and
 * all of the aliases users naturally type for the main hand:
 * "MAIN_HAND", "MAINHAND", and plain "HAND" all map to [EquipmentSlot.HAND]
 * (Bukkit has no MAIN_HAND constant on legacy API surfaces).
 * Unknown names return null.
 */
fun parseCompareSlot(name: String): EquipmentSlot? {
    return when (name.uppercase()) {
        "MAIN_HAND", "MAINHAND", "HAND" -> EquipmentSlot.HAND
        else -> runCatching { EquipmentSlot.valueOf(name.uppercase()) }.getOrNull()
    }
}

/**
 * Resolves the target item from an equipment slot name stored in the current item's
 * data under "sertraline:compare-path" (e.g. "MAIN_HAND", "OFF_HAND", "HEAD").
 * The target is the item the player currently has equipped in that slot.
 */
class CompareTargetFixedPath : CompareTargetWhen() {
    override val then: String = "%fixed%"
}

/**
 * Resolves the target item by checking which [EquipmentSlot] the current item
 * occupies on the player, then reading the item in that slot.
 */
class CompareTargetEquipmentSlot(
    val slots: List<EquipmentSlot>
) : CompareTargetWhen() {
    override val then: String = "%equipment-slot%"
}

/**
 * Describes how to resolve the target item for comparison.
 *
 * @param type   Strategy type (currently only "simple").
 * @param whens  Ordered list of strategies tried in sequence; the first successful
 *               one provides the target item.
 * @param filter Optional boolean gate evaluated before any comparison work. If it
 *               decides DENY, the whole comparison is skipped for this render
 *               (equivalent to compare-enabled = false).
 */
data class CompareTarget(
    val type: String = "simple",
    val whens: List<CompareTargetWhen>,
    val filter: CompareTargetFilter? = null
)

/**
 * A boolean gate attached to [CompareTarget.filter] that decides whether comparison
 * should run at all for the current render. Entries are evaluated top-to-bottom;
 * the first matching entry wins.
 */
class CompareTargetFilter(
    val whens: List<CompareTargetFilterWhen>
)

/**
 * A single filter entry: when [condition] is matched, [then] decides the outcome.
 * DENY skips the whole comparison; ALLOW proceeds and stops evaluating any further entries.
 */
class CompareTargetFilterWhen(
    val condition: CompareTargetFilterCondition,
    val then: CompareTargetFilterDecision
)

/**
 * The condition of a single [CompareTargetFilterWhen] entry.
 */
sealed class CompareTargetFilterCondition {
    /** Always matches — used for bare `then: deny` entries. */
    data object Always : CompareTargetFilterCondition()

    /** JavaScript expression ([CompiledScript]). */
    data class Script(val compiled: CompiledScript) : CompareTargetFilterCondition()

    /** self-compare: true — currentItem == compareItem. */
    data object SelfCompare : CompareTargetFilterCondition()

    /** lore-format: "a" / "a\nb" / [a, b] — compareItem's lore-format must match ANY listed name. */
    data class LoreFormat(val names: Set<String>) : CompareTargetFilterCondition()
}

/**
 * The verdict of a single [CompareTargetFilterWhen] entry.
 */
enum class CompareTargetFilterDecision {
    ALLOW, DENY
}

// ── Compare (condition groups) ──────────────────────────────────

/**
 * Base sealed class for a named compare condition group.
 * A group is evaluated to produce a result string (e.g. "high", "low", "equal")
 * which is then used to look up the content override in [CompareElementSetting.contents].
 */
sealed class CompareConditionGroup

/**
 * A data-driven compare group: ordered list of [DataCompareCondition] entries.
 * The first matching entry's result is returned.
 */
class DataCompareGroup(
    val conditions: List<DataCompareCondition>
) : CompareConditionGroup()

/**
 * A script-driven compare group: a compiled JS expression that returns the result string.
 *
 * Available variables: currentValue, compareValue, differentValue, player, sItem.
 */
class ScriptCompareGroup(
    val script: CompiledScript
) : CompareConditionGroup()

// ── Compare (settings) ──────────────────────────────────────────

/**
 * Top-level compare configuration attached to a [LoreSetting].
 *
 * @param target    Describes how to locate the item to compare against.
 * @param compares  Named compare groups (keyed by name, referenced by [CompareElementSetting.compareName]).
 */
class CompareSetting(
    val target: CompareTarget?,
    val compares: LinkedHashMap<String, CompareConditionGroup>
)

/**
 * Per-element compare configuration.
 *
 * @param contents     Map of result string → content template.
 *                     The result from the compare evaluation (e.g. "high", "low") is used
 *                     as a key; if found, the corresponding content overrides the element's
 *                     default content. Supports {diff}, {diff_abs}, {diff_percent_abs}, {symbol} placeholders.
 * @param currentId    Path to the current item's numeric value. Falls back to the element's [key] if null.
 * @param compareId    Path to the target item's numeric value. Falls back to the element's [key] if null.
 * @param compareName  Key into [CompareSetting.compares] that selects which condition group to use.
 *                     Defaults to "default".
 */
class CompareElementSetting(
    val contents: LinkedHashMap<String, String>,
    val currentId: String?,
    val compareId: String?,
    val compareName: String = "default"
)

// ── Compare (conditions) ────────────────────────────────────────

/**
 * Base sealed class for a single compare condition.
 */
sealed class CompareCondition {
    /** The result string returned when this condition matches (e.g. "high", "low", "equal"). */
    abstract val result: String
}

/**
 * A single data-driven compare condition.
 *
 * @param operator      Comparison operator (EQUALS, GREATER_THAN, etc.).
 *                      Null means this condition is a result-limit-only entry (NEW/LOST).
 * @param value         The threshold value (for BETWEEN the format is "low~high").
 * @param result        Result string returned when this condition matches.
 * @param resultLimit   Controls when this condition is evaluated:
 *                      DEFAULT — both values must exist;
 *                      NEW — only when the target lacks the stat;
 *                      LOST — only when the current item lacks the stat.
 * @param defaultValue  Default value used when a side is missing and resultLimit is DEFAULT.
 * @param parsedLow     Pre-parsed lower bound for BETWEEN operator (set at load time).
 * @param parsedHigh    Pre-parsed upper bound for BETWEEN operator (set at load time).
 * @param parsedValue   Pre-parsed numeric value (set at load time), avoids re-parsing on every comparison.
 */
data class DataCompareCondition(
    val operator: CompareOp? = null,
    val value: String? = null,
    override val result: String,
    val resultLimit: ResultLimit = ResultLimit.DEFAULT,
    val defaultValue: Double = 0.0,
    val parsedLow: Double? = null,
    val parsedHigh: Double? = null,
    val parsedValue: Double? = null,
) : CompareCondition()

// ── Enums ──────────────────────────────────────────────────────

/**
 * Numeric comparison operators used in [DataCompareCondition].
 */
enum class CompareOp {
    EQUAL, NOT_EQUAL, GREATER_THAN,
    GREATER_EQUAL, LESS_THAN, LESS_EQUAL,
    BETWEEN
}

/**
 * Controls when a compare condition is eligible for evaluation.
 *
 * DEFAULT — Both the current and target values must exist; absent sides are treated as [DataCompareCondition.defaultValue].
 * NEW     — Only evaluated when the current item has the stat but the target does not.
 * LOST    — Only evaluated when the target has the stat but the current item does not.
 */
enum class ResultLimit {
    DEFAULT,
    NEW,
    LOST
}

/**
 * Controls how [ComplexLoreElement.lineRequire] keys are evaluated for element visibility.
 *
 * ANY     — Show if at least one required key exists.
 * ALL     — Show only if all required keys exist.
 * NOT     — Show only if none of the required keys exist.
 * NOT_ALL — Show if at least one required key does not exist.
 */
enum class LineMode {
    ANY,
    ALL,
    NOT,
    NOT_ALL
}