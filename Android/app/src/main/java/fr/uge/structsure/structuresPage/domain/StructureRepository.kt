package fr.uge.structsure.structuresPage.domain

import android.util.Log
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.retrofit.StructureApi
import fr.uge.structsure.retrofit.response.GetAllStructureResponse
import fr.uge.structsure.structuresPage.data.StructureDao
import fr.uge.structsure.structuresPage.data.StructureData
import retrofit2.Call
import retrofit2.Response

class StructureRepository(
    private val structureDao: StructureDao
) {
    private fun getApiInterface(): StructureApi {
        return RetrofitInstance.structureApi
    }


    private fun getFromApi(){
        val apiInterface = getApiInterface()
        val call = apiInterface.getAllStructures()

        call.enqueue(object : retrofit2.Callback<List<GetAllStructureResponse>> {
            override fun onResponse(
                p0: Call<List<GetAllStructureResponse>>,
                p1: Response<List<GetAllStructureResponse>>
            ) {
                if (p1.isSuccessful && p1.body() != null) {
                    p1.body()!!.forEach { it ->
                        structureDao.upsertStructure(
                            StructureData(
                            it.id,
                            it.name,
                            it.numberOfSensors,
                            it.numberOfPlans,
                            it.url,
                                false
                        )
                        )
                    }
                }
            }

            override fun onFailure(p0: Call<List<GetAllStructureResponse>>, p1: Throwable) {
                Log.d("ERROR API", p1.message.toString())
            }
        })
    }

    fun getAllStructures(): List<StructureData> {
        var structuresFromDB = structureDao.getAllStructures()
        if (structuresFromDB.isEmpty()){
            getFromApi()
            structuresFromDB = structureDao.getAllStructures()
        }
        return structuresFromDB
    }


    fun downloadStructure(name: String) {
        // TODO
    }

}