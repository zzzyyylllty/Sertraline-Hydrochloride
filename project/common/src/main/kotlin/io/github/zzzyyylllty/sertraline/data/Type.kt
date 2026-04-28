package io.github.zzzyyylllty.sertraline.data

/**
 * 物品类型数据类
 * @property id 类型ID，用于在配置和物品中引用
 * @property name 类型显示名称
 * @property parent 父类型ID，用于继承（可选）
 * @property description 类型描述
 * @property extra 额外属性
 */
data class Type(
    val id: String,
    val name: String,
    val parent: String? = null,
    val description: String = "",
    val extra: Map<String, Any?> = mapOf()
)