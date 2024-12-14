package fr.uge.structsure.download_structure.presentation.structures

enum class StructureDownloadState(val state : String) {
    downloadable("En ligne"),
    downloadaing("Téléchargement en cours"),
    synchronizing("Synchronisation en cours"),
    downloaded("Disponible hors-connexion"),
}