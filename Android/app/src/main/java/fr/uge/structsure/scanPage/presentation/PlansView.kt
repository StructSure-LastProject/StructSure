package fr.uge.structsure.scanPage.presentation

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uge.structsure.MainActivity
import fr.uge.structsure.R
import fr.uge.structsure.components.Plan
import fr.uge.structsure.components.Point
import fr.uge.structsure.scanPage.presentation.components.SensorState
import fr.uge.structsure.structuresPage.data.PlanDB
import fr.uge.structsure.structuresPage.data.TreeNode
import fr.uge.structsure.structuresPage.data.TreePlan
import fr.uge.structsure.structuresPage.data.TreeSection
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.Typography
import fr.uge.structsure.ui.theme.White
import fr.uge.structsure.ui.theme.fonts
import fr.uge.structsure.utils.FileUtils

/**
 * This composable is used to display the plans of the structure.
 * @param structureId the id of the structure
 */
@Composable
fun PlansView(structureId: Long) {
    val context = LocalContext.current
    val selected = remember { mutableStateOf<TreePlan?>(null) }
    val defaultImage = remember(1) { BitmapFactory.decodeResource(context.resources, R.drawable.plan_not_found) }

    val plans = remember(structureId, selected) { planTree(MainActivity.db.planDao().getPlansByStructureId(structureId), selected) }
    val points = remember(selected) { MainActivity.db.sensorDao().getAllSensorsByPlan(selected.value?.plan?.id?:0).map { Point(it.x, it.y, SensorState.UNKNOWN) }.toMutableList() }
    // TODO get real point states

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
            val path = selected.value?.plan?.let { FileUtils.getLocalPlanImage(context, it.id) }
            val image = if (path == null) defaultImage else BitmapFactory.decodeFile(path)
            Plan(
                image = image,
                points = points
            )

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(LightGray) )

            Section(plans as TreeSection, selected, true)
        }
    }
}

/**
 * Item corresponding to a plan section (a group of plans) in the
 * plan selector
 * @param treeNode the tree (or subtree) to display
 * @param selected boolean used to mark visually selected element
 * @param hideSelf true to display content only, without the section name
 */
@Composable
private fun Section(treeNode: TreeSection, selected: MutableState<TreePlan?>, hideSelf: Boolean = false) {
    var collapsed by remember { mutableStateOf(true) }
    if (!hideSelf) {
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
                modifier = Modifier
                    .size(16.dp)
                    .rotate(if (collapsed) -90f else 0f)
            )
            Text(
                treeNode.name,
                Modifier.weight(1f),
                style = Typography.headlineMedium
            )
        }
    }

    if (!collapsed || hideSelf)  {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = if (hideSelf) 0.dp else 17.dp)
        ) {
            treeNode.children.values.forEach {
                if (it.isPlan) {
                    val plan = (it as TreePlan).plan
                    PlanItem(plan.name, selected.value == it ) { selected.value = it }
                } else {
                    Section(it as TreeSection, selected)
                }
            }
        }
    }
}

/**
 * Creates a tree of plans and sections from a list of raw plans from
 * the database.
 * @param plans the raw plans from the database
 * @param selected to select the first plan by default
 * @return the tree containing all item well organized
 */
private fun planTree(plans: List<PlanDB>, selected: MutableState<TreePlan?>): TreeNode {
    val root = TreeSection("")
    plans.forEach { plan ->
        val tokens = if (plan.section.isEmpty()) listOf() else plan.section.split("/")
        var node: TreeNode = root
        tokens.forEach { token -> node = node.children.computeIfAbsent(token) { TreeSection(token) } }
        val child = TreePlan(plan)
        node.children["${plan.id}"] = child
        if (selected.value == null) selected.value = child
    }
    return root
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