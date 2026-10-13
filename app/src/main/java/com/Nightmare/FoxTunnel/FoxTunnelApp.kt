package com.Nightmare.FoxTunnel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.Nightmare.FoxTunnel.data.CrashReporter
import com.Nightmare.FoxTunnel.data.FxaAuthRepository
import com.Nightmare.FoxTunnel.data.GuardianClient
import com.Nightmare.FoxTunnel.data.ProxyStateStore
import com.Nightmare.FoxTunnel.data.ServerListClient
import com.Nightmare.FoxTunnel.data.SettingsStore
import com.Nightmare.FoxTunnel.data.TokenStore
import com.Nightmare.FoxTunnel.vpn.upstream.NettyLoggingBridge
import org.conscrypt.Conscrypt
import java.security.Security

class FoxTunnelApp : Application() {

    lateinit var tokenStore: TokenStore
    lateinit var proxyStateStore: ProxyStateStore
    lateinit var settingsStore: SettingsStore
    lateinit var guardianClient: GuardianClient
    lateinit var serverListClient: ServerListClient
    lateinit var authRepository: FxaAuthRepository

    override fun onCreate() {
        super.onCreate()

        CrashReporter.install(this)
        CrashReporter.replayLastCrashIfAny(this)

        NettyLoggingBridge.install()
        installConscrypt()
        tokenStore = TokenStore(this)
        proxyStateStore = ProxyStateStore(this)
        settingsStore = SettingsStore(this)
        guardianClient = GuardianClient()
        serverListClient = ServerListClient()
        authRepository = FxaAuthRepository(tokenStore)
        createNotificationChannel()
    }

    private fun installConscrypt() {
        runCatching {
            Security.insertProviderAt(Conscrypt.newProvider(), 1)
        }.onFailure {
            Log.w("FoxTunnelApp", "failed to install Conscrypt security provider", it)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            VPN_NOTIFICATION_CHANNEL_ID,
            "VPN status",
            NotificationManager.IMPORTANCE_LOW,
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val VPN_NOTIFICATION_CHANNEL_ID = "foxtunnel_status"
    }
}
