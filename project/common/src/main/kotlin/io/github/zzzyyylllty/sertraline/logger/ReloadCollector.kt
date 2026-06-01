package io.github.zzzyyylllty.sertraline.logger

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import org.bukkit.command.CommandSender
import taboolib.module.lang.asLangText

object ReloadCollector {

    private val errors = mutableListOf<String>()
    private val warnings = mutableListOf<String>()
    private val stats = mutableListOf<String>()

    var isActive: Boolean = false
        private set

    fun begin() {
        errors.clear()
        warnings.clear()
        stats.clear()
        isActive = true
    }

    fun addError(message: String) {
        if (isActive) errors.add(message)
    }

    fun addWarning(message: String) {
        if (isActive) warnings.add(message)
    }

    fun addStat(message: String) {
        if (isActive) stats.add(message)
    }

    fun hasErrors() = errors.isNotEmpty()
    fun hasWarnings() = warnings.isNotEmpty()

    private fun printTo(target: CommandSender) {
        target.sendStringAsComponent(console.asLangText("Reload_Summary_Header"))

        if (hasErrors()) {
            target.sendStringAsComponent(console.asLangText("Reload_Summary_Section_Errors"))
            errors.forEachIndexed { index, msg ->
                target.sendStringAsComponent(
                    console.asLangText("Reload_Summary_Error_Item", index + 1, msg)
                )
            }
        }

        if (hasWarnings()) {
            target.sendStringAsComponent(console.asLangText("Reload_Summary_Section_Warnings"))
            warnings.forEachIndexed { index, msg ->
                target.sendStringAsComponent(
                    console.asLangText("Reload_Summary_Warn_Item", index + 1, msg)
                )
            }
        }

        target.sendStringAsComponent(console.asLangText("Reload_Summary_Separator"))

        for (stat in stats) {
            target.sendStringAsComponent(stat)
        }

        target.sendStringAsComponent(console.asLangText("Reload_Summary_Footer"))
    }

    fun printSummary(sender: CommandSender? = null) {
        isActive = false

        if (sender != null && sender != consoleSender) {
            printTo(sender)
        }
        printTo(consoleSender)
    }
}
