package fr.uge.structsure.utils

import android.content.Context
import java.io.File
import java.io.InputStream

/**
 * Utility object for handling file operations related to plan images.
 * Provides methods for saving and retrieving plan images from the device's internal storage.
 */
object FileUtils {

    /**
     * Retrieves the local path of a plan image if it exists.
     *
     * @param context The application context needed for accessing internal storage
     * @param planId The ID of the plan whose image should be retrieved
     * @return The absolute path to the image file if it exists, null otherwise
     */
    fun getLocalPlanImage(context: Context, planId: Long): String? {
        val file = File(context.filesDir, "plans/${planId}.jpg")
        return if (file.exists()) file.absolutePath else null
    }

    /**
     * Saves a plan image to the device's internal storage.
     * Creates the necessary directories if they don't exist.
     *
     * @param context The application context needed for accessing internal storage
     * @param planId The ID of the plan being saved
     * @param inputStream The input stream containing the image data
     * @return The absolute path to the saved image file
     */
    fun savePlanImageToInternalStorage(context: Context, planId: Long, inputStream: InputStream): String {
        val directory = File(context.filesDir, "plans")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "${planId}.jpg")
        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file.absolutePath
    }

    /**
     * Deletes plan image files associated with a plan from internal storage.
     *
     * @param context The application context needed for file operations
     * @param planId The ID of the plan whose image should be deleted
     * @return true if deletion was successful, false otherwise
     */
    fun deletePlanImage(context: Context, planId: Long): Boolean {
        val file = File(context.filesDir, "plans/${planId}.jpg")
        return if (file.exists()) {
            file.delete()
        } else {
            true
        }
    }
}