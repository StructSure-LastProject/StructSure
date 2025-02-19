package fr.uge.structsure.homePage.presentation.components

enum class StructureStates(val message: String) {
    AVAILABLE("Disponible hors-connexion"),
    ONLINE("En ligne"),
    DOWNLOADING("Synchronisation en cours")
}
