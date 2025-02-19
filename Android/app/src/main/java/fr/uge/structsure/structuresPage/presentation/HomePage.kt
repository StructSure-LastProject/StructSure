package fr.uge.structsure.structuresPage.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.components.BluetoothButton
import fr.uge.structsure.components.Page
import fr.uge.structsure.components.PullToRefresh
import fr.uge.structsure.connexionPage.data.AccountDao
import fr.uge.structsure.structuresPage.domain.StructureViewModel
import fr.uge.structsure.structuresPage.presentation.components.AccountInformationsView
import fr.uge.structsure.structuresPage.presentation.components.StructuresListView


@Composable
fun HomePage(
    connexionCS108: Cs108Connector,
    navController: NavHostController,
    dao: AccountDao,
    structureViewModel: StructureViewModel
) {
    LaunchedEffect(Unit) {
        structureViewModel.getAllStructures()
    }


    Page (
        Modifier.fillMaxSize(),
        navController = navController,
        scrollable = false,
        bottomBar = {
            Box (Modifier.systemBarsPadding().padding(horizontal = 20.dp, vertical = 15.dp)) {
                BluetoothButton(connexionCS108, true)
            }
        }
    ) {
        val isRefreshing = structureViewModel.isRefreshing.observeAsState()
        PullToRefresh(
            Modifier.padding(bottom = 75.dp),
            isRefreshing.value == true,
            { structureViewModel.getAllStructures() }
        ) {
            AccountInformationsView(dao, navController)
            StructuresListView(structureViewModel, navController)
            ConnectivityStatus()
        }
    }

}