package io.github.zzzyyylllty.data

import taboolib.module.kether.Kether


data class ActionsData(
    val type: ActionsType = ActionsType.KETHER,
    val async: Boolean = true,
    val chance: Double = 100.0,
    val value: ArrayList<String>?,
    val cancelEvent: Boolean = false
)

enum class ActionsType{
    KETHER,
    COMMAND_PLAYER,
    COMMAND_CONSOLE,
    COMMAND_OP,
    MYTHICMOBS_SKILL,
    REFRESH,
    REGENERATE,
    JAVASCRIPT
}
/*
*
        # 支持的类型：
        # kether               / 不填写   运行 Kether 脚本 https://kether.tabooproject.org/list.html
        # command_player       / cmd_p   以玩家身份执行命令
        # command_console      / cmd_c   以控制台身份执行命令
        # command_op           / cmd_o   以管理员身份执行命令，不推荐使用，管理员命令可能导致夺权，概不负责
        # mythicmobs_skill     / mmskill 实施 MYTHICMOBS 技能
        # refresh              / reload  重载物品，只会更改物品的lore
        # regenerate           / regen   重新生成物品，包括数据和一切属性!
        # JS
        * */