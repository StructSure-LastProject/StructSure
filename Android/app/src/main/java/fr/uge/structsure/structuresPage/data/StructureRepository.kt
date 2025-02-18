package fr.uge.structsure.structuresPage.data

import android.content.Context
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.ViewModel
import fr.uge.structsure.MainActivity
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.utils.FileUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Optional

class StructureRepository : ViewModel() {
    
    companion object {
        private const val TAG = "StructureRepository"
    }

    private val structureDao = MainActivity.db.structureDao()
    private val planDao = MainActivity.db.planDao()
    private val sensorDao = MainActivity.db.sensorDao()
    private val resultDao = MainActivity.db.resultDao()

    private fun getApiInterface() = RetrofitInstance.structureApi

    private suspend fun getFromApi(): List<StructureData> {
        return withContext(Dispatchers.IO) {
            val apiInterface = getApiInterface()
            val call = apiInterface.getAllStructures()

            try {
                val response = call.execute()  // This is a synchronous call
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!.map {
                        StructureData(
                            it.id,
                            it.name,
                            it.numberOfPlans,
                            it.numberOfSensors,
                            it.state,
                            it.archived,
                            downloaded = false
                        )
                    }
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.d("ERROR API", e.message.toString())
                emptyList()
            }
        }
    }

    suspend fun getAllStructures(): List<StructureData> {
        val structuresFromDB = structureDao.getAllStructures()
        val structuresFromApi = getFromApi()
        return (structuresFromDB + structuresFromApi).distinctBy { it.id }
    }


    private suspend fun getStructureDetailsFromApi(id: Long): Optional<StructureDetailsData> {
        return withContext(Dispatchers.IO) {
            val apiInterface = getApiInterface()
            val call = apiInterface.getStructureDetails(id)

            try {
                val response = call.execute()  // This is a synchronous call
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    Optional.of(StructureDetailsData(
                        result.id,
                        result.name,
                        result.note,
                        result.plans,
                        result.sensors
                    ))
                } else {
                    Optional.empty()
                }
            } catch (e: Exception) {
                Log.d("ERROR API", e.message.toString())
                Optional.empty()
            }
        }
    }


    suspend fun downloadStructure(structure: StructureData, context: Context) {
        val optionalResult = getStructureDetailsFromApi(structure.id)
        if(optionalResult.isPresent) {
            val result = optionalResult.get()
            CoroutineScope(Dispatchers.IO).launch {
                result.plans.forEach { plan ->
                    planDao.upsertPlan(
                        PlanDB(
                            plan.id,
                            plan.name,
                            plan.section,
                            plan.imageUrl,
                            structure.id
                        )
                    )
                    downloadPlanImage(context, plan.id)
                }

                result.sensors.forEach { sensor ->
                    sensorDao.upsertSensor(
                        SensorDB(
                            "${sensor.sensorId.controlChip}-${sensor.sensorId.measureChip}",
                            sensor.sensorId.controlChip,
                            sensor.sensorId.measureChip,
                            sensor.name,
                            sensor.note ?: "",
                            sensor.installationDate,
                            "", // TODO
                            sensor.x,
                            sensor.y,
                            structure.id
                        )
                    )
                }
            }
        }
        structure.downloaded = true
        structureDao.upsertStructure(structure)
    }

    /**
     * Deletes a structure and all its associated data (plans, sensors, images).
     * This includes both database entries and locally stored files.
     *
     * @param structure The structure to be deleted
     * @param context Context needed for file operations
     */
    fun deleteStructure(structure: StructureData, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val planIds = planDao.getPlanByStructureId(structure.id)

                planIds.forEach { planId ->
                    FileUtils.deletePlanImage(context, planId)
                }

                sensorDao.deleteSensorsByStructureId(structure.id)
                resultDao.deleteResults()
                planDao.deletePlansByStructureId(structure.id)
                structureDao.deleteStructure(structure)

                Log.d(TAG, "Structure ${structure.id} and all associated data deleted successfully")
            } catch (e: SQLiteException) {
                Log.e(TAG, "Database error while deleting structure ${structure.id}", e)
            } catch (e: IOException) {
                Log.e(TAG, "I/O error while deleting structure files ${structure.id}", e)
            } catch (e: SecurityException) {
                Log.e(TAG, "Security error while accessing files for structure ${structure.id}", e)
            }
        }
    }


    /**
     * Downloads and saves the plan image to local storage.
     * @param planId the id of the plan
     * @return the path to the saved image file
     */
    suspend fun downloadPlanImage(context: Context, planId: Long): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.structureApi.downloadPlanImage(planId)
                if (response.isSuccessful) {
                    response.body()?.byteStream()?.let { inputStream ->
                        val path = FileUtils.savePlanImageToInternalStorage(context, planId, inputStream)
                        Log.d(TAG, "Plan downloaded under $path")
                        path
                    }
                } else {
                    Log.e(TAG, "Failed to fetch plan $planId image: ${response.code()} - ${response.message()}")
                    null
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network or I/O error while downloading plan image", e)
                null
            } catch (e: SecurityException) {
                Log.e(TAG, "Security error accessing storage", e)
                null
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Error with response processing", e)
                null
            }
        }
    }
}