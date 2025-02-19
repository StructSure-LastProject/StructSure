package fr.uge.structsure.homePage.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.components.BluetoothButton
import fr.uge.structsure.components.Page
import fr.uge.structsure.connexionPage.data.AccountDao
import fr.uge.structsure.homePage.domain.StructureViewModel
import fr.uge.structsure.homePage.presentation.components.AccountInformationsView
import fr.uge.structsure.homePage.presentation.components.StructuresListView


@Composable
fun HomePage(
    connexionCS108: Cs108Connector,
    navController: NavHostController,
    dao: AccountDao,
    structureViewModel: StructureViewModel
) {
    Page (
        Modifier.padding(bottom = 75.dp).fillMaxSize(),
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