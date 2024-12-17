package fr.uge.structsure.bluetooth.cs108

import android.os.AsyncTask
import com.csl.cslibrary4a.ReaderDevice
import fr.uge.structsure.MainActivity

class ConnectTask(private var connectingDevice: ReaderDevice, private val callback: (d: ReaderDevice?) -> Unit) : AsyncTask<Void, String, Int>() {
    private var waitTime: Int = 30
    private var setting = -1

    override fun onPreExecute() {
        super.onPreExecute()
        MainActivity.csLibrary4A.connect(connectingDevice)
        waitTime = 30
        setting = -1
    }

    override fun doInBackground(vararg a: Void?): Int {
        do {
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            publishProgress("kkk ")
            if (MainActivity.csLibrary4A.isBleConnected) {
                setting = 0
                break
            }
        } while (--waitTime > 0)
        if (setting != 0 || waitTime <= 0) {
            cancel(true)
            callback(null)
        }
        publishProgress("mmm ")
        return waitTime
    }

    override fun onProgressUpdate(vararg output: String?) {
        for (o in output) {
            println(o)
        }
    }

    override fun onCancelled(result: Int?) {
        if (setting >= 0) {
            println("TOAST - Setup problem after connection. Disconnect")
        } else {
            MainActivity.csLibrary4A.isBleConnected
            println("TOAST - Unable to connect device")
        }
        super.onCancelled()
        MainActivity.csLibrary4A.disconnect(false)
        callback(null)
    }

    override fun onPostExecute(result: Int?) {
        connectingDevice.isConnected = true

        //        readerListAdapter.notifyDataSetChanged();
        println("TOAST - BLE is connected")
        callback(connectingDevice)
        super.onPostExecute(result)
    }
}