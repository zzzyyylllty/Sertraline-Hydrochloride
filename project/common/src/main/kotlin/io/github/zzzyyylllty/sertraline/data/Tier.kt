package io.github.zzzyyylllty.sertraline.data

/**
 * 物品品质数据类
 * @property id 品质ID，用于在配置和物品中引用
 * @property name 品质显示名称
 * @property description 品质描述
 * @property color 品质颜色（MiniMessage格式）
 * @property weight 品质权重，用于随机选择等
 * @property extra 额外属性
 */
data class Tier(
    val id: String,
    val name: String,
    val description: String = "",
    val color: String = "<white>",
    val weight: Int = 1,
    val extra: Map<String, Any?> = mapOf()
)