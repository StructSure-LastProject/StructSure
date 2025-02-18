package fr.uge.structsure.scanPage.domain

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.structuresPage.data.PlanDB
import fr.uge.structsure.structuresPage.data.StructureRepository
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the plan image fetching process.
 * It interacts with the repository to fetch the plan image.
 * The plan image is then displayed in the UI.
 *
 */
class PlanViewModel : ViewModel() {
    private val repository = StructureRepository()
    val planImage = MutableLiveData<Bitmap?>()

    /**
     * Fetches the plans for a given structure and the image of the first plan.
     *
     * @param structureId the id of the structure
     */
    fun loadPlans(structureId: Long) {
        viewModelScope.launch {
            try {
                val planId = db.planDao().getPlanByStructureId(structureId)
                if (planId != null) {
                    fetchPlanImage(planId)
                } else {
                    Log.e("PlanViewModel", "No plan found for structure $structureId")
                }
            } catch (e: Exception) {
                Log.e("PlanViewModel", "Error loading plan", e)
            }
        }
    }

    /**
     * Fetches the image of a plan.
     *
     * @param structureId the id of the structure
     * @param planId the id of the plan
     */
    private fun fetchPlanImage(planId: Long) {
        viewModelScope.launch {
            try {
                planImage.value = repository.downloadPlanImage(planId)
            } catch (e: Exception) {
                Log.e("PlanViewModel", "Error fetching plan image", e)
            }
        }
    }
}