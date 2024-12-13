package fr.uge.structsure.start_scan.domain

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.start_scan.data.ScanEntity
import fr.uge.structsure.start_scan.data.SensorEntity
import fr.uge.structsure.start_scan.data.dao.ScanDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

/**
 * État possible pour un scan.
 */
enum class ScanState { NOT_STARTED, STARTED, PAUSED, STOPPED }

/**
 * ViewModel pour gérer les scans et leur état dans l'application.
 *
 * @param scanDao DAO pour interagir avec la base de données locale des scans.
 */
class ScanViewModel(private val scanDao: ScanDao) : ViewModel() {

    // État actuel du scan (initialisé à NOT_STARTED).
    val currentScanState = mutableStateOf(ScanState.NOT_STARTED)

    // ID du scan actif (null si aucun scan n'est actif).
    var activeScanId: Long? = null
        private set

    /**
     * Crée un nouveau scan dans la base de données avec un état par défaut pour tous les capteurs.
     *
     * @param structureId ID de la structure associée au scan.
     * @param sensorDetails Liste des capteurs associés au scan (nom et coordonnées x, y).
     */
    fun createNewScan(structureId: Int, sensorDetails: List<Pair<String, Pair<Int, Int>>>) {
        viewModelScope.launch(Dispatchers.IO) {
            // Création d'un nouveau scan.
            val newScan = ScanEntity(
                structureId = structureId,
                date = Date().time
            )

            // Insertion du scan et récupération de son ID.
            val scanId = scanDao.insertScan(newScan)
            activeScanId = scanId

            // Création des capteurs associés à ce scan avec l'état par défaut (UNSCAN).
            val sensors = sensorDetails.map { (name, coords) ->
                SensorEntity(
                    scanId = scanId,
                    controlChip = "chip_$name",
                    measureChip = "chip_${name}_measure",
                    name = name,
                    x = coords.first,
                    y = coords.second
                )
            }
            scanDao.insertSensors(sensors)

            // Mise à jour de l'état du scan.
            currentScanState.value = ScanState.STARTED
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
        viewModelScope.launch(Dispatchers.IO) {
            activeScanId = null
            currentScanState.value = ScanState.STOPPED
        }
    }

    /**
     * Met à jour l'état d'un capteur dans la base de données.
     *
     * @param sensorId ID du capteur.
     * @param newState Nouvel état du capteur (OK, NOK, DEFECTIVE, UNSCAN).
     */
    fun updateSensorState(sensorId: Long, newState: String) {
        viewModelScope.launch(Dispatchers.IO) {
            scanDao.updateSensorState(sensorId, newState)
        }
    }

    /**
     * Fournit une factory pour créer une instance de ScanViewModel.
     *
     * @param scanDao DAO pour interagir avec la base de données.
     * @return Factory pour ScanViewModel.
     */
    companion object {
        fun provideFactory(scanDao: ScanDao): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ScanViewModel::class.java)) {
                        return ScanViewModel(scanDao) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}
