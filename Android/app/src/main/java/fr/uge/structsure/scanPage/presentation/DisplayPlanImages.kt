package fr.uge.structsure.scanPage.presentation

/*
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

 */

// Function to get the drawable id from the image path
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
