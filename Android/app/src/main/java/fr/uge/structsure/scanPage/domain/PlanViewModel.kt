package fr.uge.structsure.scanPage.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.R
import fr.uge.structsure.components.Point
import fr.uge.structsure.scanPage.presentation.components.SensorState
import fr.uge.structsure.structuresPage.data.PlanDB
import fr.uge.structsure.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlanViewModel(context: Context) : ViewModel() {

    /** Load once the fallback plan image */
    var defaultImage: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.plan_not_found)

    val filteredPoints = MutableLiveData<List<Point>>(listOf())

    val image = MutableLiveData(defaultImage)

    fun filterPointsForPlan(scanViewModel: ScanViewModel) {
        val selected = scanViewModel.selected.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            println("[D] Filtering data")
            val sensors = scanViewModel.sensorsNotScanned.value ?: return@launch
            val points =  sensors
                .filter { it.plan == selected.plan.id }
                .map { Point(it.x, it.y, SensorState.from(it.state)) }
            // TODO test with SensorDB, may be more performant
            filteredPoints.postValue(points)
            println("[D] Done")
        }
    }

    /**
     * Loads the image for the given plan in background so that it
     * does not affect the main thread. Once the image is loaded, the
     * Bitmap is sent to the [image] variable.
     * @param context needed to read the file from the device
     * @param plan the plan to load
     */
    fun setImage(context: Context, plan: PlanDB) {
        viewModelScope.launch(Dispatchers.IO) {
            val path = plan.let { FileUtils.getLocalPlanImage(context, it.id) }
            image.postValue(if (path == null) defaultImage else BitmapFactory.decodeFile(path))
        }
    }
}
