package fr.uge.structsure.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.uge.structsure.structuresPage.data.PlanDB
import fr.uge.structsure.structuresPage.data.PlanDao
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.structuresPage.data.SensorDao
import fr.uge.structsure.structuresPage.data.StructureDao
import fr.uge.structsure.structuresPage.data.StructureData
import fr.uge.structsure.structuresPage.data.StructureDetailsData

@Database(
    entities = [StructureData::class, SensorDB::class, PlanDB::class],
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
                    .fallbackToDestructiveMigration()// Option qu'on peut utiliser pour éviter des erreurs de migration pendant le développement.
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    abstract fun structureDao(): StructureDao
    abstract fun planDao(): PlanDao
    abstract fun sensorDao(): SensorDao
}