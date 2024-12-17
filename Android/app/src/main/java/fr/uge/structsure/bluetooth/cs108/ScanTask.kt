package fr.uge.structsure.bluetooth.cs108

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import androidx.core.app.ActivityCompat
import com.csl.cslibrary4a.BluetoothGatt.Cs108ScanData
import com.csl.cslibrary4a.ReaderDevice
import fr.uge.structsure.MainActivity

class ScanTask(private val context: Context, private var readersList: MutableList<ReaderDevice>, private val callback: (r: ReaderDevice) -> Unit) : AsyncTask<Void, String, String>() {
    private var timeMillisUpdate = System.currentTimeMillis()
    private var readersListOld: ArrayList<ReaderDevice> = ArrayList()
    private var wait4process: Boolean = false
    private var scanning: kotlin.Boolean = false

    private val mScanResultList = ArrayList<Cs108ScanData>()

    override fun doInBackground(vararg p0: Void?): String {
        while (!isCancelled) {
            if (!wait4process) {
                val cs108ScanData = MainActivity.csLibrary4A.newDeviceScanned
                if (cs108ScanData != null) mScanResultList.add(cs108ScanData)
                if (!scanning || mScanResultList.size != 0 || System.currentTimeMillis() - timeMillisUpdate > 10000) {
                    wait4process = true
                    publishProgress("")
                }
            }
        }
        return "End of Asynctask()"
    }

    override fun onProgressUpdate(vararg output: String?) {
        if (!MainActivity.csLibrary4A.isBleConnected) readersList.clear()
        if (!scanning) {    // Starts the scan the first time
            scanning = true
            if (!MainActivity.csLibrary4A.scanLeDevice(true)) cancel(true)
        }
        var listUpdated = false
        while (mScanResultList.size != 0) {
            val scanResultA = mScanResultList.removeAt(0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {     // Ask for BLE authorizations?
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) continue
            } else if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH
                ) != PackageManager.PERMISSION_GRANTED
            ) continue
            if (scanResultA.device.type == BluetoothDevice.DEVICE_TYPE_LE) {
                var match = false
                for (i in readersList.indices) {    // Increment the match counter if already seen
                    if (readersList[i].address
                            .matches(scanResultA.device.address.toRegex())
                    ) {
                        val readerDevice1: ReaderDevice = readersList[i]
                        var count = readerDevice1.count
                        count++
                        readerDevice1.count = count
                        readerDevice1.rssi = scanResultA.rssi.toDouble()
                        readersList[i] = readerDevice1
                        listUpdated = true
                        match = true
                        break
                    }
                }
                if (!match) {    // First time detected
                    println("NEW DEVICE DETECTED: name=" + scanResultA.device.name + ", address=" + scanResultA.device.address)
                    val readerDevice = ReaderDevice(
                        scanResultA.device.name,
                        scanResultA.device.address,
                        false,
                        "",
                        1,
                        scanResultA.rssi.toDouble(),
                        scanResultA.serviceUUID2p2
                    )
                    var strInfo = ""
                    if (scanResultA.device.bondState == 12) {
                        strInfo += "BOND_BONDED\n"
                    }
                    readerDevice.details =
                        strInfo + "scanRecord=" + MainActivity.csLibrary4A.byteArrayToString(
                            scanResultA.scanRecord
                        )
                    readersList.add(readerDevice)
                    listUpdated = true
                }
            }
        }
        if (System.currentTimeMillis() - timeMillisUpdate > 10000) {  // TODO Inspect, strange things seems to happen here
            timeMillisUpdate = System.currentTimeMillis()
            for (i in readersList.indices) {
                val readerDeviceNew: ReaderDevice = readersList[i]
                var matched = false
                for (k in readersListOld.indices) {
                    val readerDeviceOld = readersListOld[k]
                    if (readerDeviceOld.address.matches(readerDeviceNew.address.toRegex())) {
                        matched = true
                        if (readerDeviceOld.count >= readerDeviceNew.count) {
                            readersList.removeAt(i)
                            listUpdated = true
                            readersListOld.removeAt(k)
                        } else readerDeviceOld.count = readerDeviceNew.count
                        break
                    }
                }
                if (!matched) {
                    val readerDevice1 = ReaderDevice(
                        null,
                        readerDeviceNew.address,
                        false,
                        null,
                        readerDeviceNew.count,
                        0.0
                    )
                    readersListOld.add(readerDevice1)
                }
            }
            MainActivity.csLibrary4A.scanLeDevice(false)
            scanning = false
        }
        if (listUpdated) {
            // Updates the displayed devices
        }
        wait4process = false
    }

    override fun onCancelled() {
        super.onCancelled()
        deviceScanEnding()
    }

    override fun onPostExecute(result: String?) {
        deviceScanEnding()
    }

    /**
     * Stops the devices scan
     */
    fun deviceScanEnding() {
        MainActivity.csLibrary4A.scanLeDevice(false)
    }
}