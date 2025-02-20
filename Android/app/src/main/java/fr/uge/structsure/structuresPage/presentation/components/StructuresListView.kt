package fr.uge.structsure.structuresPage.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.structuresPage.domain.StructureViewModel
import fr.uge.structsure.ui.theme.Typography

private fun stateMapper(downloaded: Boolean): StructureStates {
    return if (downloaded) StructureStates.AVAILABLE else StructureStates.ONLINE
}

@Composable
fun StructuresListView(
    structureViewModel: StructureViewModel,
    navController: NavController
) {
    val structures = structureViewModel.getAllStructures.observeAsState()
    val structureStates = structureViewModel.structureStates.observeAsState(initial = mutableMapOf())
    val searchByName = remember { mutableStateOf("") }

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            style = Typography.titleLarge,
            text = "Ouvrages",
        )
        SearchBar(input = searchByName)

        structures.value?.filter { it.name.contains(searchByName.value) }?.forEach { structure ->
            val state = remember(structure.id, structureStates.value[structure.id]) {
                mutableStateOf(
                    structureStates.value[structure.id] ?: stateMapper(structure.downloaded)
                )
            }

            LaunchedEffect(structureStates.value[structure.id]) {
                structureStates.value[structure.id]?.let {
                    state.value = it
                }
            }

            Structure(structure, state, structureViewModel, navController)
        }
    }
}