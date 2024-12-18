package fr.uge.structsure.structuresPage.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.database.AppDatabase
import fr.uge.structsure.structuresPage.data.StructureData
import fr.uge.structsure.structuresPage.data.StructureRepository
import kotlinx.coroutines.launch

class StructureViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StructureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StructureViewModel(StructureRepository(db.structureDao(), db.planDao(), db.sensorDao())) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class StructureViewModel(private val structureRepository: StructureRepository): ViewModel() {

    private val _getAllStructures = MutableLiveData<List<StructureData>>()
    val getAllStructures: LiveData<List<StructureData>> = _getAllStructures


    fun getAllStructures() {
        viewModelScope.launch {
            val structures = structureRepository.getAllStructures()
            _getAllStructures.postValue(structures)
        }
    }

    fun downloadStructure(structureData: StructureData){
        viewModelScope.launch {
            structureRepository.downloadStructure(structureData)
        }
    }

    fun deleteStructure(structureData: StructureData){
        viewModelScope.launch {
            structureRepository.deleteStructure(structureData)
        }
    }

}