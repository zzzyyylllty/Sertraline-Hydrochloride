package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.mappings
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.severeS
import taboolib.module.lang.asLangText

public class ConfigUtil {
    fun getString(input: Any?): String? {
        return if (input == null) null else input.toString()
    }
    fun getInt(input: Any?): Int? {
        return if (input == null) null else input.toString().toInt()
    }

    fun getLong(input: Any?): Long? {
        return if (input == null) null else input.toString().toLong()
    }
    fun getDeep(input: Map<*,*>?, location: String): Any? {
        val list = location.split(".").toMutableList()
        devLog("ConfigUtil getDeep List: $list")
        val current = input?.get(list[0])
        devLog("ConfigUtil getDeep Current: $current")
        return if (current is Map<*, *> && list.size > 1) {
            list.removeFirst()
            getDeep(current, list.joinToString("."))
        } else {
            current
        }
    }
    fun getFeature(input: Map<*,*>?, feature: String): Any? {
        if (input == null) {
            devLog("Get feature of input is null, skipping feature loading")
            return null
        }
        val mapping = mappings[feature]
        if (mapping == null) {
            severeS(console.asLangText("Error_No_Mapping", feature))
            throw IllegalArgumentException("Error: No mappings for $feature found. Are you putting an new version item config to old version sertraline? Try delete plugins/Sertraline/internal/mapping/mappings.yml to solve.")
        }
        for (i in mapping) {
            val m = i.split(":").last()
            if (input.contains(m)) {
                devLog("Mapping $m for $feature found.")
                return input.get(m)
            }
        }
        return null
    }
    fun getFeatures(input: Map<*,*>?, features: List<String>, final: Map<String, Any?> = linkedMapOf<String, Any?>()): Map<String, Any?> {
        val goal = final.toMutableMap()
        for (f in features) {
            goal[f] = getFeature(input, f)
        }
        return goal
    }

}
