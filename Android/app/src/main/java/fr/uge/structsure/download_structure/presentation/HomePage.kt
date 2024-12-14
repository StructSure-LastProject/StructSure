package fr.uge.structsure.download_structure.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@Composable
fun HomePage(){
    Column(){
        HeaderLogo()
        AccountInformationsView()
        StructuresListView()
        BluetoothButton()
    }

}