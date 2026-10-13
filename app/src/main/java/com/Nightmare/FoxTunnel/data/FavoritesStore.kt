package com.Nightmare.FoxTunnel.data

import android.content.Context

class FavoritesStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("foxtunnel_favorites", Context.MODE_PRIVATE)

    fun load(): Set<String> = prefs.getStringSet(KEY_FAVORITES, emptySet())?.toSet() ?: emptySet()

    fun save(keys: Set<String>) {
        prefs.edit().putStringSet(KEY_FAVORITES, keys.toSet()).apply()
    }

    companion object {
        private const val KEY_FAVORITES = "favorites"

        fun key(countryCode: String, cityCode: String): String = "$countryCode:$cityCode"
    }
}
