package fr.uge.structsure.structuresPage.presentation.components

/**
 * Enum class representing the different states a structure can be in.
 * @property message The message associated with the state.
 */
enum class StructureStates(val message: String) {
    ONLINE("En ligne"),
    DOWNLOADING("Téléchargement en cours"),
    AVAILABLE("Disponible hors-connexion"),
    UPLOADING("Synchronisation en cours")
}
