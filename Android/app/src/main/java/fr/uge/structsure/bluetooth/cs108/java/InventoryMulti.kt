package fr.uge.structsure.bluetooth.cs108.java

import android.content.Context
import android.os.AsyncTask
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.csl.cslibrary4a.NotificationConnector
import com.csl.cslibrary4a.ReaderDevice
import com.csl.cslibrary4a.RfidReaderChipData
import fr.uge.structsure.MainActivity
import fr.uge.structsure.bluetooth.cs108.RfidTask

class InventoryMulti(private val context: Context) {
    companion object {
        var tagsIndexList: ArrayList<TagsIndex> = ArrayList()
        var tagsList: ArrayList<ReaderDevice> = ArrayList()
        var runningInventoryRfidTask = false
        private var mHandler: Handler = Handler()
    }

    private val bAdd2End = false
    private var bMultiBank = false
    private var bMultiBankInventory = false
    private var mDid: String? = null
    var vibrateTimeBackup: Int = 0

    private var readerListAdapter: ReaderListAdapter? = null
    private var inventoryRfidTask: RfidTask? = null

    fun clearTagsList() {
        MainActivity.csLibrary4A.appendToLog("InventoryRfidMultiFragment: NOT onOptionsItemSelected: clearTagsList")
        MainActivity.csLibrary4A.appendToLog(
            ("runningInventoryRfidTask = " + runningInventoryRfidTask).toString() + ", readerListAdapter" + (if (readerListAdapter != null) " tagCount = " + java.lang.String.valueOf(
                readerListAdapter!!.count
            ) else " = NULL")
        )
        if (runningInventoryRfidTask) return
        tagsList.clear()
        tagsIndexList.clear()
        readerListAdapter!!.notifyDataSetChanged()
    }

    init {
        MainActivity.csLibrary4A.appendToLog("mDid = " + mDid + ", MainActivity.mDid = " + mDid)
        if (mDid != null) {
            if (mDid!!.indexOf("E2827001") == 0) {
                // checkBoxFilterByTid!!.visibility = View.VISIBLE
                // checkBoxFilterByTid.setText("filter FM13DT160 only");
            }
        } else if (bMultiBankInventory == false) {
            // checkBoxFilterByEpc!!.visibility = View.VISIBLE
        }

        var bSelect4detail = true
        if (bMultiBankInventory) bSelect4detail = false
        val needDupElim = true

        val need4Extra1 = if (MainActivity.csLibrary4A.getPortNumber() > 1) true else false
        val need4Extra2 = (if (mDid != null) true else false)

        readerListAdapter = ReaderListAdapter(
            context,
            0,
            tagsList,
            bSelect4detail,
            true,
            needDupElim,
            need4Extra1,
            need4Extra2
        )
        // rfidListView!!.onItemClickListener =
        //     OnItemClickListener { parent, view, position, id ->
        //         val readerDevice: ReaderDevice = readerListAdapter!!.getItem(position)!!
        //         if (readerDevice.selected) {
        //             readerDevice.selected = false
        //         } else {
        //             readerDevice.selected = true
        //             var strPopup = readerDevice.upcSerial
        //             MainActivity.csLibrary4A.appendToLog("strPopup = " + (strPopup ?: "null"))
        //             if (strPopup != null && strPopup.trim { it <= ' ' }.length != 0) {
        //                 strPopup = MainActivity.csLibrary4A.getUpcSerialDetail(strPopup)
        //                 println("POPUP $strPopup")
        //             }
        //         }
        //         tagsList[position] = readerDevice
        //         for (i in 0 until tagsList.size) {
        //             if (i != position) {
        //                 val readerDevice1: ReaderDevice = tagsList[i]
        //                 if (readerDevice1.selected) {
        //                     readerDevice1.selected = false
        //                     tagsList[i] = readerDevice1
        //                 }
        //             }
        //         }
        //         readerListAdapter!!.notifyDataSetChanged()
        //     }

        // button!!.setOnClickListener { startStopHandler(false) }

        // buttonT1.setOnClickListener {
        //     val buttonText = buttonT1.text.toString().trim { it <= ' ' }
        //     if (buttonText.uppercase(Locale.getDefault()).matches("BUZ".toRegex())) {
        //         buttonT1.text = "STOP"
        //     } else {
        //         buttonT1.text = "BUZ"
        //     }
        // }
    }

