package fr.uge.structsure.bluetooth.cs108

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
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

/**
 * Proxy for the csLibrary4A library that makes interacting with the
 * RFID interrogator simpler.
 * This class provides all methods useful to monitor and interact with
 * connection state of the interrogator. Once connected, RFID chips
 * can be read using Cs108Scanner
 */
class Cs108Connector(private val context: Context) {
    companion object {
        /** The label of this class for the logs */
        private const val LOG_TAG = "TinyRFID-Connector"

        /** List with timeout to smooth real world data */
        private val timedDevices = TimedDevicesList(3000)

        /** Saves the background task that scan for nearby devices */
        private var scanTask: Job? = null

        /** Saves the background task that handle device pairing */
        private var pairTask: Job? = null

        /** Saves the background task that updates the battery level */
        private var batteryTask: Job? = null

        /** The currently known battery level, -1 if the device is not ready */
        var battery: Int = -1
            private set

        /** Dynamic list of compatible BLE devices detected by Cs108 lib */
        var devices = timedDevices.devices

        /** The currently connected device if any */
        var device: ReaderDevice? = null
            private set

        /** Whether or not a BLE device is connected and ready to be used */
        var isReady: Boolean = false
            private set

        /** Whether or not the bluetooth function is enabled on the phone */
        val isBleEnabled = MutableLiveData(BluetoothAdapter.getDefaultAdapter().isEnabled)

        /** Whether or not a bluetooth device is connected */
        val isBleConnected = MutableLiveData(false)

        /**
         * Function that receives broadcast related to the bluetooth
         * state changes. It changes isBleEnabled and isBleConnected
         * variables when the bluetooth is turned ON/OFF on the phone.
         */
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
        Log.d(LOG_TAG, "Nearby devices scan started")
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
                if (Cs108Connector.device == null) return@launch
                onDeviceReady()
                isReady = true
            } catch (e: CancellationException) {
                // Interrupted
            }
            if (bleConnected == null) onBleConnected(false)
            Log.i(LOG_TAG, "Device connection done: ${if (bleConnected == true) "success" else "failure"}")
            pairTask = null
        }
    }

    /**
     * Disconnects from the currently connected device (if existing)
     */
    fun disconnect() {
        if (device != null) {
            csLibrary4A.disconnect(false)
            device?.let {
                timedDevices.remove(it)
                it.isConnected = false
            }
            device = null
            isReady = false
            isBleConnected.postValue(false)
            Log.i(LOG_TAG, "Disconnected from device")
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
                device.isConnected = true
                onBleConnected(true)
                Log.d(LOG_TAG, "Device connected: ${device.name}")
                return true
            }
            delay(500L)
        }
        onBleConnected(false)
        Log.w(LOG_TAG, "Device connection failed: timeout")
        return false
    }

    /**
     * Wait for the latest device to be ready for other operations.
     */
    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun waitForDeviceReady() {
        var interrupted = false
        coroutineScope {
            while (csLibrary4A.mrfidToWriteSize() != 0) {
                ensureActive()
                if (!csLibrary4A.isBleConnected) {
                    deviceDisconnected()
                    interrupted = true
                    return@coroutineScope
                }
                delay(500L)
            }
        }
        if (interrupted) return // Failed to pair
        if (batteryTask == null) {
            batteryTask = GlobalScope.launch { pollBattery() }
        }
        Log.d(LOG_TAG, "Connected device is now ready")
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
                    if (data != null && checkPermission() && data.device.type == BluetoothDevice.DEVICE_TYPE_LE) {
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
                        timedDevices.add(readerDevice)
                    } else {
                        delay(100L)
                    }
                }
            }
        } catch (e: CancellationException) {
            // Interrupted
        }
        Log.d(LOG_TAG, "Nearby devices scan stopped")
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
                        deviceDisconnected()
                        break
                    }
                    delay(1000L)
                }
            }
        } catch (e: CancellationException) {
            // Interrupted
        }
        Log.d(LOG_TAG, "Battery polling stopped")
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
     * Handle device disconnection by updated BLE states variables
     * and devices list
     */
    private fun deviceDisconnected() {
        battery = -1
        onBatteryChange(-1)
        disconnect()
        // Restart the scan to be able to discover the device back
        if (scanTask != null) {
            csLibrary4A.scanLeDevice(false)
            csLibrary4A.scanLeDevice(true)
        }
    }
}