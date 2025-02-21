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
    companion object {
        private const val TAG = "StructureViewModel"
    }

    private val connectivityViewModel: ConnectivityViewModel = ConnectivityViewModel(context)
    val getAllStructures = MutableLiveData<List<StructureWithState>?>()
    private val uploadInProgress = MutableLiveData<Boolean>(false)
    
    /**
     * Initializes the view model.
     */
    init {
        connectivityViewModel.isConnected.distinctUntilChanged().observeForever { isConnected ->
            if (isConnected) getAllStructures()
            if (!isConnected || uploadInProgress.value == true) return@observeForever

            tryUploadScans(isConnected, uploadInProgress.value ?: false, null)
        }
    }

    /**
     * Gets all structures from the repository and sets the value of [getAllStructures].
     */
    fun getAllStructures() {
        viewModelScope.launch {
            val structures = structureRepository.getAllStructures().map { StructureWithState(it) }
            getAllStructures.postValue(structures)
        }
    }


    fun findAll() {
        viewModelScope.launch {
            val structures = structureRepository.getAllStructures()
                .map { StructureWithState(it) }
            getAllStructures.postValue(structures)
        }
    }

    /**
     * Downloads the structure with the given data.
     * @param structureData the data of the structure to download
     */
    fun download(structureData: StructureWithState){
        viewModelScope.launch {
            structureData.state.value = StructureStates.DOWNLOADING
            val success = structureRepository.downloadStructure(structureData.raw, context)
            structureData.state.value = if (success) StructureStates.AVAILABLE else StructureStates.ONLINE
        }
    }

    /**
     * Deletes the given structure
     * @param structureId the id of the structure to delete
     */
    fun delete(structureId: Long){
        viewModelScope.launch {
            structureRepository.deleteStructure(structureId, context)
            findAll() // forces refresh to adapt to connectivity state
        }
    }

    /**
     * Sets the state of the structure with the given id.
     * @param structureId the id of the structure
     * @param state the state to set
     */
    private fun setStructureState(structureId: Long, state: StructureStates) {
        val structure = getAllStructures.value?.find { it.id == structureId }
        if (structure == null) return
        structure.state.postValue(state)
    }

    /**
     * Called when the view model is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        connectivityViewModel.isConnected.removeObserver { }
    }

    /**
     * Tries to upload the scan with the given id.
     * @param structureId the id of the structure
     * @param scanId the id of the scan
     */
    suspend fun tryUploadScan(structureId: Long, scanId: Long) {
        setStructureState(structureId, StructureStates.UPLOADING)
        val results = scanRepository.getResultsByScan(scanId)

        val scanRequest = scanRepository.convertToScanRequest(scanId, results)
        scanRepository.submitScanResults(scanRequest)
            .onSuccess {
                Log.i(TAG, "Scan #${scanId} successfully uploaded, removing data...")
                delete(structureId)
                setStructureState(structureId, StructureStates.ONLINE)
                getAllStructures()
            }.onFailure { e ->
                Log.w(TAG, "Failed to upload results of scan #${scanId} (structure ${structureId}): ${e.message}")
            }
    }

    /**
     * Tries to upload all scans.
     * @param isConnected true if the device is connected to the internet, false otherwise
     * @param uploadInProgress true if an upload is already in progress, false otherwise
     * @param login the login of the user
     */
    fun tryUploadScans(isConnected: Boolean, uploadInProgress: Boolean, login: String?) {
        if (!isConnected || uploadInProgress) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                this@StructureViewModel.uploadInProgress.postValue(true)

                val unsentScans = scanRepository.getUnsentScans()
                if (unsentScans.isEmpty()) return@launch

                if (login != null) {
                    val lastScanTechnician = unsentScans.lastOrNull()?.technician
                    if (lastScanTechnician != null && lastScanTechnician != login) {
                        unsentScans.forEach { scan ->
                            structureRepository.deleteStructure(scan.structureId, context)
                        }
                        return@launch
                    }
                }

                unsentScans.forEach { scan ->
                    Log.i(TAG, "Found unsent scan #${scan.id} - ${scan.structureId}")
                    tryUploadScan(scan.structureId, scan.id)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during batch upload", e)
            } finally {
                this@StructureViewModel.uploadInProgress.postValue(false)
            }
        }
    }
}

data class StructureWithState(
    val id: Long = 0,
    val name: String,
    val state: MutableLiveData<StructureStates>,
    val raw: StructureData
) {
    constructor(data: StructureData) : this(
        data.id,
        data.name,
        MutableLiveData(if (data.downloaded) StructureStates.AVAILABLE else StructureStates.ONLINE),
        data
    )
}