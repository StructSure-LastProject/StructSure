// SensorCache.kt
package fr.uge.structsure.cache

import fr.uge.structsure.structuresPage.data.SensorDB

class SensorCache {
    private val sensorCache = mutableListOf<SensorDB>()


    // Method to insert multiple sensors into the cache
    fun insertSensors(sensors: List<SensorDB>) {
        sensorCache.addAll(sensors)
    }

    // Method to retrieve all sensors from the cache
    fun getAllSensors(): List<SensorDB> {
        return sensorCache.toList()
    }

    // Method to clear the cache
    fun clearCache() {
        sensorCache.clear()
    }
}