package fr.uge.structsure.scanPage.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

/**
 * Data Access Object for ScanEdits entity
 */
@Dao
interface ScanEditsDao {
    @Upsert
    fun upsert(modification: ScanEdits)

    @Query("SELECT * FROM scan_edits WHERE scanId = :scanId")
    fun getAllByScanId(scanId: Long): List<ScanEdits>
}