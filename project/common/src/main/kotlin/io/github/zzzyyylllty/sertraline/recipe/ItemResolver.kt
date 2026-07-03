package io.github.zzzyyylllty.sertraline.recipe

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.data.RecipeIngredient
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import io.github.zzzyyylllty.sertraline.util.ExternalItemHelper
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice

/**
 * 将 [RecipeIngredient] 解析为 Bukkit [RecipeChoice]，
 * 支持 Sertraline 物品、原版物品、标签和外部插件物品。
 */
object ItemResolver {

    /** 已知的外部插件命名空间（不通过 Sertraline itemMap 解析） */
    private val knownExternalNamespaces = setOf(
        "itemsadder", "oraxen", "nexo", "mythicmobs",
        "mmoitems", "ecoitems", "executableitems", "neigeitems",
        "azureflow", "craftengine"
    )

    /**
     * 将配料定义解析为 [RecipeChoice]。
     * - [RecipeIngredient.Item] → ExactChoice（Sertraline / 外部）或 MaterialChoice（原版）
     * - [RecipeIngredient.Tag] → MaterialChoice(Tag)
     * - [RecipeIngredient.Choice] → 合并所有选项
     */
    fun resolve(ingredient: RecipeIngredient): RecipeChoice {
        return when (ingredient) {
            is RecipeIngredient.Item -> resolveItem(ingredient.itemId, ingredient.amount)
            is RecipeIngredient.Tag -> resolveTag(ingredient.tagId, ingredient.amount)
            is RecipeIngredient.Choice -> resolveChoice(ingredient)
        }
    }

    private fun resolveItem(itemId: String, amount: Int): RecipeChoice {
        val (namespace, key) = parseId(itemId)

        return when {
            // 原版物品 → MaterialChoice
            namespace == "minecraft" || namespace == "vanilla" -> {
                val material = Material.matchMaterial(key)
                    ?: throw IllegalArgumentException("Unknown vanilla material: $itemId")
                RecipeChoice.MaterialChoice(material)
            }
            // Sertraline 自有物品 → ExactChoice（带 NBT）
            namespace == "sertraline" -> {
                val sItem = Sertraline.itemMap[key]
                    ?: throw IllegalArgumentException("Unknown Sertraline item: $itemId")
                val stack = buildSertralineStack(sItem, amount)
                RecipeChoice.ExactChoice(stack)
            }
            // 外部插件物品 → ExactChoice
            else -> {
                val stack = buildExternalStack(namespace, key, amount)
                RecipeChoice.ExactChoice(stack)
            }
        }
    }

    private fun resolveTag(tagId: String, amount: Int): RecipeChoice {
        val normalized = normalizeTagKey(tagId)
        val tag = resolveBukkitTag(normalized)
            ?: throw IllegalArgumentException("Unknown tag: $tagId (normalized: $normalized)")
        return RecipeChoice.MaterialChoice(tag)
    }

    @Suppress("UNCHECKED_CAST")
    private fun resolveChoice(choice: RecipeIngredient.Choice): RecipeChoice {
        val choices = choice.options.map { resolve(it) }
        if (choices.isEmpty()) throw IllegalArgumentException("Choice must have at least one option")

        // 收集 MaterialChoice / ExactChoice
        val materials = mutableListOf<Material>()
        val exactStacks = mutableListOf<ItemStack>()
        var materialChoiceCount = 0
        var exactChoiceCount = 0

        for (c in choices) {
            when (c) {
                is RecipeChoice.MaterialChoice -> {
                    @Suppress("DEPRECATION")
                    materials.addAll(c.choices)
                    materialChoiceCount++
                }
                is RecipeChoice.ExactChoice -> {
                    exactStacks.addAll(c.choices)
                    exactChoiceCount++
                }
            }
        }

        return when {
            exactChoiceCount > 0 && materialChoiceCount == 0 ->
                RecipeChoice.ExactChoice(exactStacks)
            materialChoiceCount > 0 && exactChoiceCount == 0 ->
                RecipeChoice.MaterialChoice(materials)
            else -> error("Mixed choice types cannot be merged: ${materialChoiceCount} MaterialChoice + ${exactChoiceCount} ExactChoice")
        }
    }

    // ---- 内部辅助 ----

    private fun parseId(id: String): Pair<String, String> {
        val idx = id.indexOf(':')
        return if (idx == -1) {
            "minecraft" to id.lowercase()
        } else {
            id.substring(0, idx).lowercase() to id.substring(idx + 1)
        }
    }

    /**
     * 规范化标签 Key：
     * - #minecraft:planks  →  minecraft:planks
     * - tag:planks        →  minecraft:planks
     * - planks            →  minecraft:planks
     */
    private fun normalizeTagKey(key: String): String {
        var n = key.trim()
        if (n.startsWith('#')) n = n.substring(1)
        if (n.startsWith("tag:")) n = "minecraft:" + n.substring(4)
        if (!n.contains(":")) n = "minecraft:$n"
        return n
    }

    @Suppress("UNCHECKED_CAST")
    private fun resolveBukkitTag(normalized: String): Tag<Material>? {
        return try {
            // 尝试 Bukkit 注册表中的 Tag
            val key = org.bukkit.NamespacedKey.fromString(normalized) ?: return null
            val tagField = Tag::class.java.getDeclaredField(normalized
                .substringAfter(':')
                .uppercase()
                .replace('/', '_')
            )
            tagField.isAccessible = true
            tagField.get(null) as? Tag<Material>
        } catch (_: Exception) {
            // 通过 Bukkit.getTag 兜底
            try {
                val key = org.bukkit.NamespacedKey.fromString(normalized) ?: return null
                @Suppress("DEPRECATION")
                org.bukkit.Bukkit.getTag(Tag.REGISTRY_ITEMS, key, Material::class.java)
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun buildSertralineStack(sItem: ModernSItem, amount: Int): ItemStack {
        val stack = sertralineItemBuilder(sItem.key, null)
            ?: throw IllegalArgumentException("Failed to build Sertraline item: ${sItem.key}")
        stack.amount = amount
        return stack
    }

    private fun buildExternalStack(namespace: String, key: String, amount: Int): ItemStack {
        val stack = ExternalItemHelper.buildNoPlayer(namespace, key)
            ?: throw IllegalArgumentException("Failed to build external item: $namespace:$key")
        stack.amount = amount
        return stack
    }

    /**
     * 将字符串格式的配料描述直接解析为 [RecipeChoice]。
     * 此方法供外部调用（如配置解析临时使用）。
     */
    fun parseString(choiceStr: String): RecipeChoice {
        val trimmed = choiceStr.trim()
        return when {
            trimmed.startsWith("#") || trimmed.startsWith("tag:") -> {
                resolveTag(trimmed, 1)
            }
            trimmed.contains(":") -> {
                resolveItem(trimmed, 1)
            }
            else -> {
                resolveItem("minecraft:$trimmed", 1)
            }
        }
    }
}
