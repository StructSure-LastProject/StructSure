package fr.uge.structsure.homePage.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.database.AppDatabase
import fr.uge.structsure.homePage.data.StructureData
import fr.uge.structsure.homePage.data.StructureRepository
import kotlinx.coroutines.launch

class StructureViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StructureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StructureViewModel(StructureRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class StructureViewModel(private val structureRepository: StructureRepository): ViewModel() {

    private val _getAllStructures = MutableLiveData<List<StructureData>>()
    val getAllStructures: LiveData<List<StructureData>> = _getAllStructures
    val isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getAllStructures() {
        viewModelScope.launch {
            isRefreshing.postValue(true)
            val structures = structureRepository.getAllStructures()
            _getAllStructures.postValue(structures)
            isRefreshing.postValue(false)
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