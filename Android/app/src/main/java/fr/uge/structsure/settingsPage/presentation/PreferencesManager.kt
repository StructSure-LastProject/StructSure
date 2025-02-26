package fr.uge.structsure.settingsPage.presentation

import android.content.Context
import android.content.SharedPreferences
import fr.uge.structsure.database.AppDatabase
import fr.uge.structsure.structuresPage.data.StructureRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * PreferencesManager is a utility object for managing application preferences using SharedPreferences.
 * It provides functionality to save and retrieve the server URL.
 *
 * This class ensures that data is stored persistently and can be accessed across application sessions.
 */
object PreferencesManager {
    /** The name of the SharedPreferences file */
    private const val PREFERENCES_NAME = "app_preferences"

    /** The key used to store the server URL in SharedPreferences */
    private const val SERVER_URL_KEY = "server_url"

    /** The key used to store the scanner sensitivity in SharedPreferences */
    private const val SENSITIVITY_KEY = "sensitivity"

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

    /**
     * Saves the given sensitivity range in SharedPreferences.
     * @param context The context used to access SharedPreferences.
     * @param min The nearest attenuation allowed
     * @param max The farthest attenuation allowed
     */
    fun saveScannerSensitivity(context: Context, min: Int, max: Int) {
        getPreferences(context).edit().putString(SENSITIVITY_KEY, "$min:$max").apply()
    }

    /**
     * Retrieves the sensitivity range in SharedPreferences.
     * @param context The context used to access SharedPreferences.
     */
    fun getScannerSensitivity(context: Context): List<Int> =
        getPreferences(context).getString(SENSITIVITY_KEY, "0:100")
            ?.split(":")
            ?.map { -it.toInt() }
            ?: listOf(0, -100)

    /**
     * Clears the server URL from SharedPreferences.
     * This method is used when the user logs out of the application.
     * The server URL is removed to ensure that the user must log in again to access the application.
     * This method is also used when the user changes the server URL.
     *
     * @param context The context used to access SharedPreferences.
     */
    fun deleteUserData(context: Context) {
        val db = AppDatabase.getDatabase(context)
        val structureRepository = StructureRepository()
        CoroutineScope(Dispatchers.IO).launch {
            val structures = db.structureDao().getAllStructures()
            structures.forEach { structure ->
                structureRepository.deleteStructure(structure.id, context)
            }
        }
    }
}
