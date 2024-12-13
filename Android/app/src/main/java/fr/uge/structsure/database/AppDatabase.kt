package fr.uge.structsure.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.uge.structsure.dbTest.data.UserData
import fr.uge.structsure.dbTest.data.UserDao
import fr.uge.structsure.start_scan.data.ScanEntity
import fr.uge.structsure.start_scan.data.SensorEntity
import fr.uge.structsure.start_scan.data.dao.ScanDao

// Add entities to the database
@Database(
    entities = [UserData::class, ScanEntity::class, SensorEntity::class],
    version = 2,
    exportSchema = false // Désactive l'exportation des schémas c'est qu'un prototype
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "STRUCTSURE_DB"
                )
                    .fallbackToDestructiveMigration() // Option qu'on peut utiliser pour éviter des erreurs de migration pendant le développement.
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    abstract fun userDao(): UserDao
    abstract fun scanDao(): ScanDao
}
