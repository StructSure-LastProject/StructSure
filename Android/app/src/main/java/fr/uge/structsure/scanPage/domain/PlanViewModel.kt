package fr.uge.structsure.scanPage.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.R
import fr.uge.structsure.scanPage.data.EditType
import fr.uge.structsure.scanPage.data.ScanEdits
import fr.uge.structsure.scanPage.data.TreeNode
import fr.uge.structsure.scanPage.data.TreePlan
import fr.uge.structsure.scanPage.data.TreeSection
import fr.uge.structsure.structuresPage.data.PlanDB
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlanViewModel(context: Context, private val scanViewModel: ScanViewModel) : ViewModel() {

    /** Load once the fallback plan image */
    var defaultImage: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.plan_not_found)

    /** Currently selected plan in the plans selector */
    val selected = MutableLiveData<TreePlan>(null)

    /** Tree of plans and section for the plans selector */
    val plans = MutableLiveData<TreeSection?>(null)

    /** Filtered list of point for the selected plan */
    val filteredPoints = MutableLiveData<List<SensorDB>>(listOf())

    /** Image of the currently selected plan */
    val image = MutableLiveData(defaultImage)

    /** Image for displaying in the sensor popup  */
    val popupImage = MutableLiveData(defaultImage)

    init {
        val mediator = MediatorLiveData<List<SensorDB>>()
        mediator.addSource(scanViewModel.sensorsNotScanned) { sensors ->
            // Update filtered list when sensors list changes
            updateFilteredPoint(sensors, selected.value)
        }
        mediator.addSource(selected) { selectedValue ->
            // Update filtered list when selected plan changes
            updateFilteredPoint(scanViewModel.sensorsNotScanned.value, selectedValue)
        }
        mediator.observeForever { filtered ->
            filteredPoints.postValue(filtered)
        }
    }

    /**
     * Loads the plan for the given structure.
     * @param context needed to read the file from the device
     * @param structureId the id of the structure to load the plans for
     */
    fun loadPlans(context: Context, structureId: Long) {
        val tree = planTree(context, db.planDao().getPlansByStructureId(structureId))
        plans.postValue(tree)
    }

    /**
     * Clear the loaded list of plan. Must be called when the scan
     * page is unloaded
     */
    fun reset() {
        plans.postValue(null)
        filteredPoints.postValue(listOf())
    }

    /**
     * Mark the given plan as selected and loads the image for the
     * given plan in background so that it does not affect the main
     * thread. Once the image is loaded, the Bitmap is sent to the
     * [image] variable.
     * @param context needed to read the file from the device
     * @param plan the plan to select from [plans]
     */
    fun selectPlan(context: Context, plan: TreePlan?) {
        if (plan == null) {
            image.postValue(defaultImage)
            return
        }
        selected.postValue(plan as TreePlan)
        viewModelScope.launch(Dispatchers.IO) {
            val path = plan.let { FileUtils.getLocalPlanImage(context, it.plan.id) }
            println("ImageUpdate $path")
            image.postValue(if (path == null) defaultImage else BitmapFactory.decodeFile(path))
        }
    }

    /**
     * Creates a tree of plans and sections from a list of raw plans from
     * the database.
     * @param context to load the plan
     * @param plans the raw plans from the database
     * @return the tree containing all item well organized
     */
    private fun planTree(context: Context, plans: List<PlanDB>): TreeSection {
        val root = TreeSection("")
        var defaultPlan: TreePlan? = null
        plans.forEach { plan ->
            val tokens = if (plan.section.isEmpty()) listOf() else plan.section.split("/")
            var node: TreeNode = root
            tokens.forEach { token -> node = node.children.computeIfAbsent(token) { TreeSection(token) } }
            val child = TreePlan(plan)
            node.children["${plan.id}"] = child
            if (defaultPlan == null) defaultPlan = child
        }
        selectPlan(context, defaultPlan)
        return root
    }

    /**
     * Calculates the filtered list of point for the selected plan
     * @param sensors the full list of sensor of the structure
     * @param selected the currently selected plan
     */
    private fun updateFilteredPoint(sensors: List<SensorDB>?, selected: TreePlan?) {
        val filtered = (sensors?:listOf())
            .filter { selected != null && it.plan == selected.plan.id }
        filteredPoints.postValue(filtered)
    }

    /**
     * Load the image of the plan by its ID specifically for the popup without affecting
     * the main plan display. The image is loaded in background.
     * @param context needed to read the file from the device
     * @param planId the id of the plan to load the image for
     */
    fun loadPlanForPopup(context: Context, planId: Long?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (planId == null) {
                popupImage.postValue(defaultImage)
                return@launch
            }

            val path = FileUtils.getLocalPlanImage(context, planId)
            popupImage.postValue(if (path == null) defaultImage else BitmapFactory.decodeFile(path))
        }
    }
    
    /**
     * Adds a position to a sensor and saves it in the database. The
     * sensor must have no position defined yet.
     * @param sensor the sensor to place
     * @param plan the id of the plan to put the sensor on
     * @param x the x coordinate of the point in the image
     * @param y the y coordinate of the point in the image
     */
    fun placeSensor(sensor: SensorDB, plan: Long?, x: Int, y: Int) {
        db.sensorDao().placeSensor(sensor.sensorId, plan, x, y)
        scanViewModel.activeScanId?.let { scanId ->
            viewModelScope.launch(Dispatchers.IO) {
                db.scanEditsDao().upsert(ScanEdits(scanId, EditType.SENSOR_PLACE, sensor.sensorId))
            }
        }
        scanViewModel.refreshSensorStates()
    }
}
