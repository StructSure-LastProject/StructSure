package fr.uge.structsure.structuresPage.presentation.components

enum class StructureStates(val message: String) {
    ONLINE("En ligne"),
    DOWNLOADING("Téléchargement en cours"),
    AVAILABLE("Disponible hors-connexion"),
    UPLOADING("Synchronisation en cours")
}
