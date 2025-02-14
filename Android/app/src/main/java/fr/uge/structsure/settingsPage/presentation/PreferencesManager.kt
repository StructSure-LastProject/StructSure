package fr.uge.structsure.settingsPage.presentation

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {
    private const val PREFERENCES_NAME = "app_preferences"
    private const val SERVER_URL_KEY = "server_url"

    private fun getPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun saveServerUrl(context: Context, url: String) {
        getPreferences(context).edit().putString(SERVER_URL_KEY, url).apply()
    }

    fun getServerUrl(context: Context): String? =
        getPreferences(context).getString(SERVER_URL_KEY, null)
}
