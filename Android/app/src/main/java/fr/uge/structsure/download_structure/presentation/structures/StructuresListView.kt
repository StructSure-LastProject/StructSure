package fr.uge.structsure.download_structure.presentation.structures

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.uge.structsure.download_structure.domain.StructureData
import fr.uge.structsure.download_structure.domain.StructureDownloadState
import fr.uge.structsure.ui.theme.Typography

val STRUCTURE_LIST_TEMPORARY = listOf(
    StructureData("Viaduc de Sylans", StructureDownloadState.downloaded),
    StructureData("Viaduc de la Côtière", StructureDownloadState.synchronizing),
    StructureData("Pont d'Ain", StructureDownloadState.downloading),
    StructureData("Pont des Nautes", StructureDownloadState.downloadable),
    StructureData("Viaduc de Nantua", StructureDownloadState.downloadable),
    StructureData("Viaduc de Sylans bis", StructureDownloadState.downloaded),
    StructureData("Viaduc de la Côtière bis", StructureDownloadState.synchronizing),
    StructureData("Pont d'Ain bis", StructureDownloadState.downloading),
    StructureData("Pont des Nautes bis", StructureDownloadState.downloadable),
    StructureData("Viaduc de Nantua bis", StructureDownloadState.downloadable),
)

@Composable
fun StructuresListView() {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            style = Typography.titleLarge,
            text = "Ouvrages",
        )
        SearchBar()
        STRUCTURE_LIST_TEMPORARY.forEach { structureData ->
            Structure(

)
        }
    }
}