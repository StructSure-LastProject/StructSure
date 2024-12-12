package fr.uge.structsure.start_scan.domain

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

enum class ScanState { NOT_STARTED, STARTED, PAUSED }

data class Sensor(val id: Int, var state: SensorState)

enum class SensorState { OK, NOK, FAULTY, NOT_SCANNED }

class ScanViewModel : ViewModel() {
    var scanState = mutableStateOf(ScanState.NOT_STARTED)
        private set

    var sensors = mutableStateOf(listOf<Sensor>())
        private set

    fun initializeSensors() {
        sensors.value = listOf(
            Sensor(1, SensorState.NOT_SCANNED),
            Sensor(2, SensorState.NOT_SCANNED),
            Sensor(3, SensorState.NOT_SCANNED),
            Sensor(4, SensorState.NOT_SCANNED)
        )
    }

    fun startScan() {
        scanState.value = ScanState.STARTED
        sensors.value = sensors.value.map {
            it.copy(state = SensorState.OK)
        }
    }

    fun pauseScan() {
        scanState.value = ScanState.PAUSED
    }

    fun stopScan() {
        scanState.value = ScanState.NOT_STARTED
        sensors.value = sensors.value.map {
            it.copy(state = SensorState.NOT_SCANNED)
        }
    }
}
