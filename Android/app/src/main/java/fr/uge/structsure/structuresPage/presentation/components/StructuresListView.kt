package fr.uge.structsure.structuresPage.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.uge.structsure.structuresPage.domain.StructureRepository
import fr.uge.structsure.ui.theme.Typography

private fun StateMapper(state: Boolean): StructureStates {
    if (state){
        return StructureStates.AVAILABLE
    }
    return StructureStates.ONLINE
}

@Composable
fun StructuresListView(structureRepository: StructureRepository) {
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

        structureRepository.getAllStructures().forEach {it ->
            Structure(it.name, StateMapper(it.state), structureRepository)
        }
    }
}