package fr.uge.structsure.structuresPage.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.retrofit.StructureApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.Optional

class StructureRepository(
    private val structureDao: StructureDao,
    private val planDao: PlanDao,
    private val sensorDao: SensorDao
): ViewModel() {
    private fun getApiInterface(): StructureApi {
        return RetrofitInstance.structureApi
    }


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
                            it.url,
                            false,
                            ""
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


    suspend fun downloadStructure(structure: StructureData) {
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
                }

                result.sensors.forEach { sensor ->
                    sensorDao.upsertSensor(
                        SensorDB(
                            "${sensor.sensorId.controlChip}-${sensor.sensorId.measureChip}",
                            sensor.sensorId.controlChip,
                            sensor.sensorId.measureChip,
                            sensor.name,
                            sensor.note,
                            sensor.installationDate,
                            state = "",
                            sensor.x,
                            sensor.y,
                            structure.id
                        )
                    )
                }
            }
        }
        structure.state = true
        structureDao.upsertStructure(structure)
    }

    fun deleteStructure(structure: StructureData){
        CoroutineScope(Dispatchers.IO).launch {
            sensorDao.deleteSensorsByStructureId(structure.id)
        }
        CoroutineScope(Dispatchers.IO).launch {
            planDao.deletePlansByStructureId(structure.id)
        }
        CoroutineScope(Dispatchers.IO).launch {
            structureDao.deleteStructure(structure)
        }
    }

}