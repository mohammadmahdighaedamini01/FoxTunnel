package com.Nightmare.FoxTunnel.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val FoxSeed = Color(0xFFF97810)

val md_theme_light_primary = Color(0xFFF97810)
val md_theme_light_onPrimary = Color(0xFF060606)
val md_theme_light_primaryContainer = Color(0xFFFFDDC2)
val md_theme_light_onPrimaryContainer = Color(0xFF2B1400)
val md_theme_light_inversePrimary = Color(0xFFFFB783)

val md_theme_light_secondary = Color(0xFFAB327F)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFF6D5E8)
val md_theme_light_onSecondaryContainer = Color(0xFF3A0A2A)

val md_theme_light_tertiary = Color(0xFF2B8267)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFC4EBDC)
val md_theme_light_onTertiaryContainer = Color(0xFF00201A)

val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)

val md_theme_light_background = Color(0xFFFEFEFE)
val md_theme_light_onBackground = Color(0xFF060606)
val md_theme_light_surface = Color(0xFFFEFEFE)
val md_theme_light_onSurface = Color(0xFF060606)
val md_theme_light_surfaceVariant = Color(0xFFE7E7E7)
val md_theme_light_onSurfaceVariant = Color(0xFF4A4A4A)
val md_theme_light_surfaceTint = md_theme_light_primary

val md_theme_light_surfaceContainerLowest = Color(0xFFFFFFFF)
val md_theme_light_surfaceContainerLow = Color(0xFFF7F7F7)
val md_theme_light_surfaceContainer = Color(0xFFEEEEEE)
val md_theme_light_surfaceContainerHigh = Color(0xFFE0E0E0)
val md_theme_light_surfaceContainerHighest = Color(0xFFD6D6D6)
val md_theme_light_surfaceBright = Color(0xFFFEFEFE)
val md_theme_light_surfaceDim = Color(0xFFD9D9D9)

val md_theme_light_outline = Color(0xFF7A7A7A)
val md_theme_light_outlineVariant = Color(0xFFCFCFCF)
val md_theme_light_inverseSurface = Color(0xFF2B2B2B)
val md_theme_light_inverseOnSurface = Color(0xFFF2F2F2)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFFF97810)
val md_theme_dark_onPrimary = Color(0xFF060606)
val md_theme_dark_primaryContainer = Color(0xFF7A3500)
val md_theme_dark_onPrimaryContainer = Color(0xFFFFDDC2)
val md_theme_dark_inversePrimary = Color(0xFFC85A00)

val md_theme_dark_secondary = Color(0xFFD45AA5)
val md_theme_dark_onSecondary = Color(0xFF3A0A2A)
val md_theme_dark_secondaryContainer = Color(0xFF7A1F59)
val md_theme_dark_onSecondaryContainer = Color(0xFFF6D5E8)

val md_theme_dark_tertiary = Color(0xFF4CC9A0)
val md_theme_dark_onTertiary = Color(0xFF00201A)
val md_theme_dark_tertiaryContainer = Color(0xFF0F5240)
val md_theme_dark_onTertiaryContainer = Color(0xFFC4EBDC)

val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)

val md_theme_dark_background = Color(0xFF101010)
val md_theme_dark_onBackground = Color(0xFFF2F2F2)
val md_theme_dark_surface = Color(0xFF101010)
val md_theme_dark_onSurface = Color(0xFFF2F2F2)
val md_theme_dark_surfaceVariant = Color(0xFF2A2A2A)
val md_theme_dark_onSurfaceVariant = Color(0xFFC8C8C8)
val md_theme_dark_surfaceTint = md_theme_dark_primary

val md_theme_dark_surfaceContainerLowest = Color(0xFF0A0A0A)
val md_theme_dark_surfaceContainerLow = Color(0xFF161616)
val md_theme_dark_surfaceContainer = Color(0xFF1C1C1C)
val md_theme_dark_surfaceContainerHigh = Color(0xFF262626)
val md_theme_dark_surfaceContainerHighest = Color(0xFF303030)
val md_theme_dark_surfaceBright = Color(0xFF383838)
val md_theme_dark_surfaceDim = Color(0xFF101010)

val md_theme_dark_outline = Color(0xFF8A8A8A)
val md_theme_dark_outlineVariant = Color(0xFF3D3D3D)
val md_theme_dark_inverseSurface = Color(0xFFF2F2F2)
val md_theme_dark_inverseOnSurface = Color(0xFF2B2B2B)
val md_theme_dark_scrim = Color(0xFF000000)

data class FoxTunnelStatusColors(
    val connected: Color,
    val connecting: Color,
    val disconnected: Color,
)

val LightStatusColors = FoxTunnelStatusColors(
    connected = Color(0xFF2B8267),
    connecting = Color(0xFFB26B00),
    disconnected = md_theme_light_onSurfaceVariant,
)

val DarkStatusColors = FoxTunnelStatusColors(
    connected = Color(0xFF4CC9A0),
    connecting = Color(0xFFFFC24D),
    disconnected = md_theme_dark_onSurfaceVariant,
)

val LocalFoxTunnelStatusColors = staticCompositionLocalOf { LightStatusColors }

val ConnectedGreen = LightStatusColors.connected
val ConnectingAmber = LightStatusColors.connecting
