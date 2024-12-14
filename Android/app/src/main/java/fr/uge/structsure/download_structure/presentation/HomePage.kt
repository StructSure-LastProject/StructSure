package fr.uge.structsure.download_structure.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.uge.structsure.components.Header
import fr.uge.structsure.download_structure.presentation.accountframe.AccountInformationsView

@Composable
fun HomePage(){
    Column(modifier=Modifier.padding(start=25.dp, top=50.dp, end = 25.dp),
        verticalArrangement = Arrangement.spacedBy(35.dp)){
        Header()
        AccountInformationsView()
        StructuresListView()
        BluetoothButton()
    }

}