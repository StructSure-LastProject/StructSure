package fr.uge.structsure.download_structure.domain

sealed class DownloadState {
    object Idle : DownloadState() // Aucun téléchargement en cours
    object Downloading : DownloadState() // Téléchargement en cours
    object Success : DownloadState() // Téléchargement réussi
    data class Error(val message: String) : DownloadState() // Erreur avec un message d'erreur
}