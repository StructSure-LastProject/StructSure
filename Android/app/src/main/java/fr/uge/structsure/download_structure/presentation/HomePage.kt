package fr.uge.structsure.download_structure.presentation

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.uge.structsure.components.Header
import fr.uge.structsure.download_structure.presentation.accountframe.AccountInformationsView
import fr.uge.structsure.download_structure.presentation.structures.StructuresListView

@Composable
fun HomePage() {
    Column(
        modifier = Modifier
            .padding(start = 25.dp, top = 50.dp, end = 25.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(35.dp)
    ) {
        Header()
        AccountInformationsView()
        StructuresListView()
        // BluetoothButton()
    }

}