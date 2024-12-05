package fr.uge.structsure.bluetoothConnection.presentation

import fr.uge.structsure.bluetoothConnection.domain.BluetoothDevice


data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
)
