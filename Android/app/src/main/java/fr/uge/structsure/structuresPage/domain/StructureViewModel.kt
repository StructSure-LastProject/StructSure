package fr.uge.structsure.structuresPage.domain

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.database.AppDatabase
import fr.uge.structsure.structuresPage.data.StructureData
import fr.uge.structsure.structuresPage.data.StructureRepository
import fr.uge.structsure.structuresPage.presentation.components.StructureStates
import kotlinx.coroutines.launch

class StructureViewModelFactory(
    private val db: AppDatabase,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StructureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StructureViewModel(StructureRepository(), context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class StructureViewModel(private val structureRepository: StructureRepository, private val context: Context): ViewModel() {

    val getAllStructures = MutableLiveData<List<StructureData>?>()

    val hasUnsentResults = MutableLiveData<Boolean>()

    fun setHasUnsentResults(hasResults: Boolean) {
        hasUnsentResults.postValue(hasResults)
    }

    fun getAllStructures() {
        viewModelScope.launch {
            val structures = structureRepository.getAllStructures()
            getAllStructures.postValue(structures)
        }
    }

    fun downloadStructure(structureData: StructureData){
        viewModelScope.launch {
            structureRepository.downloadStructure(structureData, context)
        }
    }

    fun deleteStructure(structureData: StructureData){
        viewModelScope.launch {
            structureRepository.deleteStructure(structureData, context)
        }
    }

    fun updateStructureState(structureId: Long, newState: StructureStates) {
        viewModelScope.launch {
            val structures = getAllStructures.value?.map { structure ->
                if (structure.id == structureId) {
                    structure.copy(
                        downloaded = newState == StructureStates.AVAILABLE
                    )
                } else {
                    structure
                }
            }
            getAllStructures.postValue(structures)
        }
    }

}