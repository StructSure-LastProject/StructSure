package fr.uge.structsure.scanPage.domain

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.bluetooth.cs108.RfidChip

/**
 * List of scanned chips with their attenuation updated each time the
 * chip is read.
 */
object ChipFinder {

    /** Chips stored by ID */
    private val entries = mutableStateMapOf<String, Chip>()

    /** Publicly exposed list of scanned chip */
    val chips = mutableStateListOf<Chip>()

    /** Clears all scanned chip from the list. */
    fun reset() {
        synchronized(this) {
            entries.clear()
            chips.clear()
        }
    }

    /**
     * Adds the given chip in the scanned chip list or updates its
     * attenuation if already present.
     * @param rfidChip the chip to add to the list
     */
    fun add(rfidChip: RfidChip) {
        if (rfidChip.id.isBlank()) return
        synchronized(this) {
            var chip = Chip(rfidChip.id, rfidChip.attenuation)
            val known = entries[chip.id]

            if (known == null) {
                val existing = db.sensorDao().findSensor(chip.id) != null
                if (existing) chip = chip.copy(alreadyUsed = true)
            } else if (known.alreadyUsed) return

            /* Update the internal map */
            entries[chip.id] = chip

            /* Publish chip in the public list */
            if (known != null) chips.replaceAll { if (it.id == known.id) chip else it }
            else chips.add(chip)
        }
    }

    data class Chip(val id: String, val attenuation: Int, val alreadyUsed: Boolean = false)
}