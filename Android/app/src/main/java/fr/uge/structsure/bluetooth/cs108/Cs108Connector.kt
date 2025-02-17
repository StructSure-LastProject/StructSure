package fr.uge.structsure.bluetooth.cs108

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.csl.cslibrary4a.ReaderDevice
import fr.uge.structsure.MainActivity.Companion.csLibrary4A
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
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
        var isConnected: Boolean = csLibrary4A.isBleConnected
        var isReady: Boolean = false
            private set
        val isBleEnabled = MutableLiveData(BluetoothAdapter.getDefaultAdapter().isEnabled)
        val isBleConnected = MutableLiveData(false)
        val bluetoothAdapter: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action

                if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    when (state) {
                        BluetoothAdapter.STATE_OFF -> {
                            isBleEnabled.postValue(false)
                            isBleConnected.postValue(false)
                        }
                        BluetoothAdapter.STATE_ON -> {
                            isBleEnabled.postValue(true)
                        }
                    }
                }
            }
        }
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
        csLibrary4A.scanLeDevice(true)
        scanTask = GlobalScope.launch { pollDevices() }
        println("[DeviceConnector] Scan started")
    }

    /**
     * Interrupt the device scan (if started).
     */
    fun stop() {
        if (scanTask == null) return
        csLibrary4A.scanLeDevice(false)
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
                csLibrary4A.connect(device)
                bleConnected = waitForBleConnection(device)
                if (!bleConnected) return@launch
                isBleConnected.postValue(true)
                waitForDeviceReady()
                onDeviceReady()
                isReady = true
            } catch (e: CancellationException) {
                // Interrupted
            }
            if (bleConnected == null) onBleConnected(false)
            println("[DeviceConnector] Connection ended")
            pairTask = null
        }
    }

    /**
     * Disconnects from the currently connected device (if existing)
     */
    fun disconnect() {
        if (device != null) {
            println("[DeviceConnector] Disconnected from device")
            csLibrary4A.disconnect(false)
            devices.remove(device)
            device!!.isConnected = false
            device = null
            isReady = false
            isBleConnected.postValue(false)
            println("[DeviceConnector] Disconnected from device")
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
        while (retry-- > 0) {
            coroutineScope {
                ensureActive()
            }
            if (csLibrary4A.isBleConnected) {
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
        coroutineScope {
            while (csLibrary4A.mrfidToWriteSize() != 0) {
                ensureActive()
                delay(500L)
            }
        }
        if (batteryTask == null) {
            batteryTask = GlobalScope.launch { pollBattery() }
        }
        println("[DeviceConnector] Device Ready")
    }

    /**
     * Ask continuously the cs108 library for any freshly read device
     * and adds it to the devices list if not added yet
     */
    private suspend fun pollDevices() {
        try {
            coroutineScope {
                while (true) {
                    ensureActive()
                    val data = csLibrary4A.newDeviceScanned
                    if (data != null && checkPermission()) {
                        if (data.device.type == BluetoothDevice.DEVICE_TYPE_LE) {
                            val strInfo = if (data.device.bondState == 12) "BOND_BONDED\n" else ""
                            val readerDevice = ReaderDevice(
                                data.device.name,
                                data.device.address,
                                false,
                                strInfo + "scanRecord=" + csLibrary4A.byteArrayToString(
                                    data.scanRecord
                                ),
                                1,
                                data.rssi.toDouble(),
                                data.serviceUUID2p2
                            )
                            upsertDevice(readerDevice)
                        }
                    }
                    delay(1000L)
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
            coroutineScope {
                while (true) {
                    ensureActive()
                    if (csLibrary4A.isBleConnected) {
                        val lvl = csLibrary4A.getBatteryDisplay(false)
                        val pos = lvl.indexOf('%')
                        val level = if (pos == -1) 0 else lvl.substring(0, lvl.indexOf('%')).toInt()
                        if (level != battery) {
                            battery = level
                            onBatteryChange(battery)
                        }
                    } else if (battery !=  -1) {
                        /* Handle device disconnection */
                        battery = -1
                        onBatteryChange(-1)
                        disconnect()
                        // Restart the scan to be able to discover the device back
                        csLibrary4A.scanLeDevice(false)
                        csLibrary4A.scanLeDevice(true)
                        break
                    }
                    delay(2000L)
                }
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
    private fun upsertDevice(device: ReaderDevice) {
        for (i in devices.indices) {
            if (devices[i].address == device.address) {
                /* Update */
                devices[i] = device
                return
            }
        }
        /* Insert */
        devices.add(device)
    }
}