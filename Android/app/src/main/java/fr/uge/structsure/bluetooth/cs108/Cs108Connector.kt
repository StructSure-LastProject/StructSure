package fr.uge.structsure.bluetooth.cs108

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.csl.cslibrary4a.ReaderDevice
import fr.uge.structsure.MainActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CancellationException

class Cs108Connector(private val context: Context, private var devices: MutableList<ReaderDevice>) {
    companion object {
        private var scanTask: Job? = null
        private var pairTask: Job? = null
        var device: ReaderDevice? = null
            private set
        var isReady: Boolean = false
            private set
    }

    /** Hook used just after device bluetooth connection success */
    private var onBleConnected: (success: Boolean) -> Unit = {}

    /** Hook used just after device initialization completed */
    private var onDeviceReady: () -> Unit = {}

    /**
     * Start scanning for any nearby compatible Bluetooth Low Energy
     * devices and add them in the devices list.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun start() {
        if (scanTask != null) return
        MainActivity.csLibrary4A.scanLeDevice(true)
        scanTask = GlobalScope.launch { pollDevices() }
        println("[TinyRfid] Scan started")
    }

    /**
     * Interrupt the device scan (if started).
     */
    fun stop() {
        if (scanTask == null) return
        MainActivity.csLibrary4A.scanLeDevice(false)
        runBlocking {
            scanTask!!.cancelAndJoin()
        }
    }

    /**
     * Tries to connect to the given device. During the connection,
     * the bluetooth callback and the onReady callback will be called.
     *
     * Technically, it asks continuously the cs108 library for any freshly
     * read device and adds it to the devices list if not added yet
     * @param device the device to connect to
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun connect(device: ReaderDevice) {
        pairTask = GlobalScope.launch {
            var bleConnected: Boolean? = null
            try {
                MainActivity.csLibrary4A.connect(device)
                bleConnected = waitForBleConnection()
                if (!bleConnected) {
                    onBleConnected(false)
                    return@launch
                }
                Cs108Connector.device = device
                onBleConnected(true)
                waitForDeviceReady()
                onDeviceReady()
                isReady = true
            } catch (e: CancellationException) {
                // Interrupted
            }
            if (bleConnected == null) onBleConnected(false)
            println("[DeviceConnector] Connection stopped")
            pairTask = null
        }
    }

    /**
     * Disconnects from the currently connected device (if existing)
     */
    fun disconnect() {
        if (device != null) {
            MainActivity.csLibrary4A.disconnect(false)
            device = null
            isReady = false
        }
    }

    /**
     * Wait for the latest bluetooth pairing request to be completed.
     * @return true in case of success, false after timeout
     */
    private suspend fun waitForBleConnection(): Boolean {
        var retry = 30
        while (pairTask!!.isActive && retry-- > 0) {
            if (MainActivity.csLibrary4A.isBleConnected) return true
            delay(500L)
        }
        return false
    }

    /**
     * Wait for the latest device to be ready for other operations.
     */
    private suspend fun waitForDeviceReady() {
        while (pairTask!!.isActive && MainActivity.csLibrary4A.mrfidToWriteSize() != 0) {
            delay(500L)
        }
    }

    /**
     * Ask continuously the cs108 library for any freshly read device
     * and adds it to the devices list if not added yet
     */
    private suspend fun pollDevices() {
        try {
            while (scanTask!!.isActive) {
                val data = MainActivity.csLibrary4A.newDeviceScanned
                if (data != null && checkPermission()) {
                    if (data.device.type == BluetoothDevice.DEVICE_TYPE_LE && !isKnownDevice(data.device)) {
                        println("[DeviceConnector] New device: name=" + data.device.name + ", address=" + data.device.address)
                        val strInfo = if (data.device.bondState == 12) "BOND_BONDED\n" else ""
                        val readerDevice = ReaderDevice(
                            data.device.name,
                            data.device.address,
                            false,
                            strInfo + "scanRecord=" + MainActivity.csLibrary4A.byteArrayToString(data.scanRecord),
                            1,
                            data.rssi.toDouble(),
                            data.serviceUUID2p2
                        )
                        devices.add(readerDevice)
                    }
                } else {
                    delay(100L)
                }
            }
        } catch (e: CancellationException) {
            // Interrupted
        }
        println("[DeviceConnector] Scan stopped")
        scanTask = null
    }

    /**
     * Checks whether or not the bluetooth authorization is granted or
     * not.
     * @return true if enabled, false otherwise
     */
    private fun checkPermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_CONNECT
            else Manifest.permission.BLUETOOTH
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if a device this the same address of the given one has
     * already be scanned or not.
     * @param device the reader to search for
     * @return true if already seen, false otherwise
     */
    private fun isKnownDevice(device: BluetoothDevice): Boolean {
        for (i in devices.indices) {    // Increment the match counter if already seen
            if (devices[i].address == device.address) return true
        }
        return false
    }
}