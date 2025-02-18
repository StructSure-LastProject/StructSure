package fr.uge.structsure.scanPage.domain

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.structuresPage.data.StructureRepository
import fr.uge.structsure.utils.FileUtils
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the plans and their images.
 * It handles both local storage and network fetching of plan images,
 * providing a seamless experience whether online or offline.
 */
class PlanViewModel : ViewModel() {
    private val repository = StructureRepository()

    /**
     * LiveData containing the path to the plan image file.
     * When updated, the UI will automatically reflect the changes.
     */
    val planImagePath = MutableLiveData<String?>()

    /**
     * Loads the plan for a given structure. First checks if the image exists
     * locally, and if not, attempts to download it from the server.
     *
     * @param context The application context needed for file operations
     * @param structureId The ID of the structure whose plan should be loaded
     */
    fun loadPlans(context: Context, structureId: Long) {
        viewModelScope.launch {
            try {
                db.planDao().getPlanByStructureId(structureId).forEach { planId ->
                    val localPath = FileUtils.getLocalPlanImage(context, planId)
                    if (localPath != null) {
                        planImagePath.value = localPath
                    } else {
                        fetchPlanImage(context, planId)
                    }
                }
            } catch (e: Exception) {
                Log.e("PlanViewModel", "Error loading plan", e)
            }
        }
    }

    /**
     * Attempts to download a plan image from the server and save it locally.
     * If the download fails but the image exists locally, uses the local version.
     *
     * @param context The application context needed for file operations
     * @param planId The ID of the plan whose image should be fetched
     */
    private fun fetchPlanImage(context: Context, planId: Long) {
        viewModelScope.launch {
            try {
                planImagePath.value = repository.downloadPlanImage(context, planId)
            } catch (e: Exception) {
                Log.e("PlanViewModel", "Error fetching plan image", e)
                val localPath = FileUtils.getLocalPlanImage(context, planId)
                if (localPath != null) {
                    planImagePath.value = localPath
                }
            }
        }
    }
}