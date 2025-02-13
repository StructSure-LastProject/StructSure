package fr.uge.structsure.structuresPage.data


data class StructureDetailsData(
    val id: Long = 0L,
    val name: String,
    val note: String,
    val plans: List<Plan>,
    val sensors: List<Sensor>
)
