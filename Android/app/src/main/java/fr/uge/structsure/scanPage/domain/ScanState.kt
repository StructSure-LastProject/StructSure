package fr.uge.structsure.scanPage.domain

/**
 * Possible states for the scanning process.
 */
enum class ScanState {
    NOT_STARTED,
    STARTED,
    PAUSED,
    STOPPED
}
