package io.github.zzzyyylllty.sertraline.function.load

import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.Sertraline.templateMap
import io.github.zzzyyylllty.sertraline.logger.severeS
import org.bukkit.configuration.ConfigurationSection
import taboolib.platform.util.asLangText
import java.lang.module.Configuration
import java.util.LinkedHashMap
import javax.security.auth.login.ConfigurationSpi

fun applyTemplate(iconfig: ConfigurationSection, templateText: LinkedHashMap<String, Any>) : ConfigurationSection {
    val templateName = templateText["template"] as String
    val template = templateMap[templateName] ?: run {
        severeS(consoleSender.asLangText("TEMPLATE_NOT_FOUND",templateName))
        throw NullPointerException("Template $templateName not found")
    }
    val config = iconfig
    for (section in template.getValues(false)) {
        config[section.key] = section.value
    }
    for (templateData in templateText["data"] as LinkedHashMap<String, Any>) {
        config["data.${templateData.key}"] = templateData.value
        severeS("Replaced Template Data ${templateData.key} to ${templateData.value}")
    }
    return config
}