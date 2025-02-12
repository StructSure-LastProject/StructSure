package fr.uge.structsure.startScan.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uge.structsure.R
import fr.uge.structsure.components.Plan
import fr.uge.structsure.components.Point
import fr.uge.structsure.startScan.presentation.components.SensorState
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.Typography
import fr.uge.structsure.ui.theme.White
import fr.uge.structsure.ui.theme.fonts


/**
 * This composable is used to display the plans of the structure.
 */
@Composable
fun PlansView(modifier: Modifier = Modifier) {
    val selected = remember { mutableStateOf("Section OA/Plan 01") }

    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            style = Typography.titleLarge,
            text = "Plans",
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = White, shape = RoundedCornerShape(size = 20.dp))
                .padding(horizontal = 20.dp, vertical = 15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start
        )  {
            val points = remember { mutableStateListOf(
                Point(0, 0, SensorState.OK),
                Point(100, 100, SensorState.OK)
            ) }
            Plan(R.drawable.oa_plan, points)

            Spacer( Modifier.fillMaxWidth().height(1.dp).background(LightGray) )

            PlanSelector(selected)
        }
    }
}

/**
 * Menu enabling to chose a plan among the existing ones.
 */
@Composable
private fun PlanSelector(selected: MutableState<String>) {

    // TODO Use real data here
    // LazyColumn(
    //     verticalArrangement = Arrangement.spacedBy(8.dp),
    //     modifier = Modifier
    //         .fillMaxWidth()
    //         .height(200.dp)
    // )
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Section("Section OA") {
            Section("Sous-section") {
                PlanItem("Plan 01", selected.value == "Section OA/Sous-section/Plan 01" ) {
                    selected.value = "Section OA/Sous-section/Plan 01"
                }
                PlanItem("Plan 02", selected.value == "Section OA/Sous-section/Plan 02" ) {
                    selected.value = "Section OA/Sous-section/Plan 02"
                }
                PlanItem("Plan 03", selected.value == "Section OA/Sous-section/Plan 03" ) {
                    selected.value = "Section OA/Sous-section/Plan 03"
                }
            }
            PlanItem("Plan 01", selected.value == "Section OA/Plan 01" ) {
                selected.value = "Section OA/Plan 01"
            }
            PlanItem("Plan 02", selected.value == "Section OA/Plan 02" ) {
                selected.value = "Section OA/Plan 02"
            }
            PlanItem("Plan 03", selected.value == "Section OA/Plan 03" ) {
                selected.value = "Section OA/Plan 03"
            }
        }
        Section("Section OB") {
            PlanItem("Plan 05", selected.value == "Section OB/Plan 05" ) {
                selected.value = "Section OB/Plan 05"
            }
            PlanItem("Plan 06", selected.value == "Section OB/Plan 06" ) {
                selected.value = "Section OB/Plan 06"
            }
            PlanItem("Plan 07", selected.value == "Section OB/Plan 07" ) {
                selected.value = "Section OB/Plan 07"
            }
        }
    }
}

/**
 * Item corresponding to a plan section (a group of plans) in the
 * plan selector
 * @param name the name of the section
 * @param children content of this section
 */
@Composable
private fun Section(name: String, children:  @Composable (ColumnScope.() -> Unit)) {
    var collapsed by remember { mutableStateOf(true) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { collapsed = !collapsed }
            .background(color = LightGray)
            .padding(horizontal = 9.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.chevron_down),
            contentDescription = "Chevron",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(16.dp).rotate(if (collapsed) -90f else 0f)
        )
        Text(
            name,
            Modifier.weight(1f),
            style = Typography.headlineMedium
        )
    }

    if (!collapsed)  {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(start = 17.dp),
            content = children
        )
    }
}

/**
 * Item corresponding to a plan in the plan selector
 * @param name the name of the plan
 * @param selected whether or not this item is the one selected among
 *     all the existing items in the selector
 * @param onClick action to run when this item is clicked
 */
@Composable
private fun PlanItem(name: String, selected: Boolean, onClick: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .alpha(if (selected) 1f else 0.75f)
            .background(if (selected) LightGray else White)
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "•",
            Modifier.width(16.dp),
            style = Typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Text(
            name,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = fonts,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = Black
            )
        )
    }
}