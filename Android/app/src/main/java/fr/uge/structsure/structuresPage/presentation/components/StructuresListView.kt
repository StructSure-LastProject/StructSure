package fr.uge.structsure.structuresPage.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.structuresPage.domain.StructureViewModel
import fr.uge.structsure.ui.theme.Typography

private fun StateMapper(state: Boolean): StructureStates {
    if (state){
        return StructureStates.AVAILABLE
    }
    return StructureStates.ONLINE
}

@Composable
fun StructuresListView(structureViewModel: StructureViewModel, navController: NavController) {
    val structures = structureViewModel.getAllStructures.observeAsState()
    structureViewModel.getAllStructures()

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
        structures.value?.filter { it.name.contains(searchByName.value) }?.forEach {
            val state = mutableStateOf(StateMapper(it.state))
            Structure(it, state, structureViewModel, navController)
        }
    }
}