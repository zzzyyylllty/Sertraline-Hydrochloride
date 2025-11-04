package io.github.zzzyyylllty.sertraline.util.minimessage

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

val mmUtil = MiniMessage.miniMessage()
val mmLegacyUtil = LegacyComponentSerializer.legacyAmpersand()
val mmJsonUtil = GsonComponentSerializer.gson()