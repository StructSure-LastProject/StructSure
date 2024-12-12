package fr.uge.structsure.bluetooth.cs108

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import androidx.compose.runtime.mutableStateListOf
import com.csl.cslibrary4a.ReaderDevice
import fr.uge.structsure.MainActivity

class Connexion(context: Context) {
    private var scanTask: ScanTask? = null
    private var connectTask: ConnectTask? = null
    var readersList: MutableList<ReaderDevice> = mutableStateListOf()
    // var readersList: ArrayList<ReaderDevice> = ArrayList()
    private val mHandler = Handler()

    private val checkRunnable: Runnable = object : Runnable {
        override fun run() {
            var operating = MainActivity.csLibrary4A.isBleConnected
            if (!operating && scanTask != null) {
                if (!scanTask!!.isCancelled) operating = true
            }
            if (!operating && connectTask != null) {
                if (!connectTask!!.isCancelled) operating = true
            }
            if (!operating) {
                scanTask = ScanTask(context, readersList) {
                    r: ReaderDevice -> onItemClick(r)
                }
                scanTask!!.execute()
            }
            mHandler.postDelayed(this, 5000)
        }
    }

    init {
        if (!MainActivity.csLibrary4A.isBleConnected) readersList.clear()
        checkRunnable.run()
    }

    fun onItemClick(readerDevice: ReaderDevice) {
        //        ReaderDevice readerDevice = readersList.get(position);    // Un ReaderDevice est l'objet qui représente un périphérique BLE compatible
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

        //                if (readersList.size() > position) {    // Update displayed list
        //                  readerDevice.setSelected(!readerDevice.getSelected());    // Invert the selected state of the device
        //                    readersList.set(position, readerDevice);
        //                    for (int i = 0; i < readersList.size(); i++) {
        //                        if (i != position) {
        //                            ReaderDevice readerDevice1 = readersList.get(i);
        //                            if (readerDevice1.getSelected()) {
        //                                readerDevice1.setSelected(false);
        //                                readersList.set(i, readerDevice1);
        //                            }
        //                        }
        //                    }
        //                }
        //                readerListAdapter.notifyDataSetChanged();
    }

    private fun afterConnection(device: ReaderDevice?) {
        if (device != null) {
            readersList.remove(device)
            readersList.add(device)
        }
    }

    /**
     * Once connected
     */
    fun onStop() {
        mHandler.removeCallbacks(checkRunnable)
        if (scanTask != null) {
            scanTask!!.cancel(true)
        }
        if (connectTask != null) {
            connectTask!!.cancel(true)
        }
    }
}