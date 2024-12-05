package fr.uge.structsure.bluetoothConnection.domain

import kotlinx.coroutines.flow.StateFlow

/**
 * Bluetooth Controller Interface
 * */
interface BluetoothController {
    /**
     * scanned Devices as a mutable state flow list
     */
    val scannedDevices: StateFlow<List<BluetoothDeviceDomain>>
    /**
     * paired Devices as a mutable state flow list
     */
    val pairedDevices: StateFlow<List<BluetoothDeviceDomain>>

    /**
     * Device discovery start method
     */
    fun startDiscovery()

    /**
     * Device discovery stop method
     */
    fun stopDiscovery()

    /**
     * Unregister the device receiver
     */
    fun release()

    /**
     * Device connection method
     * @param device BluetoothDeviceDomain
     */
    fun connectToDevice(device: BluetoothDeviceDomain)

}