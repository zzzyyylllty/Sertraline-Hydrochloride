package io.github.zzzyyylllty.sertraline.function.fluxon.script

import io.github.zzzyyylllty.sertraline.Sertraline.fluxonInst
import io.github.zzzyyylllty.sertraline.function.fluxon.script.FunctionBukkit.FluxonBukkitObject
import io.github.zzzyyylllty.sertraline.util.minimessage.mmJsonUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmLegacyAmpersandUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmLegacySectionUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmStrictUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import io.github.zzzyyylllty.sertraline.util.toUpperCase
import io.papermc.paper.registry.RegistryKey
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.FunctionContext
import org.tabooproject.fluxon.runtime.NativeFunction.NativeCallable
import java.util.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.library.xseries.XMaterial


@Awake(LifeCycle.ENABLE)
fun registerExtensionItemStack() {
    ExtensionItemStack.init(fluxonInst!!)
}

object ExtensionItemStack {

    fun init(runtime: FluxonRuntime) {
        runtime.registerExtension(ItemStack::class.java)
            // getType
            .function("type", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                if (ctx.argumentCount == 0) {
                    item.type
                } else {
                    item.type = XMaterial.valueOf(ctx.getString(0)!!).get()!!
                    null
                }
            })

            // getAmount
            .function("amount", -1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                if (ctx.argumentCount == 0) {
                    item.amount
                } else {
                    item.amount = ctx.getNumber(0).toInt()
                    null
                }
            })

            
            // getType
            .function("getType", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                item.type
            })

            // getAmount
            .function("getAmount", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                item.amount
            })

            // setAmount
            .function("setAmount", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val amt = ctx.getNumber(0).toInt()
                item.amount = amt
                null
            })

            // getDurability (deprecated)
            .function("getDurability", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                item.durability
            })

            // setDurability (deprecated)
            .function("setDurability", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val dur = ctx.getNumber(0).toShort()
                item.setDurability(dur)
                null
            })

            // hasItemMeta
            .function("hasItemMeta", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                item.hasItemMeta()
            })

            // getItemMeta
            .function("getItemMeta", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                item.itemMeta
            })

            // setItemMeta
            .function("setItemMeta", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val meta = ctx.getArgument(0)
                if (meta != null && meta !is ItemMeta)
                    throw IllegalArgumentException("setItemMeta: argument must be ItemMeta or null")
                item.setItemMeta(meta)
                null
            })

            // getLegacyLore (deprecated, List<String>?)
            .function("getLegacyLore", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                if (!item.hasItemMeta()) return@NativeCallable null
                val meta = item.itemMeta!!
                if (!meta.hasLore()) return@NativeCallable null
                meta.lore?.map { it.toString() }
            })

            // getLore (modern, List<Component>?)
            .function("getLore", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                if (!item.hasItemMeta()) return@NativeCallable null
                val meta = item.itemMeta!!
                if (!meta.hasLore()) return@NativeCallable null
                meta.lore()
            })

            // setLegacyLore (List<String>?)
            .function("setLegacyLore", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val lore = ctx.getArgument(0)
                val meta = item.itemMeta ?: throw IllegalStateException("Cannot set lore: ItemMeta is null")
                when (lore) {
                    null -> meta.lore = null
                    is List<*> -> {
                        if (!lore.all { it is String })
                            throw IllegalArgumentException("setLegacyLore: all elements must be String")
                        @Suppress("UNCHECKED_CAST")
                        meta.lore = lore as List<String>
                    }
                    else -> throw IllegalArgumentException("setLegacyLore: argument must be List<String>?")
                }
                item.setItemMeta(meta)
                null
            })

            // setLore (List<Component>?)
            .function("setLore", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val lore = ctx.getArgument(0)
                val meta = item.itemMeta ?: throw IllegalStateException("Cannot set lore: ItemMeta is null")
                when (lore) {
                    null -> meta.lore(null)
                    is List<*> -> {
                        if (!lore.all { it is Component })
                            throw IllegalArgumentException("setLore: all elements must be Component")
                        @Suppress("UNCHECKED_CAST")
                        meta.lore(lore as List<Component>)
                    }
                    else -> throw IllegalArgumentException("setLore: argument must be List<Component>?")
                }
                item.setItemMeta(meta)
                null
            })

            // addItemFlags
            .function("addItemFlags", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val flags = ctx.getArgument(0)
                val meta = item.itemMeta ?: throw IllegalStateException("Cannot add item flags: ItemMeta is null")
                when (flags) {
                    is Collection<*> -> {
                        if (!flags.all { it is ItemFlag })
                            throw IllegalArgumentException("addItemFlags: all elements must be ItemFlag")
                        @Suppress("UNCHECKED_CAST")
                        meta.addItemFlags(*flags.toTypedArray() as Array<ItemFlag>)
                    }
                    is Array<*> -> {
                        if (!flags.all { it is ItemFlag })
                            throw IllegalArgumentException("addItemFlags: all elements must be ItemFlag")
                        @Suppress("UNCHECKED_CAST")
                        meta.addItemFlags(*flags as Array<ItemFlag>)
                    }
                    else -> throw IllegalArgumentException("addItemFlags: argument must be array or collection of ItemFlag")
                }
                item.setItemMeta(meta)
                null
            })

            // removeItemFlags
            .function("removeItemFlags", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val flags = ctx.getArgument(0)
                val meta = item.itemMeta ?: throw IllegalStateException("Cannot remove item flags: ItemMeta is null")
                when (flags) {
                    is Collection<*> -> {
                        if (!flags.all { it is ItemFlag })
                            throw IllegalArgumentException("removeItemFlags: all elements must be ItemFlag")
                        @Suppress("UNCHECKED_CAST")
                        meta.removeItemFlags(*flags.toTypedArray() as Array<ItemFlag>)
                    }
                    is Array<*> -> {
                        if (!flags.all { it is ItemFlag })
                            throw IllegalArgumentException("removeItemFlags: all elements must be ItemFlag")
                        @Suppress("UNCHECKED_CAST")
                        meta.removeItemFlags(*flags as Array<ItemFlag>)
                    }
                    else -> throw IllegalArgumentException("removeItemFlags: argument must be array or collection of ItemFlag")
                }
                item.setItemMeta(meta)
                null
            })

            // getItemFlags
            .function("getItemFlags", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val meta = item.itemMeta ?: return@NativeCallable emptySet<ItemFlag>()
                meta.itemFlags
            })

            // hasItemFlag
            .function("hasItemFlag", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val flag = ctx.getArgument(0)
                if (flag !is ItemFlag) throw IllegalArgumentException("hasItemFlag: argument must be ItemFlag")
                val meta = item.itemMeta ?: return@NativeCallable false
                meta.hasItemFlag(flag)
            })

            // clone
            .function("clone", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                item.clone()
            })

            // equals
            .function("equals", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val other = ctx.getArgument(0)
                item.equals(other)
            })

            // isEmpty
            .function("isEmpty", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                item.isEmpty
            })

            // toString
            .function("toString", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                item.toString()
            })

            // containsEnchantment
            .function("containsEnchantment", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val ench = asEnchantment(ctx, 0)
                item.containsEnchantment(ench)
            })

            // getEnchantmentLevel
            .function("getEnchantmentLevel", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val ench = asEnchantment(ctx, 0)
                item.getEnchantmentLevel(ench)
            })

            // getEnchantments
            .function("getEnchantments", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                item.enchantments
            })

            // addEnchantment
            .function("addEnchantment", 2, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val ench = asEnchantment(ctx, 0)
                val lvl = ctx.getNumber(1).toInt()
                item.addEnchantment(ench, lvl)
                null
            })

            // addUnsafeEnchantment
            .function("addUnsafeEnchantment", 2, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val ench = asEnchantment(ctx, 0)
                val lvl = ctx.getNumber(1).toInt()
                item.addUnsafeEnchantment(ench, lvl)
                null
            })

            // removeEnchantment
            .function("removeEnchantment", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val ench = asEnchantment(ctx, 0)
                item.removeEnchantment(ench)
            })

            // removeEnchantments
            .function("removeEnchantments", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                item.removeEnchantments()
                null
            })

            // addEnchantments
            .function("addEnchantments", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val map = ctx.getArgument(0)
                if (map !is Map<*, *>) throw IllegalArgumentException("addEnchantments: argument must be Map<Enchantment, Int>")
                val casted = map.entries.associate {
                    val key = it.key
                    val value = it.value
                    val ench = if (key is Enchantment) key
                    else if (key is String) {
                        Enchantment.getByKey(NamespacedKey.minecraft(key)) ?: Enchantment.getByName(key.toUpperCase())
                        ?: throw IllegalArgumentException("addEnchantments: Key $key is not a valid Enchantment")
                    } else throw IllegalArgumentException("addEnchantments: Map key must be Enchantment or String")

                    if (value !is Int) throw IllegalArgumentException("addEnchantments: Map value must be Int")
                    ench to value
                }
                item.addEnchantments(casted)
                null
            })

            // addUnsafeEnchantments
            .function("addUnsafeEnchantments", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val map = ctx.getArgument(0)
                if (map !is Map<*, *>) throw IllegalArgumentException("addUnsafeEnchantments: argument must be Map<Enchantment, Int>")
                val casted = map.entries.associate {
                    val key = it.key
                    val value = it.value
                    val ench = key as? Enchantment
                        ?: if (key is String) {
                            Enchantment.getByKey(NamespacedKey.minecraft(key)) ?: Enchantment.getByName(key.toUpperCase())
                            ?: throw IllegalArgumentException("addUnsafeEnchantments: Key $key is not a valid Enchantment")
                        } else throw IllegalArgumentException("addUnsafeEnchantments: Map key must be Enchantment or String")

                    if (value !is Int) throw IllegalArgumentException("addUnsafeEnchantments: Map value must be Int")
                    ench to value
                }
                item.addUnsafeEnchantments(casted)
                null
            })

            // add without argument
            .function("add", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                item.add()
            })

            // add with argument
            .function("add", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val qty = ctx.getNumber(0).toInt()
                item.add(qty)
            })

            // subtract without argument
            .function("subtract", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                item.subtract()
            })

            // subtract with argument
            .function("subtract", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val qty = ctx.getNumber(0).toInt()
                item.subtract(qty)
            })

            // withType(Material)
            .function("withType", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val mat = ctx.getArgument(0) ?: throw IllegalArgumentException("withType: parameter 0 cannot be null")
                if (mat !is Material) throw IllegalArgumentException("withType: parameter 0 must be Material")
                item.withType(mat)
            })

            // getMaxStackSize
            .function("getMaxStackSize", 0, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                item.maxStackSize
            })

            // isSimilar(ItemStack)
            .function("isSimilar", 1, NativeCallable { ctx: FunctionContext<ItemStack?>? ->
                val item = ctx!!.getTarget() ?: throw IllegalStateException("ItemStack target is null")
                val other = ctx.getArgument(0)
                if (other != null && other !is ItemStack) throw IllegalArgumentException("isSimilar: argument must be ItemStack or null")
                item.isSimilar(other as ItemStack?)
            })
    }
}

fun asEnchantment(ctx: FunctionContext<*>, index: Int): Enchantment {
    val arg = ctx.getArgument(index) ?: throw IllegalArgumentException("Argument $index cannot be null")
    return when(arg) {
        is Enchantment -> arg
        is String -> {
            // 尝试用命名空间Key获取
            var ench = Enchantment.getByKey(NamespacedKey.minecraft(arg))
            if (ench == null) {
                // 再尝试根据名字获取（大写）
                ench = Enchantment.getByName(arg.toUpperCase())
            }
            ench ?: throw IllegalArgumentException("Cannot convert argument $index to Enchantment: $arg")
        }
        else -> throw IllegalArgumentException("Argument $index must be Enchantment or String")
    }
}