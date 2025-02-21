package fr.uge.structsure.structuresPage.domain

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.scanPage.data.repository.ScanRepository
import fr.uge.structsure.structuresPage.data.StructureData
import fr.uge.structsure.structuresPage.data.StructureRepository
import fr.uge.structsure.structuresPage.presentation.components.StructureStates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The factory for the structure view model.
 * @param context the context of the application
 */
class StructureViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StructureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StructureViewModel(StructureRepository(), ScanRepository(context), context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * The view model for the structures page.
 * @param structureRepository the repository for the structures
 * @param scanRepository the repository for the scans
 * @param context the context of the application
 */
class StructureViewModel(private val structureRepository: StructureRepository,
                         private val scanRepository: ScanRepository,
                         private val context: Context
): ViewModel() {

    private val connectivityViewModel: ConnectivityViewModel = ConnectivityViewModel(context)
    val getAllStructures = MutableLiveData<List<StructureData>?>()
    private val uploadInProgress = MutableLiveData<Boolean>(false)
    val structureStates = MutableLiveData<MutableMap<Long, StructureStates>>(mutableMapOf())

    /**
     * Initializes the view model.
     */
    init {
        connectivityViewModel.isConnected.distinctUntilChanged().observeForever { isConnected ->
            if (!isConnected || uploadInProgress.value == true) return@observeForever

            viewModelScope.launch(Dispatchers.IO) {
                uploadInProgress.postValue(true)
                val unsentScans = scanRepository.getUnsentScans()
                unsentScans.forEach { scan ->
                    Log.i("StructureVM", "Found unsent scan #${scan.id} - ${scan.structureId}")
                    tryUploadScan(scan.structureId, scan.id)
                }
                getAllStructures()
                uploadInProgress.postValue(false)
            }
        }
    }

    /**
     * Gets all structures from the repository and sets the value of [getAllStructures].
     */
    fun getAllStructures() {
        viewModelScope.launch {
            val structures = structureRepository.getAllStructures()
            getAllStructures.postValue(structures)
        }
    }

    /**
     * Downloads the structure with the given data.
     * @param structureData the data of the structure to download
     */
    fun downloadStructure(structureData: StructureData) {
        viewModelScope.launch {
            structureRepository.downloadStructure(structureData, context)
        }
    }

    /**
     * Deletes the structure with the given id.
     * @param structureId the id of the structure to delete
     */
    fun deleteStructure(structureId: Long) {
        viewModelScope.launch {
            structureRepository.deleteStructure(structureId, context)
        }
    }

    /**
     * Sets the state of the structure with the given id.
     * @param structureId the id of the structure
     * @param state the state to set
     */
    private fun setStructureState(structureId: Long, state: StructureStates) {
        val currentStates = structureStates.value ?: mutableMapOf()
        currentStates[structureId] = state
        structureStates.postValue(currentStates)
    }

    /**
     * Called when the view model is cleared.
     */
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

        try{
            val scanRequest = scanRepository.convertToScanRequest(scanId, results)
            scanRepository.submitScanResults(scanRequest)
                .onSuccess {
                    Log.i("StructureVM", "Scan #${scanId} successfully uploaded, removing data...")
                    deleteStructure(structureId)
                    setStructureState(structureId, StructureStates.ONLINE)
                    getAllStructures()
                }.onFailure { e ->
                    Log.w("StructureVM", "Failed to upload results of scan #${scanId} (structure ${structureId}): ${e.message}")
                    setStructureState(structureId, StructureStates.UPLOADING)
                }

        }catch (e: Exception) {
            Log.e("StructureVM", "Error during upload", e)
            setStructureState(structureId, StructureStates.UPLOADING)
        }
    }
}