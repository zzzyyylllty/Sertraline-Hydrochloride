package io.github.zzzyyylllty.sertraline.util

import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.MinecraftVersion.versionId

class VersionHelper {
    fun getVer(): Int{
        return versionId
    }
    fun isOrAbove12005(): Boolean{
        return MinecraftVersion.isHigherOrEqual(12005)
    }
    fun isLegacy(): Boolean{
        return MinecraftVersion.isLowerOrEqual(12004)
    }
    fun isOrAbove12100(): Boolean{
        return MinecraftVersion.isHigherOrEqual(12100)
    }
    fun isOrAbove12101(): Boolean{
        return MinecraftVersion.isHigherOrEqual(12101)
    }
    fun isOrAbove12102(): Boolean{
        return MinecraftVersion.isHigherOrEqual(12102)
    }
    fun isOrAbove12103(): Boolean{
        return MinecraftVersion.isHigherOrEqual(12103)
    }
    fun isOrAbove12104(): Boolean{
        return MinecraftVersion.isHigherOrEqual(12104)
    }
}