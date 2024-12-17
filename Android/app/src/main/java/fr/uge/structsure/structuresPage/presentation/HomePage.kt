package fr.uge.structsure.structuresPage.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.uge.structsure.components.Header
import fr.uge.structsure.structuresPage.domain.StructureRepository
import fr.uge.structsure.structuresPage.presentation.components.AccountInformationsView
import fr.uge.structsure.structuresPage.presentation.components.StructuresListView


@Composable
fun HomePage(navController: NavHostController, structureRepository: StructureRepository) {
    Column(
        modifier = Modifier
            .padding(start = 25.dp, top = 50.dp, end = 25.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(35.dp)
    ) {
        Header()
        AccountInformationsView()
        StructuresListView(structureRepository)
        // BluetoothButton()
    }

}