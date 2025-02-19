package fr.uge.structsure.structuresPage.data

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

class TreePlan (
    val plan: PlanDB,
) : TreeNode {
    override val isPlan = true
    override val children = mutableMapOf<String, TreeNode>()
}

class TreeSection(val name: String) : TreeNode {
    override val isPlan = false
    override val children = mutableMapOf<String, TreeNode>()
}