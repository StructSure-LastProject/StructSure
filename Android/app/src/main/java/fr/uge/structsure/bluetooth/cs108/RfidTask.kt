package fr.uge.structsure.bluetooth.cs108

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import com.csl.cslibrary4a.ReaderDevice
import com.csl.cslibrary4a.RfidReaderChipData
import com.csl.cslibrary4a.RfidReaderChipData.Rx000pkgData
import fr.uge.structsure.MainActivity
import java.text.SimpleDateFormat
import java.util.Date

class RfidTask(
    private val context: Context,
    private var extra1Bank: Int = -1,
    private var extra2Bank: Int = -1,
    private var data1_count: Int = 0,
    private var data2_count: Int = 0,
    private var data1_offset: Int = 0,
    private var data2_offset: Int = 0,
    private var invalidRequest: Boolean = false,
//    private var tagsList: ArrayList<ReaderDevice> = ArrayList(),
    private var strMdid: String? = null
) : AsyncTask<Void, String, String>() {

    init {
        MainActivity.csLibrary4A.appendToLog("data1_count = $data1_count, data2_count = $data2_count, extra1Bank = $extra1Bank, extra2Bank = $extra2Bank")
        inventoryHandler_setup()
    }

    val DEBUG: Boolean = false

    enum class TaskCancelRReason {
        NULL, INVALD_REQUEST, DESTORY, STOP, BUTTON_RELEASE, TIMEOUT, RFID_RESET, ERROR
    }

    private val bAdd2End = false
    val endingRequest: Boolean = true

    var taskCancelReason: TaskCancelRReason? = null
    var bSgtinOnly: Boolean = false
    var bProtectOnly: Boolean = false

    private var tagsIndexList: ArrayList<TagsIndex> = ArrayList<TagsIndex>()

    var mDid: String? = null

    val invalidDisplay: Boolean = false
    private var total = 0
    private var allTotal: Int = 0
    private var yield = 0
    private var yield4RateCount: Int = 0
    private var yieldRate: Int = 0
    var rssi: Double = 0.0
    var phase: Int = 0
    var chidx: Int = 0
    var port: Int = -1
    var portstatus: Int = 0
    var backport1: Int = 0
    var backport2: Int = 0
    var codeSensor: Int = 0
    var codeRssi: Int = 0
    var codeTempC: Float = 0f
    val INVALID_CODEVALUE: Int = -500
    var brand: String? = null
    var timeMillis: Long = 0
    var startTimeMillis: Long = 0
    var runTimeMillis: Long = 0
    var firstTime: Long = 0
    var lastTime: Long = 0
    var continousRequest: Boolean = false
    var batteryCountInventory_old: Int = 0

    var strEpcOld: String = ""
    private val rx000pkgDataArrary = java.util.ArrayList<Rx000pkgData>()
    private var endingMessaage: String? = null

    var notificationData: ByteArray? = null
    var firstTimeOld: Long = 0
    var totalOld: Int = 0
    var bGotTagRate: Boolean = false

    fun inventoryHandler_setup() {
        total = 0
        allTotal = 0
        yield = 0
        if (tagsList != null) {
            yield = tagsList!!.size
            for (i in 0 until yield) {
                allTotal += tagsList!![i].count
            }
            MainActivity.csLibrary4A.appendToLog("yield = $yield, allTotal = $allTotal")
        }
        MainActivity.csLibrary4A.clearInvalidata()

        timeMillis = System.currentTimeMillis()
        startTimeMillis = System.currentTimeMillis()
        runTimeMillis = startTimeMillis
        firstTime = 0
        lastTime = 0

        taskCancelReason = TaskCancelRReason.NULL
        if (invalidRequest) {
            cancel(true)
            taskCancelReason = TaskCancelRReason.INVALD_REQUEST
            Toast.makeText(context, "Invalid Request.", Toast.LENGTH_SHORT).show()
        }
//        MainActivity.mSensorConnector.mLocationDevice.turnOn(true)
//        MainActivity.mSensorConnector.mSensorDevice.turnOn(true)
        println("Should have turn location and sensor ON")
    }

    override fun doInBackground(vararg a: Void?): String {
        var ending = false
        var triggerReleased = false
        var triggerReleaseTime: Long = 0
        var rx000pkgData: Rx000pkgData? = null
        while (MainActivity.csLibrary4A.onRFIDEvent() != null) { } //clear up possible message before operation

        while (MainActivity.csLibrary4A.isBleConnected && !isCancelled && !ending && !MainActivity.csLibrary4A.isRfidFailure) {
            val batteryCount = MainActivity.csLibrary4A.batteryCount
            if (batteryCountInventory_old != batteryCount) {
                batteryCountInventory_old = batteryCount
                publishProgress("VV")
            }
            if (System.currentTimeMillis() > runTimeMillis + 1000) {
                runTimeMillis = System.currentTimeMillis()
                publishProgress("WW")
            }

            notificationData = MainActivity.csLibrary4A.onNotificationEvent()
            rx000pkgData = MainActivity.csLibrary4A.onRFIDEvent()
            if (rx000pkgData != null && MainActivity.csLibrary4A.mrfidToWriteSize() == 0) {
                if (rx000pkgData.responseType == null) {
                    publishProgress("null response")
                } else if (rx000pkgData.responseType == RfidReaderChipData.HostCmdResponseTypes.TYPE_18K6C_INVENTORY) {
                    run {
                        if (rx000pkgData.decodedError != null) publishProgress(rx000pkgData.decodedError)
                        else {
                            if (firstTime == 0L) firstTime = rx000pkgData.decodedTime
                            else lastTime = rx000pkgData.decodedTime
                            rx000pkgDataArrary.add(rx000pkgData)
                            publishProgress(null, "", "")
                        }
                    }
                } else if (rx000pkgData.responseType == RfidReaderChipData.HostCmdResponseTypes.TYPE_18K6C_INVENTORY_COMPACT) {
                    run {
                        if (rx000pkgData.decodedError != null) publishProgress(rx000pkgData.decodedError)
                        else {
                            if (firstTime == 0L) firstTime = rx000pkgData.decodedTime
                            rx000pkgDataArrary.add(rx000pkgData)
                            publishProgress(null, "", "")
                        }
                    }
                } else if (rx000pkgData.responseType == RfidReaderChipData.HostCmdResponseTypes.TYPE_ANTENNA_CYCLE_END) {
                    timeMillis = System.currentTimeMillis()
                } else if (rx000pkgData.responseType == RfidReaderChipData.HostCmdResponseTypes.TYPE_COMMAND_ABORT_RETURN) {
                    MainActivity.csLibrary4A.appendToLog("AAA: Abort return is received !!!")
                    ending = true
                } else if (rx000pkgData.responseType == RfidReaderChipData.HostCmdResponseTypes.TYPE_COMMAND_END) {
                    if (rx000pkgData.decodedError != null) endingMessaage =
                        rx000pkgData.decodedError
                    if (continousRequest) {
                        MainActivity.csLibrary4A.batteryLevelRequest()
                        MainActivity.csLibrary4A.startOperation(RfidReaderChipData.OperationTypes.TAG_INVENTORY_COMPACT)
                    } else ending = true
                }
            }

            //suspend the current thread up to 5 seconds until all the commands on the output buffer got sent out
            val toCnt = System.currentTimeMillis()
            if (MainActivity.csLibrary4A.mrfidToWriteSize() != 0) {
                while (System.currentTimeMillis() - toCnt < 50000 && MainActivity.csLibrary4A.mrfidToWriteSize() != 0) {
                    try {
                        Thread.sleep(200)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                MainActivity.csLibrary4A.appendToLog(String.format("InventoryRfidTask: send commands elapsed time: %d", System.currentTimeMillis() - toCnt))
                timeMillis = System.currentTimeMillis()
            }
            if (!triggerReleased && taskCancelReason == TaskCancelRReason.BUTTON_RELEASE) {
                triggerReleased = true
                triggerReleaseTime = System.currentTimeMillis()
                //taskCancelReason = TaskCancelRReason.NULL;
                MainActivity.csLibrary4A.appendToLog("AAA: release is triggered !!!")
            }
            if (taskCancelReason != TaskCancelRReason.NULL) {
                MainActivity.csLibrary4A.abortOperation()
                publishProgress("XX")
                timeMillis = 0
                val endStatus = true
                cancel(true)
            } else if (triggerReleased && (System.currentTimeMillis() > (triggerReleaseTime + 2000))) {
                MainActivity.csLibrary4A.appendToLog("AAA: triggerRelease Timeout !!!")
                taskCancelReason = TaskCancelRReason.BUTTON_RELEASE
            }
        }
        var stringReturn = "End of Asynctask()"
        if (!MainActivity.csLibrary4A.isBleConnected) stringReturn =
            "isBleConnected is false"
        else if (isCancelled) stringReturn = "isCancelled is true"
        else if (MainActivity.csLibrary4A.isRfidFailure) stringReturn = "isRfidFailure is true"
        else if (ending) stringReturn =
            (if (rx000pkgData == null) "null ending" else (rx000pkgData.responseType.toString() + " ending"))
        return stringReturn
    }

    override fun onProgressUpdate(vararg output: String?) {
        val out = output[0]
        if (out == null) tagHandler()
        else if (out.length == 1) inventoryHandlerEndReason()
        else if ("XX" == out) println("CANCELLING: PostProgressUpdate sent abortOperation")
        else if ("WW" == out) println("Request to update voltage display ignored")
        else if ("VV" == out) println("Request to update voltage value ignored")
    }

    private fun inventoryHandlerEndReason() {
        val message = when (taskCancelReason) {
            TaskCancelRReason.STOP -> "Stop button pressed"
            TaskCancelRReason.BUTTON_RELEASE -> "Trigger Released"
            TaskCancelRReason.TIMEOUT -> "Time Out"
            TaskCancelRReason.ERROR -> "Inventory Notification Error code A101: " + MainActivity.csLibrary4A.byteArrayToString(notificationData)
            else -> taskCancelReason!!.name
        }
        println("POPUP: $message")
    }

    fun tagHandler() {
        var currentTime: Long = 0
        while (rx000pkgDataArrary.size != 0) {
            val rx000pkgData = rx000pkgDataArrary[0]
            rx000pkgDataArrary.removeAt(0)
            if (rx000pkgData == null) {
                if (DEBUG) println("InventoryRfidTask: null rx000pkgData !!!")
                continue
            }


            var match = false
            var updated = false
            currentTime = rx000pkgData.decodedTime
            val iFlag = rx000pkgData.flags
            val strPc = MainActivity.csLibrary4A.byteArrayToString(rx000pkgData.decodedPc)
            if (strPc.length != 4) {
                if (DEBUG) println("InventoryRfidTask: !!! rx000pkgData.Pc length = " + strPc.length)
                continue
            }
            var extraLength = 0
            if (extra1Bank != -1 && rx000pkgData.decodedData1 != null) extraLength += rx000pkgData.decodedData1.size
            if (extra2Bank != -1 && rx000pkgData.decodedData2 != null) extraLength += rx000pkgData.decodedData2.size
            if (extraLength != 0) {
                val decodedEpcNew = ByteArray(rx000pkgData.decodedEpc.size - extraLength)
                System.arraycopy(
                    rx000pkgData.decodedEpc,
                    0,
                    decodedEpcNew,
                    0,
                    decodedEpcNew.size
                )
                rx000pkgData.decodedEpc = decodedEpcNew
            }
            var strEpc = MainActivity.csLibrary4A.byteArrayToString(rx000pkgData.decodedEpc)
            if (DEBUG) println("HelloC: decodePc = " + strPc + ", decodedEpc = " + strEpc + ", iFlags = " + String.format("%2X", iFlag))
            portstatus = INVALID_CODEVALUE
            backport1 = INVALID_CODEVALUE
            backport2 = INVALID_CODEVALUE
            codeSensor = INVALID_CODEVALUE
            codeRssi = INVALID_CODEVALUE
            codeTempC = INVALID_CODEVALUE.toFloat()
            brand = null
            var strExtra2: String? = null
            if (rx000pkgData.decodedData2 != null) strExtra2 =
                MainActivity.csLibrary4A.byteArrayToString(rx000pkgData.decodedData2)
            if (strExtra2 != null && strMdid != null) {
                if (DEBUG) println("HelloK: strExtra2 = $strExtra2, strMdid = $strMdid")
                if (strMdid!!.contains("E200B0")) portstatus =
                    strExtra2.substring(3, 4).toInt(16)
            }
            var strExtra1: String? = null
            if (rx000pkgData.decodedData1 != null) {
                strExtra1 =
                    MainActivity.csLibrary4A.byteArrayToString(rx000pkgData.decodedData1)
                if (strMdid != null && strExtra1 != null && strExtra2 != null) {
                    println("strExtra1 = $strExtra1, strExtra2 = $strExtra2")
                    decodeMicronData(strExtra1, strExtra2)
                }
            }
            var strAddresss = strEpc
            var strCrc16: String? = null
            if (rx000pkgData.decodedCrc != null) strCrc16 =
                MainActivity.csLibrary4A.byteArrayToString(rx000pkgData.decodedCrc)

            val extra1Bank = this.extra1Bank
            val data1_offset = this.data1_offset

            if (strMdid != null) {
                if (strMdid!!.indexOf("E203510") == 0) {
                    if (strEpc.length == 24 && strExtra2 != null) {
                        codeTempC = MainActivity.csLibrary4A.decodeCtesiusTemperature(
                            strEpc.substring(
                                16,
                                24
                            ), strExtra2
                        )
                        strEpc = strEpc.substring(0, 16)
                        strAddresss = strEpc
                    }
                } else if (strMdid!!.indexOf("E283A") == 0) {
                    println("E283A is found with extra1Bank = $extra1Bank, strExtra1 = $strExtra1, extra2Bank = $extra2Bank, strExtra2 = $strExtra2")
                    if (strExtra2 != null && strExtra2.length >= 28) codeTempC =
                        MainActivity.csLibrary4A.decodeAsygnTemperature(strExtra2)
                }
            }

            var bFastId = false
            val bTempId = false
            if (mDid != null) {
                println("mDid = " + mDid)
                if (mDid!!.indexOf("E28011") == 0) {
                    val iValue: Int = mDid!!.substring("E28011".length).toInt()
                    println(
                        String.format(
                            "iValue = 0x%02X",
                            iValue
                        )
                    )
                    if ((iValue and 0x20) != 0) bFastId = true
                    if (DEBUG) println(
                        "HelloK: iValue = " + String.format(
                            "%02X",
                            iValue
                        )
                    )
                }
            } else if (MainActivity.csLibrary4A.fastId > 0) bFastId = true
            if (DEBUG) println(("HelloK: strMdid = " + strMdid + ", MainMdid = " + mDid).toString() + ", bFastId = " + bFastId)

            val iPc = strPc.toInt(16)
            var strXpc: String? = null
            var iSensorData = ReaderDevice.INVALID_SENSORDATA
            if ((iPc and 0x0200) != 0 && strEpc != null && strEpc.length >= 8) {
                println("strPc = $strPc, strEpc = $strEpc")
                val iXpcw1 = strEpc.substring(0, 4).toInt(16)
                if ((iXpcw1 and 0x8000) != 0) {
                    strXpc = strEpc.substring(0, 8)
                    strEpc = strEpc.substring(8)
                    strAddresss = strEpc
                    if (strMdid != null) {
                        if (strMdid!!.indexOf("E280B12") == 0) {
                            val iXpcw2 = strXpc.substring(4, 8).toInt(16)
                            if ((iXpcw1 and 0x8100) != 0 && (iXpcw2 and 0xF000) == 0) {
                                if ((iXpcw2 and 0x0C00) == 0x0C00) {
                                    //iXpcw2 |= 0x200;
                                    iSensorData = iXpcw2 and 0x1FF
                                    if ((iXpcw2 and 0x200) != 0) {
                                        iSensorData = iSensorData xor 0x1FF
                                        iSensorData++
                                        iSensorData = -iSensorData
                                        //println(String.format("Hello123: iXpcw2 = %04X, iSensorData = %d", iXpcw2, iSensorData ));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    strXpc = strEpc.substring(0, 4)
                    strEpc = strEpc.substring(4)
                    strAddresss = strEpc
                }
            }

            if (bFastId) {
                var strEpc1: String? = null
                var strTid: String? = null
                var bValidFastId = false
                if (strEpc.length > 24) {
                    strEpc1 = strEpc.substring(0, strEpc.length - 24)
                    strTid = strEpc.substring(strEpc.length - 24, strEpc.length)
                    if (strTid.indexOf("E28011") == 0 || strTid.indexOf("E2C011") == 0) {
                        strEpc = strEpc1
                        strAddresss = strEpc
                        strExtra2 = strTid
                        extra2Bank = 2
                        data2_offset = 0
                        bValidFastId = true
                    }
                }
                if (bValidFastId == false) return
                if (DEBUG) println("HelloK: Doing IMPINJ Inventory  with strMdid = $strMdid, strEpc1 = $strEpc1:, strTid = $strTid")
            } else if (mDid != null) {
                if (DEBUG) println("HelloK: MainActivity.mDid = " + mDid)
                if (mDid == "E2806894B") {
                    if (strEpc.length >= 24) {
                        val strEpc1 = strEpc.substring(0, strEpc.length - 24)
                        val strTid = strEpc.substring(strEpc.length - 24, strEpc.length)
                        if (DEBUG) println("HelloK: matched E2806894B with strEpc = $strEpc, strEpc1 = $strEpc1, strTid = $strTid, strExtra1 = $strExtra1")
                        var matched = true
                        if (strExtra1 != null) {
                            if (!(strExtra1.length == 8 && strTid.contains(strExtra1))) matched =
                                false
                        }
                        if (matched) {
                            strEpc = strEpc1
                            strExtra2 = strTid
                            extra2Bank = 2
                            data2_offset = 0
                        }
                    }
                } else if (mDid == "E2806894C" || mDid =="E2806894d") {
                    if (strEpc.length >= 4) {
                        val strEpc1 = strEpc.substring(0, strEpc.length - 4)
                        val strBrand = strEpc.substring(strEpc.length - 4, strEpc.length)
                        if (DEBUG) println("HelloK: matched E2806894B with strEpc = $strEpc, strEpc1 = $strEpc1, strBrand = $strBrand, strExtra1 = $strExtra1")
                        strEpc = strEpc1
                        brand = strBrand
                        if (DEBUG) println("HelloK: brand 1 = $brand, strEpc = $strEpc")
                    }
                }
            }

            if (DEBUG || true) println(("strMdid = " + strMdid + ", strTidCompared = " + strMdid + ", MainActivity.mDid = " + mDid).toString() + ", strExtra1 = " + strExtra1 + ", strExtra2 = " + strExtra2)
            if (strMdid != null) {
                var strTidCompared: String = strMdid!!
                if (strTidCompared.indexOf("E28011") == 0) {
                    val iValue: Int = mDid!!.substring("E28011".length).toInt()
                    println(String.format("iValue = 0x%02X", iValue))
                    strTidCompared = if ((iValue and 0x40) != 0) "E2C011"
                    else if ((iValue and 0x80) != 0) "E280117"
                    else "E28011"
                }
                println("strTidCompared = $strTidCompared")
                if (strTidCompared == "E28011") {
                } else if (strTidCompared == "E2806894" && mDid == "E2806894C") {
                } else if (strTidCompared == "E281D") {
                } else if (strTidCompared == "E282402") {
                } else if (strTidCompared == "E282403") {
                } else if (strTidCompared == "E282405") {
                } else { //if (strMdid.matches("E280B0"))
                    var bMatched = false
                    if (strExtra1 != null && strExtra1.indexOf(strTidCompared) == 0) {
                        bMatched = true
                        if (DEBUG) println("strExtra1 contains strTidCompared")
                    } else if (strExtra2 != null && strExtra2.indexOf(strTidCompared) == 0) {
                        bMatched = true
                        if (DEBUG) println("strEXTRA2 contains strTidCompared")
                    }
                    println("bMatched = $bMatched")
                    if (bMatched == false) return
                }
            }

            rssi = rx000pkgData.decodedRssi
            phase = rx000pkgData.decodedPhase
            chidx = rx000pkgData.decodedChidx
            port = rx000pkgData.decodedPort

            timeMillis = System.currentTimeMillis()

            var rssiGeiger = rssi
            if (MainActivity.csLibrary4A.rssiDisplaySetting !== 0) rssiGeiger -= MainActivity.csLibrary4A.dBuV_dBm_constant
            System.out.printf("Display geigerTagRssiView = %.1f%n", rssiGeiger)
            System.out.printf("Display geigerTagGotView = %s%n", strEpc)

            if (tagsList == null) {
                if (strEpc.matches(strEpcOld.toRegex())) {
                    match = true
                    updated = true
                }
            }
            var bAddDevice = true
            var strValue: String? = null
            if (bSgtinOnly) {
                strValue = MainActivity.csLibrary4A.getUpcSerial(strEpc)
                println(
                    "bSgtinOnly = $bSgtinOnly, strValue = " + (strValue
                        ?: "null")
                )
                if (strValue == null) bAddDevice = false
            } else if (bProtectOnly) {
                if (strExtra1 != null) {
                    bAddDevice = false
                    strValue = strExtra1.substring(strExtra1.length - 1)
                    val iValue = strValue.toInt(16)
                    println(
                        "bProtectOnly = $bProtectOnly, strExtra1 = " + (strExtra1
                            ?: "null") + ", iValue = " + iValue
                    )
                    if ((iValue and 0x02) != 0) bAddDevice = true
                } else println("NULL strExtra1")
            }
            if (bAddDevice == false) {
            } else if (match == false) {
                if (tagsList == null) {
                    strEpcOld = strEpc
                    updated = true
                } else {
                    println("HelloK: New Epc = $strEpc, brand = $brand")
                    val readerDevice = ReaderDevice(
                        "",
                        strEpc,
                        false,
                        null,
                        strPc,
                        strXpc,
                        strCrc16,
                        strMdid,
                        strExtra1,
                        extra1Bank,
                        data1_offset,
                        strExtra2,
                        extra2Bank,
                        data2_offset,
                        SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(Date()),
                        SimpleDateFormat("z").format(
                            Date()
                        ).replace("GMT".toRegex(), ""),
//                                MainActivity.mSensorConnector.mLocationDevice.getLocation(),
//                                MainActivity.mSensorConnector.mSensorDevice.getEcompass(),
                        null,
                        null,
                        1,
                        rssi,
                        phase,
                        chidx,
                        port,
                        portstatus,
                        backport1,
                        backport2,
                        codeSensor,
                        codeRssi,
                        codeTempC,
                        brand,
                        iSensorData
                    )
                    if (bSgtinOnly && strValue != null) readerDevice.upcSerial = strValue
                    if (strMdid != null) {
                        if (strMdid!!.indexOf("E282402") == 0) readerDevice.codeSensorMax =
                            0x1F
                        else readerDevice.codeSensorMax = 0x1FF
                    }
                    if (bAdd2End) tagsList!!.add(readerDevice)
                    else tagsList!!.add(0, readerDevice)
                    val tagsIndex = TagsIndex(strAddresss, tagsList!!.size - 1)
                    tagsIndexList.add(tagsIndex)
                    tagsIndexList.sort()
                }
                yield++
                yield4RateCount++
                updated = true
            }
            if (updated && bAddDevice) {
                total++
                allTotal++
            }
        }
        println("Notify state changed")
        if (invalidDisplay) {
            System.out.printf("Display value for rfidYieldView: %s, %d%n", total, MainActivity.csLibrary4A.validata)
            System.out.printf("Display value for rfidRateView: %d, %d%n", MainActivity.csLibrary4A.validata, MainActivity.csLibrary4A.invalidUpdata)
        } else {
            var stringTemp = "Unique:$yield"
            if (true) {
                val fErrorRate =
                    MainActivity.csLibrary4A.invalidata.toFloat() / (MainActivity.csLibrary4A.validata.toFloat() + MainActivity.csLibrary4A.invalidata.toFloat()) * 100
                stringTemp += """
            
            E${java.lang.String.valueOf(MainActivity.csLibrary4A.invalidata)}/${
                    java.lang.String.valueOf(
                        MainActivity.csLibrary4A.validata
                    )
                }/${fErrorRate.toInt()}
            """.trimIndent()
            } else if (true) {
                stringTemp += """
            
            E${java.lang.String.valueOf(MainActivity.csLibrary4A.invalidata)},${
                    java.lang.String.valueOf(
                        MainActivity.csLibrary4A.invalidUpdata
                    )
                }/${java.lang.String.valueOf(MainActivity.csLibrary4A.validata)}
            """.trimIndent()
            }
            System.out.printf("Display value for rfidYieldView: %s%n", stringTemp)
            if (total != 0 && currentTime - firstTimeOld > 500) {
                if (firstTimeOld == 0L) firstTimeOld = firstTime
                if (totalOld == 0) totalOld = total
                var strRate = "Total:$allTotal\n"

                if (firstTimeOld != 0L) {
                    var tagRate = MainActivity.csLibrary4A.tagRate
                    var tagRate2: Long = -1
                    if (currentTime > firstTimeOld) tagRate2 =
                        totalOld * 1000 / (currentTime - firstTimeOld)
                    if (tagRate >= 0 || bGotTagRate) {
                        bGotTagRate = true
                        strRate += "$yieldRate/"
                        strRate += (if (tagRate != -1L) tagRate.toString() else "___") + "/" + tagRate2.toString()
                    } else {
                        if (lastTime == 0L) {
                            tagRate = MainActivity.csLibrary4A.streamInRate / 17
                            strRate += "rAte"
                        } else if (currentTime > firstTimeOld) {
                            tagRate = totalOld * 1000 / (currentTime - firstTimeOld)
                            strRate += "Rate"
                        }
                        strRate += ":$yieldRate/$tagRate"
                    }
                }

                System.out.printf("Display value for rfidRateView: %s%n", strRate)
                //if (lastTime - firstTime > 1000) {
                firstTimeOld = currentTime
                totalOld = total
                total = 0
                //}
            } else { }
        }
    }

    override fun onCancelled() {
        super.onCancelled()
        DeviceConnectTask4InventoryEnding(taskCancelReason!!)
    }

    override fun onPostExecute(result: String) {
        DeviceConnectTask4InventoryEnding(taskCancelReason!!)
    }

    fun DeviceConnectTask4InventoryEnding(taskCancelReason: TaskCancelRReason) {
        println("CANCELLING: TaskEnding sent abortOperation again with taskCancelReason = $taskCancelReason")
        MainActivity.csLibrary4A.abortOperation() //added in case previous command end is received with inventory stopped
        println("INVENDING: Ending with endingRequest = $endingRequest")
        println("Notify state changed")
        if (endingRequest) {
            when (taskCancelReason) {
                TaskCancelRReason.NULL -> println("TOAST Finish as COMMAND END is received")
                TaskCancelRReason.STOP -> println("Finished as STOP is pressed")
                TaskCancelRReason.BUTTON_RELEASE -> println("Finish as BUTTON is released")
                TaskCancelRReason.TIMEOUT -> println("Finish as TIMEOUT")
                TaskCancelRReason.RFID_RESET -> println("Finish as RFID RESET")
                TaskCancelRReason.INVALD_REQUEST -> println("Invalid sendHostRequest. Operation is cancelled")
                else -> println("Finish reason as $taskCancelReason")
            }
            println("INVENDING: Toasting")
        }
        println("INFO RfidTask stopped")
        if (endingMessaage != null) {
            println(endingMessaage)
        }
//        MainActivity.mSensorConnector.mLocationDevice.turnOn(false)
//        MainActivity.mSensorConnector.mSensorDevice.turnOn(false)
        println("Should have turn location and sensor OFF")
    }

    fun decodeMicronData(strActData: String?, strCalData: String?): String? {
        var strActData = strActData
        var iTag35 = -1
        if (strMdid!!.contains("E282402")) iTag35 = 2
        else if (strMdid!!.contains("E282403")) iTag35 = 3
        else if (strMdid!!.contains("E282405")) iTag35 = 5
        if (iTag35 < 2) return ""

        if (iTag35 == 5) {
            backport1 = strActData!!.substring(0, 4).toInt(16)
            backport2 = strActData.substring(4, 8).toInt(16)
            println("backport1 = $backport1, backport2 = $backport2")
            strActData = strActData.substring(8)
        }
        var iSensorCode = strActData!!.substring(0, 4).toInt(16)
        iSensorCode = iSensorCode and 0x1FF
        if (iTag35 == 2) iSensorCode = iSensorCode and 0x1F
        codeSensor = iSensorCode
        var iRssi: Int
        val strRetValue = ""
        if (iTag35 == 2) {
            iRssi = strCalData!!.substring(0, 4).toInt(16)
            iRssi = iRssi and 0x1F
            codeRssi = iRssi
        } else if (iTag35 == 3) {
            iRssi = strActData.substring(4, 8).toInt(16)
            iRssi = iRssi and 0x1F
            codeRssi = iRssi

            if (strActData.length < 8) return null
            codeTempC = MainActivity.csLibrary4A.decodeMicronTemperature(
                iTag35,
                strActData.substring(8, 12),
                strCalData
            )
        } else if (iTag35 == 5) {
            iRssi = strActData.substring(4, 8).toInt(16)
            iRssi = iRssi and 0x1F
            codeRssi = iRssi

            codeTempC = MainActivity.csLibrary4A.decodeMicronTemperature(
                iTag35,
                strActData.substring(8, 12),
                strCalData
            )
        }
        return ""
    }


    companion object {
        private var tagsList: ArrayList<ReaderDevice> = ArrayList()

        fun startScan(context: Context) {
            println("startInventoryTask")
            var extra1Bank = -1
            var extra2Bank = -1
            var extra1Count = 0
            var extra2Count = 0
            var extra1Offset = 0
            var extra2Offset = 0
            var mDid: String? = null
            var inventoryRfidTask: RfidTask? = null

            println(
                ("Rin: mDid = " + (mDid
                    ?: "null") + ", MainActivity.mDid = " + mDid).toString() + ", bMultiBankInventory = " + false
            )

            println("bMultiBank = false with extra1Bank = $extra1Bank,$extra1Offset,$extra1Count, extra2Bank = $extra2Bank,$extra2Offset,$extra2Count")
            var bMultiBank = false
            if (bMultiBank == false) {
                MainActivity.csLibrary4A.restoreAfterTagSelect()
                inventoryRfidTask = RfidTask(
                    context, -1, -1, 0, 0, 0, 0,
                    false, mDid
                )
                inventoryRfidTask.bSgtinOnly = false
                MainActivity.csLibrary4A.startOperation(RfidReaderChipData.OperationTypes.TAG_INVENTORY_COMPACT)
            } else {
                val inventoryUcode8_bc = mDid != null && mDid == "E2806894" && mDid != null && (mDid == "E2806894B" || mDid == "E2806894C")
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
                    var needResetData = false
                    println(("HelloK: mDid = " + mDid + ", MainActivity.mDid = " + mDid).toString() + " with extra1Bank = " + extra1Bank + "," + extra1Offset + "," + extra1Count + ", extra2Bank = " + extra2Bank + "," + extra2Offset + "," + extra2Count)
                    if (mDid != null) MainActivity.csLibrary4A.setResReadNoReply(mDid.matches("E281D".toRegex()))
                    if (inventoryUcode8_bc == false) {
                        println("BleStreamOut: Set Multibank")
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
                }
                println("startInventoryTask: going to startOperation with extra1Bank = $extra1Bank,$extra1Offset,$extra1Count, extra2Bank = $extra2Bank,$extra2Offset,$extra2Count")
                inventoryRfidTask = RfidTask(
                    context, extra1Bank, extra2Bank, extra1Count, extra2Count, extra1Offset, extra2Offset,
                    false, mDid
                )
                inventoryRfidTask.bProtectOnly = false
                if (inventoryUcode8_bc) MainActivity.csLibrary4A.startOperation(RfidReaderChipData.OperationTypes.TAG_INVENTORY_COMPACT)
                else MainActivity.csLibrary4A.startOperation(RfidReaderChipData.OperationTypes.TAG_INVENTORY)
            }
            inventoryRfidTask.execute()
        }
    }
}