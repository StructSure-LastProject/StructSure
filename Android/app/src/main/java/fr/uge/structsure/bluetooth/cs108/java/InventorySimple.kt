package fr.uge.structsure.bluetooth.cs108.java

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AbsListView
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.csl.cslibrary4a.NotificationConnector
import com.csl.cslibrary4a.ReaderDevice
import com.csl.cslibrary4a.RfidReaderChipData
import com.csl.cslibrary4a.RfidReaderChipData.Rx000pkgData
import fr.uge.structsure.MainActivity
import java.util.Collections

class InventorySimple(private val context: Context) {
    companion object {
        var tagsIndexList: ArrayList<TagsIndex> = ArrayList()
        var tagsList: ArrayList<ReaderDevice> = ArrayList()
    }

    private val bAdd2End = false
    private var mHandler: Handler = Handler()

    private var readerListAdapter: ReaderListAdapter? = null

    init {

        val bSelect4detail = true
        val needDupElim = true
        val need4Extra1 = if (MainActivity.csLibrary4A.getPortNumber() > 1) true else false
        val need4Extra2 = false
        readerListAdapter = ReaderListAdapter(context, 0, tagsList, bSelect4detail, true, needDupElim, need4Extra1, need4Extra2)
    }

    fun onResume() {
        setNotificationListener()
    }

    fun onPause() {
        MainActivity.csLibrary4A.setNotificationListener(null)
    }

    fun onDestroy() {
        mHandler.removeCallbacks(runnableCheckReady)
        MainActivity.csLibrary4A.setNotificationListener(null)
        MainActivity.csLibrary4A.setSameCheck(true)
        MainActivity.csLibrary4A.setInvBrandId(false)
    }

    fun setNotificationListener() {
        MainActivity.csLibrary4A.setNotificationListener(NotificationConnector.NotificationListener {
            MainActivity.csLibrary4A.appendToLog("TRIGGER key is pressed.")
            inventoryHandler_tag()
        })
    }

    fun startStopHandler(buttonTrigger: Boolean) {
        if (buttonTrigger) MainActivity.csLibrary4A.appendToLog("getTriggerButtonStatus = " + MainActivity.csLibrary4A.getTriggerButtonStatus())
        else MainActivity.csLibrary4A.appendToLog("TriggerButton is pressed")

        var started = false
        if (bRunningInventory) started = true
        if (buttonTrigger && ((started && MainActivity.csLibrary4A.getTriggerButtonStatus()) || (started == false && MainActivity.csLibrary4A.getTriggerButtonStatus() === false))) {
            MainActivity.csLibrary4A.appendToLog("BARTRIGGER: trigger ignore")
            return
        }
        MainActivity.csLibrary4A.appendToLog("started = $started")
        if (started == false) {
            if (MainActivity.csLibrary4A.isBleConnected() === false) {
                Toast.makeText(context, "BLE disbaled", Toast.LENGTH_SHORT).show()
                return
            } else if (MainActivity.csLibrary4A.isRfidFailure()) {
                Toast.makeText(context, "Rfid is disabled", Toast.LENGTH_SHORT).show()
                return
            } else if (MainActivity.csLibrary4A.mrfidToWriteSize() !== 0) {
                Toast.makeText(context, "Not Ready", Toast.LENGTH_SHORT) .show()
                mHandler.post(runnableCheckReady)
                return
            }

            uplinkPacketList.clear()

            timeMillis = System.currentTimeMillis()
            startTimeMillis = timeMillis
            runTimeMillis = timeMillis
            total = 0

            tagsList.clear()
            tagsIndexList.clear()
            println("Start scanning sensors")

            startInventoryTask()
            bRunningInventory = true
            myHandler.post(runnableSimpleInentory)
        } else {
            MainActivity.csLibrary4A.abortOperation()
            bRunningInventory = false
        }
    }

