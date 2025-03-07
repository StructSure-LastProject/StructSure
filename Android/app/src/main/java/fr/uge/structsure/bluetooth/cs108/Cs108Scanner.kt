package fr.uge.structsure.bluetooth.cs108

import android.util.Log
import com.csl.cslibrary4a.RfidReaderChipData
import fr.uge.structsure.MainActivity
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
 * Scanner that enables to look for all RFID chips in range of the
 * connected scanner.
 * Note that calling any method of this class with an unconnected or
 * not ready scanner may end in unexpected behavior.
 */
class Cs108Scanner(private val callback: (RfidChip) -> Unit) {
    companion object {
        private const val LOG_TAG = "TinyRFID-Scanner"
        private var task: Job? = null
    }

    /**
     * Launch a background task continuously searching for nearby RFID
     * chips and sending them to the configured callback.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun start() {
        if (task != null) return
        MainActivity.csLibrary4A.startOperation(RfidReaderChipData.OperationTypes.TAG_INVENTORY_COMPACT)
        task = GlobalScope.launch { pollRfid() }
        Log.d(LOG_TAG, "RFID scan started")
    }

    /**
     * Interrupt the chip scan (if started).
     */
    fun stop() {
        if (task == null) return
        MainActivity.csLibrary4A.abortOperation()
        runBlocking {
            task!!.cancelAndJoin()
        }
        clearEventsBuffer()
    }

    fun toggle() {
        if (task == null) start() else stop()
    }

    /**
     * Ask continuously the cs108 library for any freshly read chip
     * and call the callback with each new result.
     */
    private suspend fun pollRfid() {
        try {
            coroutineScope {
                while (true) {
                    ensureActive()
                    val event = MainActivity.csLibrary4A.onRFIDEvent()
                    if (event != null) {
                        callback(RfidChip(MainActivity.csLibrary4A, event))
                    } else {
                        delay(100L)
                    }
                }
            }
        } catch (e: CancellationException) {
            // Interrupted
        }
        Log.d(LOG_TAG, "RFID scan stopped")
        task = null
    }

    /**
     * Consumes all RFID events of the cs library buffer
     */
    private fun clearEventsBuffer() {
        while (true) {
            MainActivity.csLibrary4A.onRFIDEvent() ?: return
        }
    }
}