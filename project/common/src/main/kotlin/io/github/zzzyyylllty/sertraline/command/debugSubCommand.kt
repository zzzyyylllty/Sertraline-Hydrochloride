package io.github.zzzyyylllty.sertraline.command

import io.github.zzzyyylllty.embiancomponent.EmbianComponent.SafetyComponentSetter
import io.github.zzzyyylllty.embiancomponent.tools.getComponentsNMSFiltered
import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.itemCache
import io.github.zzzyyylllty.sertraline.Sertraline.itemManager
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.Sertraline.mappings
import io.github.zzzyyylllty.sertraline.Sertraline.tagManager
import io.github.zzzyyylllty.sertraline.item.rebuild
import io.github.zzzyyylllty.sertraline.item.rebuildLore
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import io.github.zzzyyylllty.sertraline.logger.sendStringAsComponent
import io.github.zzzyyylllty.sertraline.logger.sendStringAsComponent
import io.github.zzzyyylllty.sertraline.impl.getComponentsNMS
import io.github.zzzyyylllty.sertraline.util.ComponentFormatter
import io.github.zzzyyylllty.sertraline.util.ItemTagManager
import io.github.zzzyyylllty.sertraline.util.dependencies.AttributeUtil.refreshAttributes
import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.bool
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.asList
import taboolib.expansion.setupDataContainer
import taboolib.module.nms.NMSItemTag.Companion.asNMSCopy
import taboolib.module.nms.getItemTag
import taboolib.platform.util.asLangText
import taboolib.platform.util.giveItem
import taboolib.platform.util.isAir

@CommandHeader(
    name = "sertralinedebug",
    aliases = ["itemdebug","needyitemdebug","depazdebug"],
    permission = "sertraline.command.debug",
    description = "DEBUG Command of DepazItems.",
    permissionMessage = "",
    permissionDefault = PermissionDefault.OP,
    newParser = false,
)
object DebugCommand {

    @CommandBody
    val main = mainCommand {
        createModernHelper()
    }

    @CommandBody
    val help = subCommand {
        createModernHelper()
    }

    @CommandBody
    val testTags = subCommand {
        execute<CommandSender> { sender, context, argument ->
            // 注册自定义标签
            ItemTagManager.registerCustomTag("myplugin:special_items", listOf("minecraft:diamond_sword", "minecraft:netherite_sword"))

            // 给物品添加自定义标签
            ItemTagManager.addItemToCustomTag("minecraft:golden_apple", "myplugin:food_items")

            // 获取某个标签的所有物品
            val axes = ItemTagManager.getItemsByTag("minecraft:axes")
            sender.sendStringAsComponent("Axes: $axes")

            val specialItems = ItemTagManager.getItemsByTag("myplugin:special_items")
            sender.sendStringAsComponent("Special items: $specialItems")

            // 检查物品是否有标签
            val hasTag = ItemTagManager.hasItemTag("minecraft:diamond_sword", "minecraft:axes")
            sender.sendStringAsComponent("Diamond sword is an axe: $hasTag")
        }
    }
    @CommandBody
    val getMappings = subCommand {
        execute<CommandSender> { sender, context, argument ->
            var message = mappings.toString()
            sender.sendStringAsComponent(message)
        }
    }
    @CommandBody
    val getItems = subCommand {
        execute<CommandSender> { sender, context, argument ->
            var message = itemMap.toString()
            sender.sendStringAsComponent(message)
        }
    }
    @CommandBody
    val getItemCaches = subCommand {
        execute<CommandSender> { sender, context, argument ->
            var message = itemCache.toString()
            sender.sendStringAsComponent(message)
        }
    }

    @CommandBody
    val getItem = subCommand {
        dynamic("id") {
            execute<CommandSender> { sender, context, argument ->
                val id = context["id"]
                val message = itemMap[id].toString()
                sender.sendStringAsComponent(message)
            }
            suggestion<CommandSender>(uncheck = true) { sender, context ->
                itemMap.keys.asList()
            }
        }
    }

    @CommandBody
    val getItemIDs = subCommand {
        execute<CommandSender> { sender, context, argument ->
            val message = itemMap.keys.toString()
            sender.sendStringAsComponent(message)
        }

    }
    @CommandBody
    val getConfig = subCommand {
        execute<CommandSender> { sender, context, argument ->
            sender.sendStringAsComponent(config.toString())
        }
    }
    @CommandBody
    val giveTestItem = subCommand {
        execute<CommandSender> { sender, context, argument ->
            val p = sender as Player
            p.giveItem(sertralineItemBuilder("depaz_pills",p))
        }
    }
    @CommandBody
    val processors = subCommand {
        execute<CommandSender> { sender, context, argument ->
            sender.sendStringAsComponent(itemManager.listProcessors().toString())
        }
    }
    @CommandBody
    val tagProcessors = subCommand {
        execute<CommandSender> { sender, context, argument ->
            sender.sendStringAsComponent(tagManager.listProcessors().toString())
        }
    }
    @CommandBody
    val rebuild = subCommand {
        execute<CommandSender> { sender, context, argument ->
            submitAsync {
                val inv = (sender as Player).inventory
                val hand = inv.itemInMainHand.rebuild(sender)
                inv.setItemInMainHand(hand)
            }
        }
    }

