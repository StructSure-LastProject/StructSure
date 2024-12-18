package fr.uge.structsure.start_scan.domain

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.database.AppDatabase
import fr.uge.structsure.start_scan.data.PlanEntity
import fr.uge.structsure.start_scan.data.StructureEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StructureViewModel(private val db: AppDatabase) : ViewModel() {

    /**
     * Fonction pour insérer des données d'exemple dans la BDD Room.
     */
    fun insertSampleData() {
        viewModelScope.launch(Dispatchers.IO) {
            val dao = db.structurePlanDao()

            // Insertion d'une structure (ouvrage)
            val structureId = dao.insertStructure(
                StructureEntity(name = "Pont Syllan", note = "Ouvrage principal nord")
            )

            // Liste de plans avec des images dans drawable
            val plans = listOf(
                PlanEntity(structureId = structureId, name = "Plan P1", section = "Nord/P1", imagePath = "drawable/plan_p1"),
                PlanEntity(structureId = structureId, name = "Plan P2", section = "Nord/P2", imagePath = "drawable/plan_p2"),
                PlanEntity(structureId = structureId, name = "Plan P3", section = "Nord/P3", imagePath = "drawable/plan_p3"),
                PlanEntity(structureId = structureId, name = "Plan P4", section = "Sud/P4", imagePath = "drawable/plan_p4"),
                PlanEntity(structureId = structureId, name = "Plan P5", section = "Est/P5", imagePath = "drawable/plan_p5"),
                PlanEntity(structureId = structureId, name = "Plan P6", section = "Ouest/P6", imagePath = "drawable/plan_p6"),
                PlanEntity(structureId = structureId, name = "Plan P7", section = "Nord/P7", imagePath = "drawable/plan_p7"),
                PlanEntity(structureId = structureId, name = "Plan P8", section = "Hauban/P8", imagePath = "drawable/plan_p8")
            )

            dao.insertPlans(plans)

            Log.d("DB_INSERT", "Structure et plans insérés avec succès")
        }
    }


    /**
     * Fonction pour récupérer les plans pour une structure donnée et les loguer.
     */
    fun fetchPlansForStructure(structureId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val dao = db.structurePlanDao()
            val plans = dao.getPlansByStructureId(structureId)
            plans.forEach { plan ->
                Log.d("PLAN_INFO", "Nom: ${plan.name}, Section: ${plan.section}, Image: ${plan.imagePath}")
            }
        }
    }


    fun fetchPlansAndTestImages(structureId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val dao = db.structurePlanDao()
            val plans = dao.getPlansByStructureId(structureId)

            plans.forEach { plan ->
                Log.d("TEST_IMAGES", "Nom: ${plan.name}, Section: ${plan.section}, Image Path: ${plan.imagePath}")
            }
        }
    }

}
