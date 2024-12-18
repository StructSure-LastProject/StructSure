package fr.uge.structsure.start_scan.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import fr.uge.structsure.start_scan.data.PlanEntity

@Composable
fun DisplayPlanImages(plans: List<PlanEntity>) {
    Column(modifier = Modifier.padding(16.dp)) {
        plans.forEach { plan ->
            Image(
                painter = painterResource(id = getDrawableId(plan.imagePath)),
                contentDescription = plan.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(8.dp)
            )
        }
    }
}

// Fonction pour obtenir l'ID de drawable à partir du nom
fun getDrawableId(imagePath: String): Int {
    val resourceName = imagePath.substringAfterLast("/")
    return when (resourceName) {
        "plan_p1" -> fr.uge.structsure.R.drawable.plan_p1
        "plan_p2" -> fr.uge.structsure.R.drawable.plan_p2
        "plan_p3" -> fr.uge.structsure.R.drawable.plan_p3
        "plan_p4" -> fr.uge.structsure.R.drawable.plan_p4
        "plan_p5" -> fr.uge.structsure.R.drawable.plan_p5
        "plan_p6" -> fr.uge.structsure.R.drawable.plan_p6
        "plan_p7" -> fr.uge.structsure.R.drawable.plan_p7
        "plan_p8" -> fr.uge.structsure.R.drawable.plan_p8
        else -> fr.uge.structsure.R.drawable.oa_plan
    }
}
