package fr.uge.structsure.structuresPage.data

/**
 * A proxy cache for sensors, storing their current state in memory
 * and tracking the previous state for each sensor.
 */
class SensorCache {

    private val lock = Any()

    // Map: sensorId -> Pair<currentState, previousState>
    private val sensorMap = mutableMapOf<String, Pair<SensorDB, String?>>()

    // Map: chipId -> sensorId (to quickly find sensors by chip)
    private val chipToSensorIdMap = mutableMapOf<String, String>()

    /**
     * Inserts a list of sensors into the cache.
     */
    fun insertSensors(sensors: List<SensorDB>) {
        synchronized(lock) {
            for (sensor in sensors) {
                sensorMap[sensor.sensorId] = Pair(sensor, null) // Initialize with no previous state
                chipToSensorIdMap[sensor.controlChip] = sensor.sensorId
                chipToSensorIdMap[sensor.measureChip] = sensor.sensorId
            }
        }
    }

    /**
     * Finds a sensor by its chip ID (control or measure), or null if not found.
     */
    fun findSensor(chipId: String): SensorDB? {
        synchronized(lock) {
            val sensorId = chipToSensorIdMap[chipId] ?: return null
            return sensorMap[sensorId]?.first
        }
    }

    /**
     * Updates a sensor's state in the cache.
     * Tracks the previous state.
     * @param sensor The sensor to update.
     * @param newState The new state to set.
     * @return The previous state of the sensor.
     */
    fun updateSensorState(sensor: SensorDB, newState: String): String? {
        synchronized(lock) {
            val current = sensorMap[sensor.sensorId]
            if (current == null || current.first.state != newState) {
                val previousState = current?.first?.state
                sensor.state = newState
                sensorMap[sensor.sensorId] = Pair(sensor, previousState)
                return previousState
            }
            return current?.second // Return the previously recorded state
        }
    }

    /** Returns all sensors in the cache. */
    fun getAllSensors(): List<SensorDB> {
        synchronized(lock) {
            return sensorMap.values.map { it.first }
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