    fun onResume() {
        // setNotificationListener()
    }

    fun onPause() {
        MainActivity.csLibrary4A.setNotificationListener(null)
    }

    fun onDestroy() {
        mHandler.removeCallbacks(runnableCheckReady)
        MainActivity.csLibrary4A.setNotificationListener(null)
        // if (inventoryRfidTask != null) { TODO restore for InventoryRfidTask
        //     inventoryRfidTask!!.taskCancelReason = InventoryRfidTask.TaskCancelRReason.DESTORY
        //     MainActivity.csLibrary4A.abortOperation() // added in case inventoryRiidTask is removed
        // }
        MainActivity.csLibrary4A.setSameCheck(true)
        MainActivity.csLibrary4A.setInvBrandId(false)
        resetSelectData()
        MainActivity.csLibrary4A.setVibrateTime(vibrateTimeBackup)
    }

    fun setNotificationListener() {
        MainActivity.csLibrary4A.appendToLog("setNotificationListener A in multibank inventory")
        MainActivity.csLibrary4A.setNotificationListener(NotificationConnector.NotificationListener {
            MainActivity.csLibrary4A.appendToLog("setNotificationListener TRIGGER key is pressed in multibank inventory.")
            startStopHandler(true)
        })
    }

    var needResetData: Boolean = false
    fun resetSelectData() {
        MainActivity.csLibrary4A.appendToLog("mDid = " + mDid + ", MainActivity.mDid = " + mDid)
        if (mDid != null && mDid!!.indexOf("E282405") === 0) {
        } else MainActivity.csLibrary4A.restoreAfterTagSelect()
        if (needResetData) {
            MainActivity.csLibrary4A.setTagRead(0)
            MainActivity.csLibrary4A.setAccessBank(1)
            MainActivity.csLibrary4A.setAccessOffset(0)
            MainActivity.csLibrary4A.setAccessCount(0)
            needResetData = false
        }
        if (mDid != null && mDid!!.matches("E203510".toRegex())) MainActivity.csLibrary4A.setSelectCriteriaDisable(
            1
        )
    }

    fun startStopHandler(buttonTrigger: Boolean) {
        MainActivity.csLibrary4A.appendToLog("0 buttonTrigger is $buttonTrigger")
        if (buttonTrigger) MainActivity.csLibrary4A.appendToLog("BARTRIGGER: getTriggerButtonStatus = " + MainActivity.csLibrary4A.getTriggerButtonStatus())
        // if (runningInventoryBarcodeTask) {
        //     Toast.makeText(context, "Running barcode inventory", Toast.LENGTH_SHORT)
        //         .show()
        //     return
        // }
        var started = false
        if (inventoryRfidTask != null) if (inventoryRfidTask!!.getStatus() === AsyncTask.Status.RUNNING) started =
            true
        if (buttonTrigger && ((started && MainActivity.csLibrary4A.getTriggerButtonStatus()) || (started == false && MainActivity.csLibrary4A.getTriggerButtonStatus() === false))) {
            MainActivity.csLibrary4A.appendToLog("BARTRIGGER: trigger ignore")
            return
        }
        if (started == false) {
            if (MainActivity.csLibrary4A.isBleConnected() === false) {
                Toast.makeText(context, "BLE not connected", Toast.LENGTH_SHORT).show()
                return
            } else if (MainActivity.csLibrary4A.isRfidFailure()) {
                Toast.makeText(context, "Rfid is disabled", Toast.LENGTH_SHORT).show()
                return
            } else if (MainActivity.csLibrary4A.mrfidToWriteSize() !== 0) {
                Toast.makeText(context, "Not Ready", Toast.LENGTH_SHORT).show()
                mHandler.post(runnableCheckReady)
                return
            }
            // if (bAdd2End) rfidListView!!.transcriptMode = AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL
            // else rfidListView!!.setSelection(0)
            startInventoryTask()
        } else if (MainActivity.csLibrary4A.mrfidToWriteSize() === 0) {
            // if (bAdd2End) rfidListView!!.transcriptMode = AbsListView.TRANSCRIPT_MODE_NORMAL
            // if (buttonTrigger) inventoryRfidTask!!.taskCancelReason =
            //     InventoryRfidTask.TaskCancelRReason.BUTTON_RELEASE
            // else inventoryRfidTask!!.taskCancelReason = InventoryRfidTask.TaskCancelRReason.STOP
            MainActivity.csLibrary4A.appendToLogView("CANCELLING: StartStopHandler generates taskCancelReason = " + inventoryRfidTask!!.taskCancelReason.toString())
        } else MainActivity.csLibrary4A.appendToLog("BtData. Stop when still writing !!!")
    }

