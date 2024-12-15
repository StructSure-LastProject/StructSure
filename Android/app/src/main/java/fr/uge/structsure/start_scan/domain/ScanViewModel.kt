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
 * États possibles pour le déroulement du scan.
 */
enum class ScanState {
    NOT_STARTED, // Aucun scan n'est lancé
    STARTED,     // Scan en cours
    PAUSED,      // Scan en pause
    STOPPED      // Scan arrêté
}

/**
 * ViewModel pour gérer la logique métier du scan.
 * - Responsable de la gestion des états du scan (démarrer, pause, reprise, arrêt).
 * - Interagit avec la base de données pour stocker et récupérer les capteurs.
 */
class ScanViewModel(private val scanDao: ScanDao, private val context: Context) : ViewModel() {

    // État actuel du scan
    val currentScanState = mutableStateOf(ScanState.NOT_STARTED)

    // ID du scan en cours
    var activeScanId: Long? = null
        private set

    // Messages observables pour les capteurs avec état "OK"
    val sensorMessages = mutableStateListOf<String>()

    // Contrôle la progression du scan
    private var continueScanning = true

    // Dernier capteur traité
    private var lastProcessedSensorIndex = 0

    /**
     * Crée un nouveau scan avec des capteurs associés.
     * - Ajoute un scan et ses capteurs dans la base de données.
     * - Commence l'interrogation des capteurs.
     *
     * @param structureId Identifiant de la structure scannée.
     * @param sensorDetails Liste des capteurs avec leurs coordonnées.
     */
    fun createNewScan(structureId: Int, sensorDetails: List<Pair<String, Pair<Int, Int>>>) {
        viewModelScope.launch(Dispatchers.IO) {
            val newScan = ScanEntity(
                structureId = structureId,
                date = System.currentTimeMillis()
            )
            val scanId = scanDao.insertScan(newScan)
            activeScanId = scanId

            // Ajoute les capteurs à la base de données
            val sensors = sensorDetails.mapIndexed { index, (name, coords) ->
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
            lastProcessedSensorIndex = 0
            startSensorInterrogation(sensors)
        }
    }

    /**
     * Lance l'interrogation progressive des capteurs.
     * - Interroge les capteurs un par un en simulant des délais.
     *
     * @param sensors Liste des capteurs à interroger.
     */
    private fun startSensorInterrogation(sensors: List<SensorEntity>) {
        continueScanning = true
        viewModelScope.launch(Dispatchers.IO) {
            for (i in lastProcessedSensorIndex until sensors.size) {
                if (!continueScanning) {
                    lastProcessedSensorIndex = i // Sauvegarde l'index du dernier capteur
                    return@launch
                }

                val sensor = sensors[i]
                delay(100) // Simule un délai pour l'interrogation

                // Détermine l'état du capteur de manière aléatoire
                val newState = when ((1..3).random()) {
                    1 -> "OK"
                    2 -> "DEFECTIVE"
                    else -> "NOK"
                }

                // Mise à jour de l'état dans la base de données
                scanDao.updateSensorState(sensor.id, newState)

                // Ajoute un message si le capteur est "OK"
                if (newState == "OK") {
                    viewModelScope.launch(Dispatchers.Main) {
                        sensorMessages.add("Capteur ${sensor.name} is OK!")
                    }
                }
            }
        }
    }

    /**
     * Récupère les capteurs associés au scan en cours.
     * @return Liste des capteurs.
     */
    suspend fun getSensors(): List<SensorEntity> {
        return scanDao.getSensorsByScanId(activeScanId ?: return emptyList())
    }

    /**
     * Met le scan en pause.
     * Arrête temporairement l'interrogation des capteurs.
     */
    fun pauseScan() {
        currentScanState.value = ScanState.PAUSED
        continueScanning = false
    }

    /**
     * Reprend le scan après une pause.
     * @param sensors Liste des capteurs à reprendre.
     */
    fun resumeScan(sensors: List<SensorEntity>) {
        currentScanState.value = ScanState.STARTED
        startSensorInterrogation(sensors)
    }

    /**
     * Arrête définitivement le scan.
     * Réinitialise les données et arrête l'interrogation des capteurs.
     */
    fun stopScan() {
        currentScanState.value = ScanState.STOPPED
        continueScanning = false
        sensorMessages.clear()
        activeScanId = null
        lastProcessedSensorIndex = 0 // Réinitialise l'index pour recommencer
    }
}
