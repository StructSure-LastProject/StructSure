package fr.uge.structsure.scanPage.domain

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun fetchPlanImage(planId: Long) {
        viewModelScope.launch {
            try {
            val bitmap = repository.downloadPlanImage(planId)
            planImage.postValue(bitmap)

            } catch (e: Exception) {
                Log.e("PlanViewModel", "Failed to fetch plan image", e)
                planImage.postValue(null)
            }
        }
    }
}