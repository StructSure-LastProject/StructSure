package fr.uge.structsure.settingsPage.presentation

import android.content.Context
import android.content.SharedPreferences

/**
 * PreferencesManager is a utility object for managing application preferences using SharedPreferences.
 * It provides functionality to save and retrieve the server URL.
 *
 * This class ensures that data is stored persistently and can be accessed across application sessions.
 */
object PreferencesManager {
    /**
     * The name of the SharedPreferences file.
     */
    private const val PREFERENCES_NAME = "app_preferences"

    /**
     * The key used to store the server URL in SharedPreferences.
     */
    private const val SERVER_URL_KEY = "server_url"

    /**
     * Retrieves the SharedPreferences instance.
     *
     * @param context The context used to access SharedPreferences.
     * @return The SharedPreferences instance associated with the application.
     */
    private fun getPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    /**
     * Saves the server URL in SharedPreferences.
     *
     * @param context The context used to access SharedPreferences.
     * @param url The server URL to save.
     */
    fun saveServerUrl(context: Context, url: String) {
        getPreferences(context).edit().putString(SERVER_URL_KEY, url).apply()
    }

    /**
     * Retrieves the server URL from SharedPreferences.
     *
     * @param context The context used to access SharedPreferences.
     * @return The server URL if it exists, or null if no URL is saved.
     */
    fun getServerUrl(context: Context): String? =
        getPreferences(context).getString(SERVER_URL_KEY, null)
}
