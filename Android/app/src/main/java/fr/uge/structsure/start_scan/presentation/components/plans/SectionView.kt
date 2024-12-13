package fr.uge.structsure.start_scan.presentation.components.plans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier

@Composable
fun SectionView(
    modifier: Modifier = Modifier,
    sections: List<SectionData>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(16.dp)
    ) {
        // Affichage de chaque section racine
        sections.forEach { sectionData ->
            Section(datas = sectionData)
        }
    }
}