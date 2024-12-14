package fr.uge.structsure.start_scan.domain

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.start_scan.data.ScanEntity
import fr.uge.structsure.start_scan.data.SensorEntity
import fr.uge.structsure.start_scan.data.dao.ScanDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * État du scan (non démarré, en cours, en pause, ou arrêté).
 */
enum class ScanState {
    NOT_STARTED,
    STARTED,
    PAUSED,
    STOPPED
}

/**
 * ViewModel pour gérer la logique métier des scans et des capteurs.
 * - Responsable de la création, de la mise à jour et de l'arrêt des scans.
 * - Gère les états des capteurs et la communication avec la base de données.
 */
class ScanViewModel(
    private val scanDao: ScanDao,
    private val context: Context
) : ViewModel() {

    // État actuel du scan
    val currentScanState = mutableStateOf(ScanState.NOT_STARTED)
    var activeScanId: Long? = null
        private set

    // Liste observable des messages de capteurs OK
    val sensorMessages = mutableStateListOf<String>()

    /**
     * Crée un nouveau scan et initialise les capteurs associés.
     * @param structureId ID de la structure associée.
     * @param sensorDetails Détails des capteurs (nom, coordonnées).
     */
    fun createNewScan(structureId: Int, sensorDetails: List<Pair<String, Pair<Int, Int>>>) {
        viewModelScope.launch(Dispatchers.IO) {
            val newScan = ScanEntity(
                structureId = structureId,
                date = System.currentTimeMillis()
            )
            val scanId = scanDao.insertScan(newScan)
            activeScanId = scanId

            val sensors = sensorDetails.map { (name, coords) ->
                SensorEntity(
                    scanId = scanId,
                    controlChip = "chip_$name",
                    measureChip = "chip_${name}_measure",
                    name = name,
                    state = "UNSCAN",
                    x = coords.first,
                    y = coords.second
                )
            }
            scanDao.insertSensors(sensors)

            currentScanState.value = ScanState.STARTED
            simulateSensorInterrogation(sensors)
        }
    }

    /**
     * Simule l'interrogation des capteurs.
     * Met à jour l'état des capteurs et affiche un message pour ceux qui sont OK.
     */
    private fun simulateSensorInterrogation(sensors: List<SensorEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            sensors.forEach { sensor ->
                delay(2000)
                val newState = when ((1..3).random()) {
                    1 -> "OK"
                    2 -> "DEFECTIVE"
                    else -> "NOK"
                }

                scanDao.updateSensorState(sensor.id, newState)
                if (newState == "OK") {
                    viewModelScope.launch(Dispatchers.Main) {
                        sensorMessages.add("Capteur ${sensor.name} is OK!")
                    }
                }
            }
            currentScanState.value = ScanState.PAUSED
        }
    }

    /**
     * Met le scan en pause.
     */
    fun pauseScan() {
        currentScanState.value = ScanState.PAUSED
    }

    /**
     * Arrête le scan et réinitialise l'état.
     */
    fun stopScan() {
        currentScanState.value = ScanState.STOPPED
        sensorMessages.clear()
        activeScanId = null
    }
}
