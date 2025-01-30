package fr.uge.structsure.structuresPage.presentation

import androidx.compose.material3.FabPosition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.components.BluetoothButton
import fr.uge.structsure.components.Page
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
    Page (
        floatingActionButton = {
            BluetoothButton(connexionCS108)
        },
        floatingActionButtonPosition = FabPosition.Start
    ) {
        AccountInformationsView(dao, navController)
        StructuresListView(structureViewModel, navController)
        ConnectivityStatus()
    }

}