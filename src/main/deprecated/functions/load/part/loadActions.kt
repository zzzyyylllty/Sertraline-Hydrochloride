package io.github.zzzyyylllty.functions.load.part

import com.willfp.eco.util.toSingletonList
import github.saukiya.tools.base.EmptyMap
import io.github.zzzyyylllty.SertralineHydrochloride.console
import io.github.zzzyyylllty.data.*
import io.github.zzzyyylllty.debugMode.debugLog
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.lang.asLangText

fun loadActions(config: YamlConfiguration,root: String) : LinkedHashMap<String, SingleActionsData> {

    val section = "$root.actions" // = testItem.sertraline
    val source = "${config.name}-$root"

    val actionsList : LinkedHashMap<String, SingleActionsData> = LinkedHashMap<String, SingleActionsData>()

    val actions = (config[section] ?: run {
        debugLog(console.asLangText("debug.load.no_actions"))
        return actionsList
    }) as List<ConfigurationSection>

    for (ac in actions) {
        actionsList[ac["trigger"] as String] = SingleActionsData(
            ActionsType.valueOf(ac["type"].toString()),
            (ac["async"] ?: true) as Boolean,
            (ac["chance"] ?: 100.0) as Double,
            ac["value"].toSingletonList() as List<String>,
            (ac["cancel-event"] ?: false) as Boolean,
        )
    }

    return actionsList
}
/*
*     actions: # 动作，不储存在NBT
    left-click @ non-shift: # 左键点击且非shift
      - type: KETHER  # 执行什么类型的动作?
        # 支持的类型：
        # KETHER               / 不填写   运行 Kether 脚本 https://kether.tabooproject.org/list.html
        # command_player       / CMD_P   以玩家身份执行命令
        # command_console      / CMD_C   以控制台身份执行命令
        # command_op           / CMD_O   以管理员身份执行命令，不推荐使用，管理员命令可能导致夺权，概不负责
        # mythicmobs_skill     / MMSKILL 实施 MYTHICMOBS 技能
        # REFRESH              / RELOAD  重载物品，只会更改物品的lore
        # REGENERATE           / REGEN   重新生成物品，包括数据和一切属性!
        # JAVASCRIPT           / JS      JavaScript!
        async: true   # 是否异步执行，默认true
        chance: 100.0 # 运行的概率，默认100
        value:
          - 'tell Hello!'
* */