package fr.uge.structsure.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import fr.uge.structsure.components.Button
import fr.uge.structsure.settings.domain.checkServerStatus
import org.w3c.dom.Text

@Composable
fun SettingsPage(){


    Column(
        verticalArrangement = Arrangement.Center
    ){
        Text(text = "Server url : ")
        Button(onClick = {
            checkServerStatus()
        }) {
            Text(text = "Check server status")
        }
    }
}