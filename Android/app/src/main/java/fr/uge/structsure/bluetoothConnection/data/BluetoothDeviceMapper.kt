package fr.uge.structsure.bluetoothConnection.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import fr.uge.structsure.bluetoothConnection.domain.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name,
        address = address
    )
}