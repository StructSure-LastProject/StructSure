package fr.uge.structsure.structuresPage.data

/**
 * Data class representing the details of a structure.
 * @property id The id of the structure.
 * @property name The name of the structure.
 * @property note The note of the structure.
 * @property plans The list of plans of the structure.
 * @property sensors The list of sensors of the structure.
 */
data class StructureDetailsData(
    val id: Long = 0L,
    val name: String,
    val note: String,
    val plans: List<Plan>,
    val sensors: List<Sensor>
)