    var bRunningInventory: Boolean = false
    var total: Int = 0
    var timeMillis: Long = 0
    var startTimeMillis: Long = 0
    var runTimeMillis: Long = 0
    var rateTimeMillis: Long = 0
    var iTagTarget: Int = 0
    var iTagGot: Int = 0
    var myHandler: Handler = Handler(Looper.getMainLooper())
    var uplinkPacketList: ArrayList<Rx000pkgData?> = ArrayList()
    var runnableSimpleInentory: Runnable = object : Runnable {
        override fun run() {
            var uplinkPacket: Rx000pkgData
            if (MainActivity.csLibrary4A.isBleConnected() && bRunningInventory) {
                while (MainActivity.csLibrary4A.mrfidToWriteSize() === 0 && (iTagTarget == 0 || iTagGot < iTagTarget)) {
                    if (System.currentTimeMillis() > runTimeMillis + 1000) {
                        runTimeMillis = System.currentTimeMillis()
                        val timePeriod = (System.currentTimeMillis() - startTimeMillis) / 1000
                        if (timePeriod > 0) {
                            println("RFID run time: $timePeriod sec")
                        }
                    }
                    uplinkPacket = MainActivity.csLibrary4A.onRFIDEvent()
                    if (uplinkPacket == null) break
                    else {
                        uplinkPacketList.add(uplinkPacket)
                        iTagGot++
                    }
                }
                if (iTagTarget != 0 && iTagGot >= iTagTarget) {
                    MainActivity.csLibrary4A.abortOperation()
                    bRunningInventory = false
                }

                if (iTagGot != 0) {
                    // println("RfidView: Total:$iTagGot")

                    val tagRate: Long = MainActivity.csLibrary4A.getTagRate()
                    var strRate = ""
                    var bUpdateRate = false
                    if (tagRate >= 0) {
                        strRate = tagRate.toString()
                        bUpdateRate = true
                    } else {
                        if (System.currentTimeMillis() - rateTimeMillis > 1500) {
                            strRate = "___"
                            bUpdateRate = true
                        }
                    }
                    if (bUpdateRate) {
                        // println("RateView: Total:$strRate")
                        rateTimeMillis = System.currentTimeMillis()
                    }
                }
                myHandler.postDelayed(this, 200)
            } else {
                bRunningInventory = false
                println("Stopping sensor scan...")
            }
        }
    }

    fun inventoryHandler_tag() {
        var rssi = 0.0
        var phase = -1
        var chidx = -1
        var port = -1
        var total = 0

        while (uplinkPacketList.size != 0) {
            val uplinkPacket = uplinkPacketList[0]
            uplinkPacketList.removeAt(0)
            val tagData = uplinkPacket

            var match = false
            total++
            rssi = uplinkPacket!!.decodedRssi
            phase = uplinkPacket.decodedPhase
            chidx = uplinkPacket.decodedChidx
            port = uplinkPacket.decodedPort

            var deviceTag: ReaderDevice? = null
            var iMatchItem = -1 // Can be changed
            if (iMatchItem >= 0) {
                deviceTag = tagsList.get(iMatchItem)
                var count = deviceTag.count
                count++
                deviceTag.count = count
                deviceTag.rssi = rssi
                deviceTag.phase = phase
                deviceTag.channel = chidx
                deviceTag.port = port
                tagsList.set(iMatchItem, deviceTag)
                match = true
            }
            if (match == false) {
                deviceTag = ReaderDevice(
                    "",
                    MainActivity.csLibrary4A.byteArrayToString(uplinkPacket.decodedEpc),
                    false,
                    null,
                    MainActivity.csLibrary4A.byteArrayToString(uplinkPacket.decodedPc),
                    null,
                    (if (uplinkPacket.decodedCrc != null) MainActivity.csLibrary4A.byteArrayToString(
                        uplinkPacket.decodedCrc
                    ) else null),
                    null,
                    null,
                    0,
                    0,
                    null,
                    0,
                    0,
                    null,
                    null,
                    null,
                    null,
                    1,
                    rssi,
                    phase,
                    chidx,
                    port,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0f,
                    null,
                    0
                )
                if (bAdd2End) tagsList.add(deviceTag)
                else tagsList.add(0, deviceTag)
                val tagsIndex: TagsIndex = TagsIndex(
                    MainActivity.csLibrary4A.byteArrayToString(
                        uplinkPacket.decodedEpc
                    ), tagsList.size - 1
                )
                tagsIndexList.add(tagsIndex)
                Collections.sort(tagsIndexList)
            }
        }
        MainActivity.csLibrary4A.appendToLog("readerListAdapter is " + (if (readerListAdapter != null) "valid" else "null"))
        if (readerListAdapter != null) readerListAdapter!!.notifyDataSetChanged()
        println("RfidView: ${("Unique:" + tagsList.size)} Total:$total")
    }

    fun startInventoryTask() {
        MainActivity.csLibrary4A.appendToLog("startInventoryTask")
        MainActivity.csLibrary4A.restoreAfterTagSelect()
        MainActivity.csLibrary4A.startOperation(RfidReaderChipData.OperationTypes.TAG_INVENTORY_COMPACT)
    }

    private val runnableCheckReady: Runnable = object : Runnable {
        override fun run() {
            if (MainActivity.csLibrary4A.mrfidToWriteSize() != 0) {
                MainActivity.csLibrary4A.setNotificationListener(null)
                mHandler.postDelayed(this, 500)
            } else {
                setNotificationListener()
            }
        }
    }
}