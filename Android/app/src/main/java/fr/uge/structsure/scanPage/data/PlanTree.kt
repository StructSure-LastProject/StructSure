package fr.uge.structsure.scanPage.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.uge.structsure.structuresPage.data.PlanDB

/**
 * Representation for a tree of section containing either other
 * sections and/or plans.
 * @property children the children of this node (can be empty)
 * @property isPlan if this node is a plan, false for a section
 */
interface TreeNode {
    val children: MutableMap<String, TreeNode>
    val isPlan: Boolean
}

/**
 * Representation of a plan in the tree (leave)
 * @property plan the plan object represent by this node
 */
data class TreePlan (
    val plan: PlanDB,
) : TreeNode {
    override val isPlan = true
    override val children = mutableMapOf<String, TreeNode>()
}

/**
 * Representation of a section in the tree (node)
 * @property String the name of this section
 */
data class TreeSection(val name: String) : TreeNode {
    override val isPlan = false
    override val children = mutableMapOf<String, TreeNode>()
    val collapsed: MutableState<Boolean> = mutableStateOf(true)
}

/**
 * Find a plan in the tree by its ID
 * @param planId the ID of the plan to find
 * @return the plan node if found, null otherwise
 */
fun TreeSection.findPlanById(planId: Long): TreeNode? {
    children.values.forEach { node ->
        if (node.isPlan) {
            val treePlan = node as TreePlan
            if (treePlan.plan.id == planId) {
                return node
            }
        } else {
            val result = (node as TreeSection).findPlanById(planId)
            if (result != null) {
                return result
            }
        }
    }
    return null
}

/**
 * Get the name of the section of a node
 * @param node the node to get the section name from
 * @return the name of the section
 */
fun getPlanSectionName(node: TreeNode): String {
    return when {
        node is TreePlan -> node.plan.section
        node is TreeSection -> node.name
        else -> "Section inconnue"
    }
}