package fr.uge.structsure.scanPage.data.cache

import fr.uge.structsure.structuresPage.data.SensorDB

/**
 * Cache for storing sensor data in memory.
 * The cache is used to store sensor data and manage sensor states.
 */
class SensorCache {

    private val lock = Any()

    // Map: sensorId -> (SensorDB, previousState)
    private val sensorMap = mutableMapOf<String, Pair<SensorDB, String?>>()

    // Map: chipId -> sensorId
    private val chipToSensorIdMap = mutableMapOf<String, String>()

    /**
     * Inserts a list of sensors into the cache.
     *
     * @param sensors The list of sensors to insert.
     */
    fun insertSensors(sensors: List<SensorDB>) {
        synchronized(lock) {
            for (sensor in sensors) {
                sensorMap[sensor.sensorId] = Pair(sensor, null)
                chipToSensorIdMap[sensor.controlChip] = sensor.sensorId
                chipToSensorIdMap[sensor.measureChip] = sensor.sensorId
            }
        }
    }

    /**
     * Finds a sensor by its chip ID.
     * Returns null if the sensor is not found.
     *
     * @param chipId The chip ID of the sensor.
     */
    fun findSensor(chipId: String): SensorDB? {
        synchronized(lock) {
            val sensorId = chipToSensorIdMap[chipId] ?: return null
            return sensorMap[sensorId]?.first
        }
    }

    /**
     * Merges two states into a single state.
     * The states are one of "OK", "NOK", "DEFECTIVE" or "".
     * The merge is done as follows:
     * - If one of the states is "NOK", the result is "NOK".
     * - If one of the states is "OK" and the other is not "NOK", the result is "NOK".
     * - If one of the states is "OK", the result is "OK".
     * - If one of the states is "DEFECTIVE", the result is "DEFECTIVE".
     * - Otherwise, the result is "".
     *
     * @param lastState The last state received from the server.
     * @param newState The new state received from the RFID chip.
     */
    private fun mergeStates(lastState: String?, newState: String): String {
        if (lastState == null || lastState == ""){
            return newState
        }
        if (lastState == "NOK" || newState == "NOK") {
            return "NOK"
        }
        if (lastState == "OK") {
            return if (newState == "OK") "OK" else "NOK"
        }
        return if (newState == "OK") "NOK" else "DEFECTIVE"
    }

    /**
     * Updates the state of a sensor in the cache.
     * Returns the new state if it has changed, or null if it has not.
     * The new state is computed by merging the previous state with the new state.
     * The previous state is the last state received from the server.
     * The new state is the state received from the RFID chip.
     *
     * @param sensor The sensor being updated.
     * @param newState The new state of the sensor.
     * @return The new state of the sensor if it has changed, or null otherwise.
     */
    fun updateSensorState(sensor: SensorDB, newState: String): String? {
        synchronized(lock) {
            val serverState = sensor.state
            val secondPair = sensorMap[sensor.sensorId]?.second
            val lastState = if(secondPair == null || secondPair == "") serverState else secondPair
            val computedState = mergeStates(lastState, newState)
            if (computedState != lastState) {
                sensorMap[sensor.sensorId] = Pair(sensor,computedState)
                return computedState
            }
            return null
        }
    }

    /**
     * Clears the entire cache.
     */
    fun clearCache() {
        synchronized(lock) {
            sensorMap.clear()
            chipToSensorIdMap.clear()
        }
    }
}
