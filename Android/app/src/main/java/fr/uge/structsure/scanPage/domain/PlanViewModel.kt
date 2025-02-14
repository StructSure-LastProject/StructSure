package fr.uge.structsure.scanPage.domain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.structuresPage.data.PlanDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PlanViewModel : ViewModel() {
    private val planDao = db.planDao()

    val plans = MutableLiveData<List<PlanDB>>()

    fun fetchPlansForStructure(structureId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedPlans = planDao.getPlansByStructureId(structureId)
            plans.postValue(fetchedPlans)
        }
    }
}
