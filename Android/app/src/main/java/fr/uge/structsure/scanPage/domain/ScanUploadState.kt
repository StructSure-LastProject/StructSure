package fr.uge.structsure.scanPage.domain

sealed class ScanUploadState {
    object Success : ScanUploadState()
    data class Error(val message: String) : ScanUploadState()
}