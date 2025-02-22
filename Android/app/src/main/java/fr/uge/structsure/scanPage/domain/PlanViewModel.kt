package fr.uge.structsure.scanPage.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.R
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
            filterPointsForPlan(scanViewModel)
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

    private fun filterPointsForPlan(scanViewModel: ScanViewModel) {
        val selected = selected.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val sensors = scanViewModel.sensorsNotScanned.value ?: return@launch
            val points =  sensors
                .filter { it.plan == null }
                // .filter { it.plan == selected.plan.id }
            filteredPoints.postValue(points)
        }
    }
}
