package fr.uge.structsure.bluetooth.cs108

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import androidx.compose.runtime.mutableStateListOf
import com.csl.cslibrary4a.ReaderDevice
import fr.uge.structsure.MainActivity

class Connexion(private val context: Context) {
    private var scanTask: ScanTask? = null
    private var connectTask: ConnectTask? = null
    var readersList: MutableList<ReaderDevice> = mutableStateListOf()

    init {
        if (!MainActivity.csLibrary4A.isBleConnected) readersList.clear()
    }
    
    fun startScan() {
        val scanning = scanTask != null && !scanTask!!.isCancelled

        if (!scanning) {
            println("Starting scan")
            scanTask = ScanTask(context, readersList) {
                    r: ReaderDevice -> onItemClick(r)
            }
            scanTask!!.execute()
        }
    }

    fun onItemClick(readerDevice: ReaderDevice) {
        println("Reader clicked: $readerDevice")
        if (MainActivity.csLibrary4A.isBleConnected && readerDevice.isConnected && (readerDevice.selected)) {
            // If THIS device is already connected, disconnect the device
            println("[CONNECT] - Disconnecting from device " + readerDevice.name)
            MainActivity.csLibrary4A.disconnect(false)
            readersList.clear()
        } else if (!MainActivity.csLibrary4A.isBleConnected && !readerDevice.selected) {
            // If not connected yet to the BLE device
            val validStart =
                connectTask == null || connectTask!!.status == AsyncTask.Status.FINISHED
            if (validStart) {    // Creates a new DeviceConnectTask if not started yet
                if (scanTask != null) scanTask!!.cancel(true) // Stops the scan task?

                println("[CONNECT] - Connecting to device " + readerDevice.name)
                connectTask = ConnectTask(readerDevice) {r -> afterConnection(r)}
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    connectTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                } else {
                    connectTask!!.execute()
                }
            }
        }
    }

    private fun afterConnection(device: ReaderDevice?) {
        if (device != null) {
            readersList.remove(device)
            readersList.add(device)

//            val inventoryRfidTask = RfidTask(
//                context, -1, -1, 0, 0, 0, 0,
//                false
//            )
//            RfidTask(context).execute()
//            InventoryRfidTask.startScan(context)
//             RfidTask.startScan(context)
//            MainActivity.csLibrary4A.setTagRead(1)
//            MainActivity.csLibrary4A.setAccessBank(3)
//            MainActivity.csLibrary4A.setAccessOffset(112)
//            MainActivity.csLibrary4A.setAccessCount(1)
//            MainActivity.csLibrary4A.setNotificationListener {
//                MainActivity.csLibrary4A.appendToLog("setNotificationListener TRIGGER key is pressed in search inventory.")
//            }
//            MainActivity.csLibrary4A.startOperation(RfidReaderChipData.OperationTypes.TAG_SEARCHING)
//            var geigerSearchTask = InventoryRfidTask(
//                context, -1, -1, 0, 0, 0, 0, true, true,
//                null, null, null /* textView */, null,
//                null, null, null, null, null, null
//            )
//            geigerSearchTask.execute()

        }

    }

    /**
     * Once connected
     */
    fun onStop() {
        if (scanTask != null) {
            println("Scan stopped")
            scanTask!!.cancel(true)
        }
        if (connectTask != null) {
            connectTask!!.cancel(true)
        }
    }
}