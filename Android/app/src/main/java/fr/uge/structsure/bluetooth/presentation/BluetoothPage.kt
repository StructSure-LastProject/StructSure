package fr.uge.structsure.bluetoothConnection.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.csl.cslibrary4a.ReaderDevice
import fr.uge.structsure.bluetooth.cs108.Connexion

@Composable
fun BluetoothPage(bleConnexion: Connexion) {
    var selectedDevice by remember { mutableStateOf<ReaderDevice?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Available Devices", style = MaterialTheme.typography.headlineMedium)

        // Display the list of scanned devices
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(bleConnexion.readersList) { device ->
                Device(device = device, onClick = {
                    selectedDevice = device // Set the selected device
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to connect to the selected device
        Button(onClick = {
            selectedDevice?.let {
                bleConnexion.onItemClick(selectedDevice);
            }
        }) {
            Text(text = "Connect to Selected Device")
        }
    }
}

@Composable
fun Device(device: ReaderDevice, onClick: () -> Unit) {
    Text(
        text = device.name ?: "Unnamed Device",
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    )
}