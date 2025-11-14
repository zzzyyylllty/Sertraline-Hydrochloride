package io.github.zzzyyylllty.sertraline.util.minimessage

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

val mmUtil = MiniMessage.miniMessage()
val mmStrictUtil = MiniMessage.builder().strict(true).build()
val mmLegacyAmpersandUtil = LegacyComponentSerializer.legacyAmpersand()
val mmLegacySectionUtil = LegacyComponentSerializer.legacySection()
val mmJsonUtil = GsonComponentSerializer.gson()