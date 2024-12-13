package fr.uge.structsure.start_scan.presentation.components.plans

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.start_scan.presentation.components.plans.Plan
import fr.uge.structsure.ui.theme.Typography

@Composable
fun Section(
    modifier: Modifier = Modifier,
    datas: SectionData
) {
    var isExpanded by remember { mutableStateOf(true) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .background(color = Variables.LightGray, shape = RoundedCornerShape(10.dp))
        ) {
            Image(
                painter = painterResource(id = if (isExpanded) R.drawable.chevron_down else R.drawable.chevron_down),
                contentDescription = "Chevron",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = datas.name,
                style = Typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }

        if (isExpanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 16.dp)
            ) {
                // Affichage des plans
                datas.plans.forEach { planData ->
                    Plan(planData = planData)
                }

                // Affichage des sous-sections
                datas.subSections.forEach { subSectionData ->
                    Section(datas = subSectionData)
                }
            }
        }
    }
}

data class SectionData(
    val plans: MutableList<PlanData>,
    val subSections: MutableList<SectionData>,
    val name: String = "Sections"
)
