package fr.uge.structsure.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import fr.uge.structsure.components.Button
import fr.uge.structsure.settings.domain.checkServerStatus
import org.w3c.dom.Text

@Composable
fun SettingsPage(){
    var url by remember {
        mutableStateOf(TextFieldValue(""))
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Server url : ")
        TextField(value = url, onValueChange = { url = it}, label = {Text(text = "URL")})
        Button(onClick = {
            checkServerStatus()
        }) {
            Text(text = "Check server status")
        }
    }
}