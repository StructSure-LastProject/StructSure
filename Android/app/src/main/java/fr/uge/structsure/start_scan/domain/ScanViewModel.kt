package fr.uge.structsure.start_scan.domain

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.retrofit.response.GetAllSensorsResponse
import fr.uge.structsure.start_scan.data.ScanEntity
import fr.uge.structsure.start_scan.data.SensorEntity
import fr.uge.structsure.start_scan.data.dao.ScanDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
 * - Récupère les capteurs depuis le backend Spring.
 * - Insère les capteurs dans la base de données Room.
 * - Démarre et gère le processus de scan.
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
     * Récupère les capteurs depuis le backend et les insère dans Room.
     */
    fun fetchSensorsAndStartScan(structureId: Long) {
        RetrofitInstance.sensorApi.getAllSensors(structureId).enqueue(object :
            Callback<List<GetAllSensorsResponse>> {
            override fun onResponse(
                call: Call<List<GetAllSensorsResponse>>,
                response: Response<List<GetAllSensorsResponse>>
            ) {
                if (response.isSuccessful) {
                    val sensors = response.body()
                    sensors?.let { sensorList ->
                        insertSensorsAndStartScan(sensorList)
                    }
                }
            }

            override fun onFailure(call: Call<List<GetAllSensorsResponse>>, t: Throwable) {
                println("Erreur réseau : ${t.localizedMessage}")
            }
        })
    }

    /**
     * Convertit la réponse par Retrofit en SensorEntity et démarre le scan.
     */
    private fun insertSensorsAndStartScan(sensors: List<GetAllSensorsResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            val newScan = ScanEntity(structureId = 1, date = System.currentTimeMillis())
            val scanId = scanDao.insertScan(newScan)

            // Filtrer les doublons
            val uniqueSensors = sensors.distinctBy { it.controlChip to it.measureChip }

            val sensorEntities = uniqueSensors.map { sensor ->
                SensorEntity(
                    controlChip = sensor.controlChip,
                    measureChip = sensor.measureChip,
                    name = sensor.name,
                    note = sensor.note,
                    state = "UNSCAN",
                    installationDate = sensor.installationDate,
                    x = sensor.x,
                    y = sensor.y
                )
            }

            scanDao.insertSensors(sensorEntities)
            currentScanState.value = ScanState.STARTED

            // LOG pour vérifier les capteurs insérés
            println("Capteurs insérés : ${sensorEntities.size}")

            // Démarre l'interrogation des capteurs
            startSensorInterrogation(sensorEntities)
        }
    }

    /**
     * Lance l'interrogation progressive des capteurs.
     * Met à jour leur état dans la base de données et affiche un message pour les capteurs "OK".
     */
    private fun startSensorInterrogation(sensors: List<SensorEntity>) {
        continueScanning = true // Contrôle le déroulement du scan
        viewModelScope.launch(Dispatchers.IO) {
            for (i in lastProcessedSensorIndex until sensors.size) {
                if (!continueScanning) {
                    lastProcessedSensorIndex = i // Sauvegarde l'index pour reprendre plus tard
                    return@launch
                }

                val sensor = sensors[i]
                delay(2000) // Simule un délai d'interrogation (2 secondes)

                // Détermine un état aléatoire pour le capteur
                val newState = when ((1..3).random()) {
                    1 -> "OK"
                    2 -> "DEFECTIVE"
                    else -> "NOK"
                }

                // Met à jour l'état du capteur dans la base de données
                scanDao.updateSensorState(sensor.controlChip, sensor.measureChip, newState)

                // Si le capteur passe à l'état "OK", ajoute un message pour l'affichage
                if (newState == "OK") {
                    viewModelScope.launch(Dispatchers.Main) {
                        sensorMessages.add("Capteur ${sensor.name} is OK!")
                    }
                }
            }
        }
    }

    /**
     * Met le scan en pause.
     */
    fun pauseScan() {
        currentScanState.value = ScanState.PAUSED
        continueScanning = false
    }

    /**
     * Reprend le scan après une pause.
     */
    fun resumeScan(sensors: List<SensorEntity>) {
        currentScanState.value = ScanState.STARTED
        startSensorInterrogation(sensors)
    }

    /**
     * Arrête définitivement le scan et réinitialise les données.
     */
    fun stopScan() {
        currentScanState.value = ScanState.STOPPED
        continueScanning = false
        sensorMessages.clear()
        activeScanId = null
        lastProcessedSensorIndex = 0
    }
    
    
}
