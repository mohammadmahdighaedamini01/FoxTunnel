package com.Nightmare.FoxTunnel.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.Nightmare.FoxTunnel.FoxTunnelApp
import com.Nightmare.FoxTunnel.ui.screens.AccountScreen
import com.Nightmare.FoxTunnel.ui.screens.HomeScreen
import com.Nightmare.FoxTunnel.ui.screens.LoginScreen
import com.Nightmare.FoxTunnel.ui.screens.LogsScreen
import com.Nightmare.FoxTunnel.ui.screens.ServerListScreen
import com.Nightmare.FoxTunnel.ui.screens.SettingsScreen
import com.Nightmare.FoxTunnel.ui.screens.SplashScreen
import com.Nightmare.FoxTunnel.ui.theme.ThemeController

object FoxTunnelRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val HOME = "home"
    const val SERVERS = "servers"
    const val SETTINGS = "settings"
    const val ACCOUNT = "account"
    const val LOGS = "logs"
}

@Composable
fun FoxTunnelNavGraph(
    navController: NavHostController = rememberNavController(),
    app: FoxTunnelApp,
    themeController: ThemeController,
    onRequestConnect: () -> Unit,
    onDisconnect: () -> Unit,
) {
    NavHost(navController = navController, startDestination = FoxTunnelRoutes.SPLASH) {
        composable(FoxTunnelRoutes.SPLASH) {
            SplashScreen(
                authRepository = app.authRepository,
                onSignedIn = {
                    navController.navigate(FoxTunnelRoutes.HOME) { popUpTo(FoxTunnelRoutes.SPLASH) { inclusive = true } }
                },
                onNeedsLogin = {
                    navController.navigate(FoxTunnelRoutes.LOGIN) { popUpTo(FoxTunnelRoutes.SPLASH) { inclusive = true } }
                },
            )
        }
        composable(FoxTunnelRoutes.LOGIN) {
            LoginScreen(
                authRepository = app.authRepository,
                onSignedIn = {
                    navController.navigate(FoxTunnelRoutes.HOME) { popUpTo(FoxTunnelRoutes.LOGIN) { inclusive = true } }
                },
            )
        }
        composable(FoxTunnelRoutes.HOME) {
            HomeScreen(
                app = app,
                themeController = themeController,
                onRequestConnect = onRequestConnect,
                onDisconnect = onDisconnect,
                onOpenServers = { navController.navigate(FoxTunnelRoutes.SERVERS) },
                onOpenSettings = { navController.navigate(FoxTunnelRoutes.SETTINGS) },
            )
        }
        composable(FoxTunnelRoutes.SERVERS) {
            ServerListScreen(
                serverListClient = app.serverListClient,
                proxyStateStore = app.proxyStateStore,
                onServerSelected = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
            )
        }
        composable(FoxTunnelRoutes.SETTINGS) {
            SettingsScreen(
                settingsStore = app.settingsStore,
                onOpenLogs = { navController.navigate(FoxTunnelRoutes.LOGS) },
                onOpenAccount = { navController.navigate(FoxTunnelRoutes.ACCOUNT) },
                onSignOut = {
                    onDisconnect()
                    app.tokenStore.clear()
                    navController.navigate(FoxTunnelRoutes.LOGIN) { popUpTo(0) { inclusive = true } }
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(FoxTunnelRoutes.ACCOUNT) {
            AccountScreen(
                authRepository = app.authRepository,
                onBack = { navController.popBackStack() },
            )
        }
        composable(FoxTunnelRoutes.LOGS) {
            LogsScreen(onBack = { navController.popBackStack() })
        }
    }
}
