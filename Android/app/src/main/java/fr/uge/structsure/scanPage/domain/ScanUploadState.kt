package fr.uge.structsure.scanPage.domain

sealed class ScanUploadState {
    object Success : ScanUploadState()  // for successful uploads locally
    object UploadSuccess : ScanUploadState()  // for successful uploads to the server
    data class Error(val message: String) : ScanUploadState()
}