    @CommandBody
    val rebuildLore = subCommand {
        execute<CommandSender> { sender, context, argument ->
            submitAsync {
                val inv = (sender as Player).inventory
                inv.itemInMainHand.rebuildLore(sender)
            }
        }
    }

    @CommandBody
    val refreshAttributes = subCommand {
        execute<CommandSender> { sender, context, argument ->
            refreshAttributes(sender as Player)
        }
    }

    @CommandBody
    val dumpComponent = subCommand {
        execute<CommandSender> { sender, context, argument ->
            val inv = (sender as Player).inventory
            val item = inv.itemInMainHand
            if (item.isAir || item.isEmpty || item.amount <= 0) {
                sender.sendMessage("item cannot be null")
                return@execute
            }
            val components = asNMSCopy(item).getComponentsNMSFiltered()
            sender.sendMessage(ComponentFormatter.formatComponentMap(components))
        }
        bool("full") {
            execute<CommandSender> { sender, context, argument ->
                val inv = (sender as Player).inventory
                val item = inv.itemInMainHand
                if (item.isAir || item.isEmpty || item.amount <= 0) {
                    sender.sendMessage("item cannot be null")
                    return@execute
                }
                if (context.bool("full")) {
                    val components = asNMSCopy(item).getComponentsNMS()
                    sender.sendMessage(ComponentFormatter.formatComponentMap(components))
                } else {
                    val components = asNMSCopy(item).getComponentsNMSFiltered()
                    sender.sendMessage(ComponentFormatter.formatComponentMap(components))
                }
            }
            suggestion<CommandSender>(uncheck = true) { sender, context ->
                listOf("true", "false")
            }
        }
    }

    @CommandBody
    val testNewComponent = subCommand {
        execute<CommandSender> { sender, context, argument ->
            var item = ItemStack(Material.DIAMOND_CHESTPLATE)
            item = SafetyComponentSetter.setComponent(item,"minecraft:custom_data", mapOf<String, Any>("test" to "abc"))!!
            item = SafetyComponentSetter.removeComponent(item,"minecraft:attribute_modifiers")!! // 163
            sender.sendStringAsComponent("<yellow>Filtered: <gray>${SafetyComponentSetter.getAllComponentsFiltered(item)}")
            sender.sendStringAsComponent("<yellow>Original: <gray>${SafetyComponentSetter.getAllComponents(item)}")
            sender.sendStringAsComponent("<yellow>GetJava: <gray>${SafetyComponentSetter.getComponentJava<Map<*,*>>(item, "minecraft:custom_data")}")
            sender.sendStringAsComponent("<yellow>GetJson: <gray>${SafetyComponentSetter.getComponent(item, "minecraft:custom_data")}")
            (sender as Player).giveItem(item)
        }
    }
    @CommandBody
    val testTabooComponent = subCommand {
        execute<CommandSender> { sender, context, argument ->
            val inv = (sender as Player).inventory
            val item = inv.itemInMainHand
            sender.sendMessage("GetTag: ${item.getItemTag(true)}")
        }
    }
    @CommandBody
    val testTabooComponentAll = subCommand {
        execute<CommandSender> { sender, context, argument ->
            val inv = (sender as Player).inventory
            val item = inv.itemInMainHand
            sender.sendMessage("GetTag - NOT OnlyCustom: ${item.getItemTag(false)}")
        }
    }

    @CommandBody
    val setupDataContainer = subCommand {
        execute<CommandSender> { sender, context, argument ->
            (sender as Player).setupDataContainer()
        }
    }

    @CommandBody
    val buildDisplay = subCommand {
        dynamic("id") {
            execute<CommandSender> { sender, context, argument ->
                val id = context["id"]
                if (sender is Player) {
                    sertralineItemBuilder(
                        id,
                        sender
                    )?.let {
                        sender.sendMessage(it.displayName())
                    } ?: sender.sendStringAsComponent("Item not exist.")
                } else {
                    sender.sendStringAsComponent("Must a player.")
                }
            }
            suggestion<CommandSender>(uncheck = true) { sender, context ->
                itemMap.keys.asList()
            }
            player("player") {
                execute<CommandSender> { sender, context, argument ->
                    val id = context["id"]
                    val tabooPlayer = context.player("player")
                    // 转化为Bukkit的Player
                    val bukkitPlayer = tabooPlayer.castSafely<Player>()
                    sertralineItemBuilder(
                        id,
                        bukkitPlayer
                    )?.let {
                        sender.sendMessage(it.displayName())
                    } ?: sender.sendStringAsComponent("Item not exist.")
                }
            }
        }
    }

}