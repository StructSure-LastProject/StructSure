package fr.uge.structsure.structuresPage.domain

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.scanPage.data.repository.ScanRepository
import fr.uge.structsure.structuresPage.data.StructureData
import fr.uge.structsure.structuresPage.data.StructureRepository
import fr.uge.structsure.structuresPage.presentation.components.StructureStates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StructureViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StructureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StructureViewModel(StructureRepository(), ScanRepository(context), context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class StructureViewModel(private val structureRepository: StructureRepository, private val scanRepository: ScanRepository,
                         private val context: Context
): ViewModel() {
    val connectivityViewModel: ConnectivityViewModel = ConnectivityViewModel(context)
    val getAllStructures = MutableLiveData<List<StructureData>?>()
    val uploadInProgress = MutableLiveData<Boolean>(false)

    val structureStates = MutableLiveData<MutableMap<Long, StructureStates>>(mutableMapOf())

    init {
        connectivityViewModel.isConnected.observeForever { isConnected ->
            if (!isConnected || uploadInProgress.value == true) return@observeForever // No connection, nothing to do
            // TODO stop if not logged in
            viewModelScope.launch(Dispatchers.IO) {
                uploadInProgress.postValue(true)
                scanRepository.getUnsentScans().forEach { scan ->
                    println("Found unsent scan #${scan.id} - ${scan.structureId}")
                    tryUploadScan(scan.structureId, scan.id)
                }
                uploadInProgress.postValue(false)
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

    fun deleteStructure(structureId: Long) {
        viewModelScope.launch {
            structureRepository.deleteStructure(structureId, context)
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

    /**
     * Tries to upload the data of the given scan to the server and
     * remove it from the local storage.
     * @param structureId the id of the structure of the scan
     * @param scanId the id of the scan to upload
     */
    suspend fun tryUploadScan(structureId: Long, scanId: Long) {
        setStructureState(structureId, StructureStates.UPLOADING)
        val results = scanRepository.getResultsByScan(scanId)
        val scanRequest = scanRepository.convertToScanRequest(scanId, results)
        scanRepository.submitScanResults(scanRequest).onSuccess {
            Log.i("StructureVM", "Scan #${scanId} successfully uploaded, removing data...")
            deleteStructure(structureId)
            setStructureState(structureId, StructureStates.ONLINE)
        }.onFailure { e ->
            Log.w("StructureVM", "Failed to upload results of scan #${scanId} (structure ${structureId}): ${e.message}")
        }
    }
}