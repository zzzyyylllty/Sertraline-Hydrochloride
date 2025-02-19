package io.github.zzzyyylllty.data.compatiblility

data class MMOItemsComp(
    val type: String,
    val id: String,
    val revid: Long,
    val tier: String
)
/*
*
  compatibility:
    mmoitems: # 添加 MMOItems NBT以与大部分支持MI的插件自动进行兼容，使用该选项你必须关闭MI的物品删除自动失效功能。
      type: 'CONSUMABLE'
      id: 'DEPAZ_PILLS'
      revid: 114514 # 如果你不想让你的物品被MI自动更新，关闭MI的自动更新功能或确保它不低于MI内同名同种物品。
      tier: RARE
      * */