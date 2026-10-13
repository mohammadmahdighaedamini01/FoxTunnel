package com.Nightmare.FoxTunnel.ui.screens

internal fun flagEmoji(countryCode: String): String {
    val code = countryCode.trim().uppercase()
    if (code.length != 2 || code.any { it !in 'A'..'Z' }) return ""
    val base = 0x1F1E6 - 'A'.code
    return String(Character.toChars(base + code[0].code)) + String(Character.toChars(base + code[1].code))
}
