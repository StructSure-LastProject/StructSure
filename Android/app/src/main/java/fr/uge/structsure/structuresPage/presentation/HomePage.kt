package fr.uge.structsure.structuresPage.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FabPosition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(35.dp)
        ) {
            AccountInformationsView(dao, navController)
            StructuresListView(structureViewModel, navController)
            ConnectivityStatus()
        }
    }

}