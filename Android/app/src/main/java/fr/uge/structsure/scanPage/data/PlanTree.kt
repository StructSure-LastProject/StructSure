package fr.uge.structsure.scanPage.data

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
}