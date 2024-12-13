package fr.uge.structsure.start_scan.presentation.components.plans

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uge.structsure.R
import fr.uge.structsure.ui.theme.Typography

data class PlanData(
    val name: String,
    val sectionPath: String // Exemple : "section1/section1.1/"
)

@Composable
fun Plan(
    modifier: Modifier = Modifier,
    planData: PlanData
) {
    // val planImage = painterResource(id = planData.file) // Charger le Painter à partir de l'ID de ressource

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        // Image(painter = planImage, contentDescription = planData.name, modifier = Modifier.size(64.dp))
        Text(text = planData.name, style = Typography.bodyMedium)
    }
}

fun organizePlans(plans: List<PlanData>): SectionData {
    val root = SectionData(mutableListOf(), mutableListOf())

    plans.forEach { plan ->
        val pathParts = plan.sectionPath.split("/").filter { it.isNotEmpty() }
        var currentSection = root

        for (part in pathParts) {
            val existingSection = currentSection.subSections.find { it.name == part }
            if (existingSection != null) {
                currentSection = existingSection
            } else {
                val newSection = SectionData(mutableListOf(), mutableListOf(), part)
                currentSection.subSections.add(newSection)
                currentSection = newSection
            }
        }

        currentSection.plans.add(plan)
    }

    return root
}