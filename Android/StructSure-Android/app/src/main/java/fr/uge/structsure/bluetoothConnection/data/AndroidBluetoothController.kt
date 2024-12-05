package fr.uge.structsure.bluetoothConnection.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.widget.TextView
import com.csl.cs108library4a.Cs108Library4A
import com.csl.cs108library4a.ReaderDevice
import fr.uge.structsure.bluetoothConnection.domain.BluetoothController
import fr.uge.structsure.bluetoothConnection.domain.BluetoothDeviceDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/***
 * Android Bluetooth Controller (implementation of Bluetooth Controller)
 * @param context context
 */
@SuppressLint("MissingPermission")
class AndroidBluetoothController(
    private val context: Context
): BluetoothController {

    /**
     * Bluetooth Manager
     */
    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    /**
     * Bluetooth Adapter
     */
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    /**
     * scanned devices getter
     */
    override val scannedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()


    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    /**
     * paired devices getter
     */
    override val pairedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()

    /**
     * Found device receiver
     */
    private val foundDeviceReceiver = FoundDeviceReceiver { bluetoothDevice ->
        val newDevice = bluetoothDevice.toBluetoothDeviceDomain()
        _scannedDevices.update { devices ->
            if (newDevice in devices) devices else devices + newDevice
        }
    }

    /**
     * Init method to get devices
     */
    init {
        updatePairedDevices()
    }

    override fun startDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)){
            return
        }

        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )

        updatePairedDevices()
        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)){
            return
        }
        bluetoothAdapter
            ?.cancelDiscovery()
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
    }

    override fun connectToDevice(device: BluetoothDeviceDomain) {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }

        val bluetoothDevice = bluetoothManager?.adapter?.getRemoteDevice(device.address)

        bluetoothDevice?.let {
            println(device.name)
            println(device.address)
            // Testing the Cs108 Lib
            val cs108Lib = Cs108Library4A(context,  TextView(context))
            val readerDevice = ReaderDevice(
                device.name,
                device.address,
                true,
                "",
                0,
                0.0,
                0,
            )
            cs108Lib.connect(readerDevice)
            println(cs108Lib.isBleConnected)
        }
    }

    /**
     * Update paired devices list
     */
    private fun updatePairedDevices(){
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)){
            return
        }
        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceDomain() }
            ?.also { devices -> _pairedDevices.update { devices } }
    }

    /**
     * The method checks if the passed parameter permission is granted or not
     * @param permission The permission to test
     * @return boolean Permission granted or not
     */
    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}