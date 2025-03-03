package fr.uge.structsure.structuresPage.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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

@Composable
fun StructuresListView(
    structureViewModel: StructureViewModel,
    navController: NavController
) {
    val structures = structureViewModel.getAllStructures.observeAsState()

    LaunchedEffect(structureViewModel) {
        structureViewModel.getAllStructures()
    }

    val searchByName = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            style = Typography.titleLarge,
            text = "Ouvrages",
        )
        SearchBar(input = searchByName)
        structures.value?.filter { it.name.contains(searchByName.value, true) }?.forEach {
            Structure(it, structureViewModel, navController)
        }
    }
}