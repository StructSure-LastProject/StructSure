package fr.uge.structsure.download_structure.presentation.structures

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val STRUCTURE_LIST_TEMPORARY = listOf (
    StructureDate("Viaduc de Sylans", StructureDownloadState.downloaded),
    StructureDate("Viaduc de la Côtière", StructureDownloadState.synchronizing),
    StructureDate("Pont d'Ain", StructureDownloadState.downloadaing),
    StructureDate("Pont des Nautes", StructureDownloadState.downloadable),
    StructureDate("Viaduc de Nantua", StructureDownloadState.downloadable),
)

@Preview(showBackground = true)
@Composable
fun StructuresListView() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        items(STRUCTURE_LIST_TEMPORARY) { structureData ->
            Structure(data = structureData)
        }
    }
}