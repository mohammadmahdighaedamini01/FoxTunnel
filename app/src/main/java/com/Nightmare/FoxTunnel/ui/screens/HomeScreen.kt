package com.Nightmare.FoxTunnel.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.Nightmare.FoxTunnel.data.RECOMMENDED_COUNTRY_CODE
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import com.Nightmare.FoxTunnel.FoxTunnelApp
import com.Nightmare.FoxTunnel.data.model.ConnectionState
import com.Nightmare.FoxTunnel.data.model.ProxyCandidate
import com.Nightmare.FoxTunnel.ui.theme.LocalFoxTunnelStatusColors
import androidx.compose.ui.res.painterResource
import com.Nightmare.FoxTunnel.R
import com.Nightmare.FoxTunnel.ui.theme.ThemeController
import com.Nightmare.FoxTunnel.ui.theme.ThemeMode
import com.Nightmare.FoxTunnel.vpn.FoxTunnelService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    app: FoxTunnelApp,
    themeController: ThemeController,
    onRequestConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onOpenServers: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val state by FoxTunnelService.state.collectAsState()
    val lastError by FoxTunnelService.lastError.collectAsState()

    val selectedProxy by app.proxyStateStore.selectedProxyFlow.collectAsState()

    val statusColors = LocalFoxTunnelStatusColors.current
    val targetRingColor = when (state) {
        ConnectionState.CONNECTED -> statusColors.connected
        ConnectionState.CONNECTING -> statusColors.connecting
        ConnectionState.DISCONNECTED -> MaterialTheme.colorScheme.outline
    }

    val ringColor by animateColorAsState(
        targetValue = targetRingColor,
        animationSpec = tween(durationMillis = 800),
        label = "connect-ring-color",
    )
    val poweredOn = state != ConnectionState.DISCONNECTED
    val powerProgress by animateFloatAsState(
        targetValue = if (poweredOn) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "power-icon-swap",
    )
    val scale by animateFloatAsState(
        targetValue = if (state == ConnectionState.CONNECTED) 1f else 0.92f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "connect-scale",
    )

    val systemInDarkTheme = isSystemInDarkTheme()
    val haptics = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FoxTunnel") },
                actions = {

                    val mode = themeController.mode
                    val showingDark = themeController.resolveDark(systemInDarkTheme)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .combinedClickable(
                                role = Role.Button,
                                onClick = { themeController.toggle(systemInDarkTheme) },
                                onLongClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    themeController.set(ThemeMode.SYSTEM)
                                },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(if (showingDark) R.drawable.ic_theme_moon else R.drawable.ic_theme_sun),

                            contentDescription = when (mode) {
                                ThemeMode.SYSTEM ->
                                    "Theme: follow system. Tap to switch to ${if (showingDark) "light" else "dark"} mode"
                                ThemeMode.LIGHT -> "Theme: light. Tap for dark mode, long press to follow the system"
                                ThemeMode.DARK -> "Theme: dark. Tap for light mode, long press to follow the system"
                            },
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(ringColor.copy(alpha = 0.15f))
                    .clickable {

                        if (state == ConnectionState.DISCONNECTED) onRequestConnect() else onDisconnect()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size((140 * scale).dp)
                        .clip(CircleShape)
                        .background(ringColor.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.ic_power_on),
                            contentDescription = null,
                            tint = ringColor,
                            modifier = Modifier
                                .size(64.dp)
                                .graphicsLayer {
                                    rotationZ = powerProgress * 180f
                                    alpha = 1f - powerProgress
                                },
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_power_off),
                            contentDescription = if (state == ConnectionState.DISCONNECTED) "Connect" else "Disconnect",
                            tint = ringColor,
                            modifier = Modifier
                                .size(64.dp)
                                .graphicsLayer {
                                    rotationZ = (powerProgress - 1f) * 180f
                                    alpha = powerProgress
                                },
                        )
                    }
                }
            }

            Spacer(Modifier.padding(top = 24.dp))
            Text(
                text = when (state) {
                    ConnectionState.CONNECTED -> "Connected"
                    ConnectionState.CONNECTING -> "Connecting\u2026"
                    ConnectionState.DISCONNECTED -> "Disconnected"
                },
                style = MaterialTheme.typography.headlineSmall,
            )

            if (state == ConnectionState.CONNECTING) {
                Spacer(Modifier.padding(top = 4.dp))
                Text(
                    "Tap to cancel",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            val error = lastError
            if (error != null && state != ConnectionState.CONNECTING) {
                Spacer(Modifier.padding(top = 8.dp))
                Text(
                    error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(Modifier.padding(top = 24.dp))
            Card(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenServers),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Location",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.padding(top = 4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val proxy = selectedProxy
                        val flag = proxy
                            ?.takeIf { it.countryCode != RECOMMENDED_COUNTRY_CODE }
                            ?.let { flagEmoji(it.countryCode) }
                            .orEmpty()
                        if (flag.isNotEmpty()) {
                            Text(flag, fontSize = 30.sp)
                        } else {
                            Icon(imageVector = Icons.Filled.Public, contentDescription = null)
                        }
                        Spacer(Modifier.padding(start = 12.dp))
                        Column {
                            Text(
                                proxy?.let { it.countryName.ifBlank { it.countryCode } } ?: "Recommended",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                            val cityName = proxy?.cityCode.orEmpty()
                            if (cityName.isNotBlank()) {
                                Text(
                                    cityName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.padding(top = 12.dp))
            Text(
                text = "Settings",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.clickable(onClick = onOpenSettings).padding(8.dp),
            )
        }
    }
}
