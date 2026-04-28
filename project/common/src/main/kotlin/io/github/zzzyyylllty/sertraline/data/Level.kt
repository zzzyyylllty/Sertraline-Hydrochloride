package io.github.zzzyyylllty.sertraline.data

/**
 * 物品等级数据类
 * @property id 等级ID，用于在配置和物品中引用
 * @property name 等级显示名称
 * @property description 等级描述
 * @property color 等级颜色（MiniMessage格式）
 * @property weight 等级权重，用于随机选择等
 * @property extra 额外属性
 */
data class Level(
    val id: String,
    val name: String,
    val description: String = "",
    val color: String = "<white>",
    val weight: Int = 1,
    val extra: Map<String, Any?> = mapOf()
)