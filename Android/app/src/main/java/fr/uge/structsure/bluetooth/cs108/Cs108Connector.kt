package fr.uge.structsure.bluetooth.cs108

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.ActivityCompat
import com.csl.cslibrary4a.ReaderDevice
import fr.uge.structsure.MainActivity
import fr.uge.structsure.MainActivity.Companion.csLibrary4A
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CancellationException

class Cs108Connector(private val context: Context) {
    companion object {
        private var scanTask: Job? = null
        private var pairTask: Job? = null
        private var batteryTask: Job? = null
        var battery: Int = -1
            private set
        var devices: MutableList<ReaderDevice> = mutableStateListOf()
            private set
        var device: ReaderDevice? = null
            private set
        var isReady: Boolean = false
            private set
    }

    /** Hook used just after device bluetooth connection success */
    private var onBleConnected: (success: Boolean) -> Unit = {}

    /** Hook used just after device initialization completed */
    private var onDeviceReady: () -> Unit = {}

    /** Hook used when the battery level change */
    private var onBatteryChange: (level: Int) -> Unit = {}

    init {
        csLibrary4A.setBatteryDisplaySetting(1)
    }

    /**
     * Start scanning for any nearby compatible Bluetooth Low Energy
     * devices and add them in the devices list.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun start() {
        if (scanTask != null) return
        MainActivity.csLibrary4A.scanLeDevice(true)
        scanTask = GlobalScope.launch { pollDevices() }
        println("[DeviceConnector] Scan started")
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
                bleConnected = waitForBleConnection(device)
                if (!bleConnected) return@launch
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
            println("[DeviceConnector] Disconnected from device")
            MainActivity.csLibrary4A.disconnect(false)
            devices.remove(device)
            device!!.isConnected = false
            devices.add(device!!)
            device = null
            isReady = false
        }
    }

    /**
     * Sets the hook of device bluetooth pairing. The hook receives a
     * boolean that tells whether or not the connection is successful
     * or not.
     * Note that this step is just before isReady, and that isReady
     * must be waited for before calling scanning operations
     * @param onBleConnected the function to attach to the hook
     */
    fun onBleConnected(onBleConnected: (success: Boolean) -> Unit = {}) {
        this.onBleConnected = onBleConnected
    }

    /**
     * Sets the hook of device being ready. The hook is called as soon
     * as the freshly connected device is fully initialized and ready
     * to scan for chips.
     * @param onReady the function to attach to the hook
     */
    fun onReady(onReady: () -> Unit = {}) {
        onDeviceReady = onReady
    }

    /**
     * Sets the hook of the device battery level. The hook is called
     * each time the battery level change
     * @param onBatteryChange the function to attach to the hook
     */
    fun onBatteryChange(onBatteryChange: (level: Int) -> Unit = {}) {
        this.onBatteryChange = onBatteryChange
    }

    /**
     * Wait for the latest bluetooth pairing request to be completed.
     * This function also handle device.connected update and BLE
     * hook response.
     * @param device the device to connect to
     * @return true in case of success, false after timeout
     */
    private suspend fun waitForBleConnection(device: ReaderDevice): Boolean {
        var retry = 30
        while (pairTask!!.isActive && retry-- > 0) {
            if (MainActivity.csLibrary4A.isBleConnected) {
                Cs108Connector.device = device
                devices.remove(device)
                device.isConnected = true
                devices.add(device)
                onBleConnected(true)
                println("[DeviceConnector] BLE connected")
                return true
            }
            delay(500L)
        }
        onBleConnected(false)
        println("[DeviceConnector] BLE timeout")
        return false
    }

    /**
     * Wait for the latest device to be ready for other operations.
     */
    private suspend fun waitForDeviceReady() {
        while (pairTask!!.isActive && MainActivity.csLibrary4A.mrfidToWriteSize() != 0) {
            delay(500L)
        }
        if (batteryTask == null) batteryTask = GlobalScope.launch { pollBattery() }
        println("[DeviceConnector] Device Ready")
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
     * Ask continuously the cs108 library for the updated battery
     * level and updates the battery field
     */
    private suspend fun pollBattery() {
        try {
            while (batteryTask!!.isActive) {
                if (MainActivity.csLibrary4A.isBleConnected) {
                    val lvl = MainActivity.csLibrary4A.getBatteryDisplay(false)
                    val pos = lvl.indexOf('%')
                    val level = if (pos == -1) 0 else lvl.substring(0, lvl.indexOf('%')).toInt()
                    if (level != battery) {
                        battery = level
                        onBatteryChange(battery)
                    }
                } else if (battery !=  -1) {
                    battery = -1
                    onBatteryChange(battery)
                }
                delay(2000L)
            }
        } catch (e: CancellationException) {
            // Interrupted
        }
        println("[DeviceConnector] Battery poll stopped")
        batteryTask = null
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