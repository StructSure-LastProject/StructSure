package fr.uge.structsure.structuresPage.domain

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.database.AppDatabase
import fr.uge.structsure.scanPage.data.repository.ScanRepository
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
            return StructureViewModel(StructureRepository(), ScanRepository(context), context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class StructureViewModel(private val structureRepository: StructureRepository, val scanRepository: ScanRepository,
                         private val context: Context): ViewModel() {
    val connectivityViewModel: ConnectivityViewModel = ConnectivityViewModel(context)
    val getAllStructures = MutableLiveData<List<StructureData>?>()
    val hasUnsentResults = MutableLiveData<Boolean>()

    private var structureWithUnsentResults: Long? = null

    val structureStates =
        MutableLiveData<MutableMap<Long, StructureStates>>(mutableMapOf())

    init {
        observeConnectivity()
    }

    private fun observeConnectivity() {
        connectivityViewModel.isConnected.observeForever { isConnected ->
            if (!isConnected) {
                structureWithUnsentResults?.let { structureId ->
                    val currentStates = structureStates.value ?: mutableMapOf()
                    currentStates[structureId] = StructureStates.DOWNLOADING
                    structureStates.postValue(currentStates)
                }
            }
        }
    }

    fun getAllStructures() {
        viewModelScope.launch {
            val structures = structureRepository.getAllStructures()
            getAllStructures.postValue(structures)
        }
    }

    fun downloadStructure(structureData: StructureData) {
        viewModelScope.launch {
            structureRepository.downloadStructure(structureData, context)
        }
    }

    fun deleteStructure(structureData: StructureData) {
        viewModelScope.launch {
            structureRepository.deleteStructure(structureData, context)
        }
    }

    fun setHasUnsentResults(hasResults: Boolean, structureId: Long? = null) {
        hasUnsentResults.postValue(hasResults)
        structureWithUnsentResults = if (hasResults) structureId else null

        if (hasResults && structureId != null && connectivityViewModel.isConnected.value == false) {
            setStructureState(structureId, StructureStates.DOWNLOADING)
        }
        if (!hasResults && structureId != null && connectivityViewModel.isConnected.value == true) {
            setStructureState(structureId, StructureStates.ONLINE)
        }
    }

    fun setStructureState(structureId: Long, state: StructureStates) {
        val currentStates = structureStates.value ?: mutableMapOf()
        currentStates[structureId] = state
        structureStates.postValue(currentStates)
    }

    override fun onCleared() {
        super.onCleared()
        connectivityViewModel.isConnected.removeObserver { }
    }
}