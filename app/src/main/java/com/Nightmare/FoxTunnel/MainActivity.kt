package com.Nightmare.FoxTunnel

import android.Manifest
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.Nightmare.FoxTunnel.ui.navigation.FoxTunnelNavGraph
import com.Nightmare.FoxTunnel.ui.theme.FoxTunnelTheme
import com.Nightmare.FoxTunnel.ui.theme.rememberThemeController
import com.Nightmare.FoxTunnel.vpn.FoxTunnelService

class MainActivity : ComponentActivity() {

    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            FoxTunnelService.start(this)
        } else {

            Toast.makeText(
                this,
                "FoxTunnel needs Android's VPN permission to tunnel this device's traffic. " +
                    "Proxy-only mode in Settings works without it.",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {  }

    private fun requestConnect() {
        requestNotificationPermissionIfNeeded()

        if ((application as FoxTunnelApp).settingsStore.proxyOnlyMode) {
            FoxTunnelService.start(this)
            return
        }

        val prepareIntent = VpnService.prepare(this)
        if (prepareIntent != null) {
            vpnPermissionLauncher.launch(prepareIntent)
        } else {
            FoxTunnelService.start(this)
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val alreadyGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!alreadyGranted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as FoxTunnelApp
        setContent {

            val themeController = rememberThemeController(app.settingsStore)
            FoxTunnelTheme(themeMode = themeController.mode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    FoxTunnelNavGraph(
                        navController = navController,
                        app = app,
                        themeController = themeController,
                        onRequestConnect = { requestConnect() },
                        onDisconnect = { FoxTunnelService.stop(this) },
                    )
                }
            }
        }
    }
}
