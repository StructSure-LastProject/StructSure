package fr.uge.structsure.download_structure.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StructureViewModel(
    private val repository: StructureRepository
) : ViewModel() {
    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState

    fun downloadStructure(name: String) {
        viewModelScope.launch {
            _downloadState.value = DownloadState.Downloading
            try {
                repository.downloadStructure(name)
                _downloadState.value = DownloadState.Success
            } catch (e: Exception) {
                _downloadState.value = DownloadState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }
}

class StructureViewModelFactory(
    private val repository: StructureRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StructureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StructureViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
