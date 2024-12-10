package fr.uge.structsure.bluetoothConnection.domain

typealias BluetoothDeviceDomain = BluetoothDevice

/**
 * Data class that represents the Bluetooth Device
 */
data class BluetoothDevice(
    val name: String?,
    val address: String
)
