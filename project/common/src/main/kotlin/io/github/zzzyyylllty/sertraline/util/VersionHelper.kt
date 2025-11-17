package io.github.zzzyyylllty.sertraline.util

import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.MinecraftVersion.versionId

class VersionHelper {
    fun getVer(): Int{
        return versionId
    }
    val isUniversal: Boolean by lazy {
        MinecraftVersion.isUniversal
    }
    val isSertralinePremium by lazy {
        try {
            val classInstance = Class.forName("io.github.zzzyyylllty.sertraline.PremiumHelper")
            return@lazy (classInstance != null)
        } catch (e: ClassNotFoundException) {
            return@lazy false
        } catch (e: Exception) {
            throw RuntimeException("An error occurred while enabling PremiumHelper.", e)
        }
    }
    fun isOrAbove12005(): Boolean{
        return versionId >= 12005
    }
    fun isLegacy(): Boolean{
        return versionId < 12005
    }
    fun isOrAbove12100(): Boolean{
        return versionId >= 12100
    }
    fun isOrAbove12101(): Boolean{
        return versionId >= 12101
    }
    fun isOrAbove12102(): Boolean{
        return versionId >= 12102
    }
    fun isOrAbove12103(): Boolean{
        return versionId >= 12103
    }
    fun isOrAbove12104(): Boolean{
        return versionId >= 12104
    }
    fun isOrAbove12105(): Boolean{
        return versionId >= 12105
    }
}