package fr.uge.structsure.structuresPage.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.components.BluetoothButton
import fr.uge.structsure.components.Page
import fr.uge.structsure.connexionPage.data.AccountDao
import fr.uge.structsure.structuresPage.domain.StructureViewModel
import fr.uge.structsure.structuresPage.presentation.components.AccountInformationsView
import fr.uge.structsure.structuresPage.presentation.components.StructureStates
import fr.uge.structsure.structuresPage.presentation.components.StructuresListView
import kotlinx.coroutines.launch


@Composable
fun HomePage(
    connexionCS108: Cs108Connector,
    navController: NavHostController,
    dao: AccountDao,
    structureViewModel: StructureViewModel
) {
    val structures = structureViewModel.getAllStructures.observeAsState()
    val hasUnsentResults = structureViewModel.hasUnsentResults.observeAsState()
    val isConnected = structureViewModel.connectivityViewModel.isConnected.observeAsState()

    LaunchedEffect(Unit) {
        structureViewModel.getAllStructures()
    }

    LaunchedEffect(isConnected.value) {
        structures.value?.find { it.downloaded }?.let { structure ->
            when {
                isConnected.value == false && hasUnsentResults.value == true -> {
                    structureViewModel.setStructureState(structure.id, StructureStates.DOWNLOADING)
                }
                isConnected.value == true && hasUnsentResults.value == true -> {
                    structureViewModel.setStructureState(structure.id, StructureStates.ONLINE)
                }
            }
        }
    }


    Page (
        Modifier.padding(bottom = 75.dp),
        navController = navController,
        bottomBar = {
            Box (Modifier.systemBarsPadding().padding(horizontal = 20.dp, vertical = 15.dp)) {
                BluetoothButton(connexionCS108)
            }
        }
    ) {
        AccountInformationsView(dao, navController)
        StructuresListView(structureViewModel, navController)
        ConnectivityStatus()
    }

}