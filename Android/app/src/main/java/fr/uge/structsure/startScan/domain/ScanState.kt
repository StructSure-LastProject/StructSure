package fr.uge.structsure.startScan.domain

/**
 * États possibles pour le déroulement du scan.
 */
enum class ScanState {
    NOT_STARTED,
    STARTED,
    PAUSED,
    STOPPED
}
