package fr.uge.structsure.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.uge.structsure.dbTest.data.UserData
import fr.uge.structsure.dbTest.data.UserDao
import fr.uge.structsure.start_scan.data.PlanEntity
import fr.uge.structsure.start_scan.data.ScanEntity
import fr.uge.structsure.start_scan.data.SensorEntity
import fr.uge.structsure.start_scan.data.StructureEntity
import fr.uge.structsure.start_scan.data.dao.ScanDao
import fr.uge.structsure.start_scan.data.dao.StructurePlanDao

// Add entities to the database
@Database(
    entities = [UserData::class, ScanEntity::class, SensorEntity::class, StructureEntity::class, PlanEntity::class],
    version = 2,
    exportSchema = false // Désactive l'exportation des schémas c'est qu'un prototype
)
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
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * DAOs for the database.
     */
    abstract fun userDao(): UserDao
    abstract fun scanDao(): ScanDao
    abstract fun structureDao(): StructureDao
    abstract fun structurePlanDao(): StructurePlanDao
}
