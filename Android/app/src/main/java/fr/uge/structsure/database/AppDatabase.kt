package fr.uge.structsure.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.uge.structsure.connexionPage.data.AccountDao
import fr.uge.structsure.connexionPage.data.AccountEntity
import fr.uge.structsure.startScan.data.ScanEntity
import fr.uge.structsure.startScan.data.StructureEntity
import fr.uge.structsure.startScan.data.dao.ScanDao
import fr.uge.structsure.startScan.data.dao.StructurePlanDao
import fr.uge.structsure.structuresPage.data.PlanDB
import fr.uge.structsure.structuresPage.data.PlanDao
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.structuresPage.data.SensorDao
import fr.uge.structsure.structuresPage.data.StructureDao
import fr.uge.structsure.structuresPage.data.StructureData

/**
 * BDD Room for StructSure app.
 * - UserData: User data for authentication.
 * - ScanEntity: Scan data.
 * - SensorEntity: Sensor data.
 * - StructureEntity: Structure data.
 * - PlanEntity: Plan data.
 */
@Database(
    entities = [ScanEntity::class, StructureEntity::class, AccountEntity::class,
        StructureData::class, SensorDB::class, PlanDB::class],
    version = 4,
    exportSchema = false
)
/**
 * AppDatabase class for Room database.
 */
abstract class AppDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // for production use a proper migration strategy (see https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929)
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE sensors ADD COLUMN note TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "STRUCTSURE_DB"
                )
                    .fallbackToDestructiveMigration() // Option when the database version is changed.
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * DAOs for the database.
     */
    abstract fun accountDao(): AccountDao
    abstract fun scanDao(): ScanDao
    abstract fun structureDao(): StructureDao
    abstract fun structurePlanDao(): StructurePlanDao
    abstract fun planDao(): PlanDao
    abstract fun sensorDao(): SensorDao
}