    fun startInventoryTask() {
        MainActivity.csLibrary4A.appendToLog("startInventoryTask")
        var extra1Bank = -1
        var extra2Bank = -1
        var extra1Count = 0
        var extra2Count = 0
        var extra1Offset = 0
        var extra2Offset = 0
        var mDid = this.mDid

        MainActivity.csLibrary4A.appendToLog(
            ("Rin: mDid = " + (mDid
                ?: "null") + ", MainActivity.mDid = " + mDid).toString() + ", bMultiBankInventory = " + bMultiBankInventory
        )
        if (mDid != null && mDid != null) {
            if (mDid.indexOf("E280B12") !== 0) mDid = mDid
        }
        if (mDid != null) {
            MainActivity.csLibrary4A.appendToLog("mDid is valid as $mDid")
            // if (MainActivity.csLibrary4A.getSelectEnable())
            MainActivity.csLibrary4A.setSelectCriteriaDisable(-1)
            MainActivity.csLibrary4A.appendToLog("new mDid is $mDid")
            extra2Bank = 2
            extra2Offset = 0
            extra2Count = 2
            MainActivity.csLibrary4A.appendToLog("mDid = $mDid")
            if (mDid.matches("E2801101".toRegex()) || mDid.matches("E2801102".toRegex()) || mDid.matches(
                    "E2801103".toRegex()
                ) || mDid.matches("E2801104".toRegex()) || mDid.matches("E2801105".toRegex())
            ) {
                extra1Bank = 0
                extra1Offset = 4
                extra1Count = 1
                if (mDid.matches("E2801101".toRegex())) extra2Count = 6
            } else if (mDid.matches("E200B0".toRegex())) {
                extra1Bank = 2
                extra1Offset = 0
                extra1Count = 2
                extra2Bank = 3
                extra2Offset = 0x2d
                extra2Count = 1
            } else if (mDid.matches("E203510".toRegex())) {
                extra1Bank = 2
                extra1Offset = 0
                extra1Count = 2
                extra2Bank = 3
                extra2Offset = 8
                extra2Count = 2
            } else if (mDid.matches("E283A".toRegex())) {
                extra1Bank = 2
                extra1Offset = 0
                extra1Count = 2
                extra2Bank = 3
                extra2Offset = 0
                extra2Count = 8
            } else if (mDid.indexOf("E280B12") == 0) {
                extra1Bank = 2
                extra1Offset = 0
                extra1Count = 2
                extra2Bank = 3
                extra2Offset = 0x120
                extra2Count = 1
            } else if (mDid.indexOf("E280B0") == 0) {
                extra1Bank = 3
                extra1Offset = 188
                extra1Count = 2
                // extra2Bank = 3;
                // extra2Offset = 0x10d;
                // extra2Count = 1;
            } else if (mDid.indexOf("E281D") == 0) { // need atmel firmware 0.2.20
                extra1Bank = 0
                extra1Offset = 4
                extra1Count = 1
                extra2Count = 6
            } else if (mDid.indexOf("E201E") == 0) {
                extra1Bank = 3
                extra1Offset = 112
                extra1Count = 1
                extra2Count = 6
            } else if (mDid.matches("E282402".toRegex())) {
                extra1Bank = 0
                extra1Offset = 11
                extra1Count = 1
                extra2Bank = 0
                extra2Offset = 13
                extra2Count = 1
            } else if (mDid.matches("E282403".toRegex())) {
                extra1Bank = 0
                extra1Offset = 12
                extra1Count = 3
                extra2Bank = 3
                extra2Offset = 8
                extra2Count = 4
            } else if (mDid.matches("E282405".toRegex())) {
                extra1Bank = 0
                extra1Offset = 10
                extra1Count = 5
                extra2Bank = 3
                extra2Offset = 0x12
                extra2Count = 4
            }
            MainActivity.csLibrary4A.appendToLog("mDid = $mDid")
            if (mDid.indexOf("E280B12") == 0) {
                if (mDid == "E280B12B") {
                    MainActivity.csLibrary4A.setSelectCriteria(0, true, 4, 0, 5, 1, 0x220, "8321")
                    MainActivity.csLibrary4A.appendToLog("Hello123: Set Sense at Select !!!")
                } else { // if (MainActivity.mDid.matches("E280B12A")) {
                    MainActivity.csLibrary4A.setSelectCriteriaDisable(-1)
                    MainActivity.csLibrary4A.appendToLog("Hello123: Set Sense at BOOT !!!")
                }
            } else if (mDid.matches("E203510".toRegex())) {
                MainActivity.csLibrary4A.setSelectCriteria(0, true, 7, 4, 0, 2, 0, mDid)
                if (MainActivity.csLibrary4A.get98XX() === 2) MainActivity.csLibrary4A.setCurrentLinkProfile(
                    1
                )
            } else if (mDid.matches("E283A".toRegex())) {
                if (MainActivity.csLibrary4A.get98XX() === 2) MainActivity.csLibrary4A.setCurrentLinkProfile(
                    9
                )
            } else if (mDid.matches("E28240".toRegex())) {
                // if (MainActivity.selectFor !== 0) {
                //     MainActivity.csLibrary4A.setSelectCriteriaDisable(-1)
                //     MainActivity.selectFor = 0
                // }
            } else if (mDid.matches("E282402".toRegex())) {
                // MainActivity.csLibrary4A.appendToLog("selectFor = " + selectFor)
                // if (MainActivity.selectFor !== 2) {
                //     MainActivity.csLibrary4A.setSelectCriteria(0, true, 4, 2, 0, 3, 0xA0, "20")
                //     MainActivity.selectFor = 2
                // }
            } else if (mDid.matches("E282403".toRegex())) {
                // if (MainActivity.selectFor !== 3) {
                //     MainActivity.csLibrary4A.setSelectCriteria(0, true, 4, 2, 0, 3, 0xD0, "1F")
                //     MainActivity.csLibrary4A.setSelectCriteria(1, true, 4, 2, 5, 3, 0xE0, "")
                //     MainActivity.selectFor = 3
                // }
            } else if (mDid.matches("E282405".toRegex())) {
                // if (MainActivity.selectFor !== 5) {
                //     MainActivity.csLibrary4A.setSelectCriteria(0, true, 4, 5, MainActivity.selectHold, 3, 0x3B0, "00")
                //     MainActivity.csLibrary4A.setSelectCriteriaDisable(2)
                //     if (MainActivity.csLibrary4A.getRetryCount() < 2) MainActivity.csLibrary4A.setRetryCount(
                //         2
                //     )
                //     MainActivity.selectFor = 5
                // }
            } else {
                // MainActivity.csLibrary4A.appendToLog("MainActivity.selectFor = " + MainActivity.selectFor)
                // if (MainActivity.selectFor !== -1) {
                //     MainActivity.csLibrary4A.setSelectCriteriaDisable(-1)
                //     MainActivity.selectFor = -1
                // }
            }
            var bNeedSelectedTagByTID = true
            if (mDid.indexOf("E2806894") == 0) {
                mDid = "E2806894"
                Log.i("DebugAurel", "HelloK: Find E2806894 with MainActivity.mDid = " + mDid)
                if (mDid == "E2806894A") {
                    Log.i("DebugAurel", "HelloK: Find E2806894A")
                    MainActivity.csLibrary4A.setInvBrandId(false)
                    MainActivity.csLibrary4A.setSelectCriteriaDisable(1)
                } else if (mDid == "E2806894B") {
                    Log.i("DebugAurel", "HelloK: Find E2806894B")
                    MainActivity.csLibrary4A.setInvBrandId(false)
                    MainActivity.csLibrary4A.setSelectCriteria(0, true, 4, 0, 1, 0x203, "1", true)
                    MainActivity.csLibrary4A.setSelectCriteria(1, true, 4, 2, 2, 0, "E2806894", false)
                    if (true) bNeedSelectedTagByTID = false
                } else if (mDid == "E2806894C" || mDid == "E2806894d") {
                    Log.i("DebugAurel", "HelloK: Find " + mDid)
                    MainActivity.csLibrary4A.setInvBrandId(true)
                    MainActivity.csLibrary4A.setSelectCriteria(0, true, 4, 0, 1, 0x204, "1", true)
                    MainActivity.csLibrary4A.setSelectCriteria(1, true, 4, 2, 2, 0, "E2806894", false)
                    if (true) bNeedSelectedTagByTID = false
                }
            } // else if (mDid.indexOf("E28011") == 0) bNeedSelectedTagByTID = false;

            Log.i("DebugAurel", "BleStreamOut: going to setSelectedTagByTID with mDid = " + mDid + " with extra1Bank = " + extra1Bank + ", extra2Bank = " + extra2Bank + ", bNeedSelectedTagByTID = " + bNeedSelectedTagByTID + ", bMultiBank = " + bMultiBank
            )
            if (bNeedSelectedTagByTID) {
                var strMdid: String = mDid
                if (strMdid.indexOf("E28011") == 0) {
                    var iValue = strMdid.substring(6, 8).toInt(16)
                    iValue = iValue and 0x0F
                    MainActivity.csLibrary4A.appendToLog(String.format("iValue = 0x%X", iValue))
                    strMdid = if (iValue == 1) "E2C011A2"
                    else if (iValue == 2) "E28011C"
                    else if (iValue == 3) "E28011B"
                    else if (iValue == 4) "E28011A"
                    else if (iValue == 5) "E280119"
                    else if (iValue == 6) "E2801171"
                    else if (iValue == 7) "E2801170"
                    else if (iValue == 8) "E2801150"
                    else "E2001" // strMdid.substring(0, 5); even E2801 or E2C01 will return
                }
                MainActivity.csLibrary4A.appendToLog("revised mDid = " + strMdid)

                // MainActivity.csLibrary4A.setSelectCriteriaDisable(-1);
                MainActivity.csLibrary4A.setInvAlgo(MainActivity.csLibrary4A.getInvAlgo())
            }
        }

        MainActivity.csLibrary4A.appendToLog("bMultiBank = $bMultiBank with extra1Bank = $extra1Bank,$extra1Offset,$extra1Count, extra2Bank = $extra2Bank,$extra2Offset,$extra2Count")
        if (bMultiBank == false) {
            MainActivity.csLibrary4A.restoreAfterTagSelect()
            // inventoryRfidTask = InventoryRfidTask(
            //     context, -1, -1, 0, 0, 0, 0,
            //     false, MainActivity.csLibrary4A.getInventoryBeep(),
            //     tagsList, readerListAdapter, null, null,
            //     null, null, null, null, null, null
            // )
            inventoryRfidTask = RfidTask(
                context, -1, -1, 0, 0, 0, 0, false, mDid
            )
            MainActivity.csLibrary4A.startOperation(RfidReaderChipData.OperationTypes.TAG_INVENTORY_COMPACT)
        } else {
            val inventoryUcode8_bc = mDid != null && mDid.matches("E2806894".toRegex()) && (mDid == "E2806894B" || mDid == "E2806894C")
            if ((extra1Bank != -1 && extra1Count != 0) || (extra2Bank != -1 && extra2Count != 0)) {
                if (extra1Bank == -1 || extra1Count == 0) {
                    extra1Bank = extra2Bank
                    extra2Bank = 0
                    extra1Count = extra2Count
                    extra2Count = 0
                    extra1Offset = extra2Offset
                    extra2Offset = 0
                }
                if (extra1Bank == 1) extra1Offset += 2
                if (extra2Bank == 1) extra2Offset += 2
                MainActivity.csLibrary4A.appendToLog(("HelloK: mDid = " + mDid + ", MainActivity.mDid = " + mDid).toString() + " with extra1Bank = " + extra1Bank + "," + extra1Offset + "," + extra1Count + ", extra2Bank = " + extra2Bank + "," + extra2Offset + "," + extra2Count)
                if (mDid != null) MainActivity.csLibrary4A.setResReadNoReply(mDid.matches("E281D".toRegex()))
                if (inventoryUcode8_bc == false) {
                    MainActivity.csLibrary4A.appendToLog("BleStreamOut: Set Multibank")
                    MainActivity.csLibrary4A.setTagRead(if (extra2Count != 0 && extra2Count != 0) 2 else 1)
                    MainActivity.csLibrary4A.setAccessBank(extra1Bank, extra2Bank)
                    MainActivity.csLibrary4A.setAccessOffset(extra1Offset, extra2Offset)
                    MainActivity.csLibrary4A.setAccessCount(extra1Count, extra2Count)
                    needResetData = true
                } else if (needResetData) {
                    MainActivity.csLibrary4A.setTagRead(0)
                    MainActivity.csLibrary4A.setAccessBank(1)
                    MainActivity.csLibrary4A.setAccessOffset(0)
                    MainActivity.csLibrary4A.setAccessCount(0)
                    needResetData = false
                }
            } else resetSelectData()
            MainActivity.csLibrary4A.appendToLog("startInventoryTask: going to startOperation with extra1Bank = $extra1Bank,$extra1Offset,$extra1Count, extra2Bank = $extra2Bank,$extra2Offset,$extra2Count")
            // inventoryRfidTask = InventoryRfidTask(context, extra1Bank, extra2Bank,
            //     extra1Count,
            //     extra2Count,
            //     extra1Offset,
            //     extra2Offset,
            //     false,
            //     MainActivity.csLibrary4A.getInventoryBeep(),
            //     tagsList,
            //     readerListAdapter,
            //     null,
            //     mDid,
            //     null,
            //     null,
            //     null,
            //     null,
            //     null,
            //     null
            // )
            inventoryRfidTask = RfidTask(
                context,
                extra1Bank,
                extra2Bank,
                extra1Count,
                extra2Count,
                extra1Offset,
                extra2Offset,
                false,
                mDid
            )
            if (inventoryUcode8_bc) MainActivity.csLibrary4A.startOperation(RfidReaderChipData.OperationTypes.TAG_INVENTORY_COMPACT)
            else MainActivity.csLibrary4A.startOperation(RfidReaderChipData.OperationTypes.TAG_INVENTORY)
        }
        inventoryRfidTask!!.execute()
    }

    private val runnableCheckReady: Runnable = object : Runnable {
        override fun run() {
            if (MainActivity.csLibrary4A.mrfidToWriteSize() !== 0) {
                println("Please wait")
                MainActivity.csLibrary4A.setNotificationListener(null)
                mHandler.postDelayed(this, 500)
            } else {
                println("Sensor scan stopped")
                // if (userVisibleHint) setNotificationListener()
            }
        }
    }
}