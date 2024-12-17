package fr.uge.structsure.download_structure.data

sealed class StructureDownloadState(val state: String) {
    object downloadable : StructureDownloadState("En ligne")
    object downloading : StructureDownloadState("Téléchargement en cours")
    object synchronizing : StructureDownloadState("Synchronisation en cours")
    object downloaded : StructureDownloadState("Disponible hors-connexion")

    companion object {
        fun fromString(state: String): StructureDownloadState {
            return when (state) {
                downloadable.state -> downloadable
                downloading.state -> downloading
                synchronizing.state -> synchronizing
                downloaded.state -> downloaded
                else -> throw IllegalArgumentException("État inconnu : $state")
            }
        }
    }
}