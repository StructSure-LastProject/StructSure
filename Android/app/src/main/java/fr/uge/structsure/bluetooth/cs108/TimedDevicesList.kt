package fr.uge.structsure.bluetooth.cs108

import android.os.SystemClock
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.csl.cslibrary4a.ReaderDevice
import fr.uge.structsure.components.TaskLoopRunner

/**
 * Buffer that stores element for a specified amount of time before
 * flushing to old items if they have not been updated quickly enough
 *
 * The goal of this class is to track detected nearby devices, including
 * an error margin in the detection system
 */
class TimedDevicesList(private val timeout: Long) {

    /** Task that remove too old values periodically */
    private val runner = TaskLoopRunner(500) { flush() }

    /** Internal list of values with their associated entrance time */
    private val timedDevices = mutableStateMapOf<ReaderDevice, Long>()

    /** Set to track device currently in the connection process */
    private val connectingDevice = mutableStateOf<String?>(null)

    /** List of active values */
    val devices: State<List<ReaderDevice>> = derivedStateOf { timedDevices.keys.toList() }

    /**
     * Adds the given element to the buffer if not already present.
     * @param element the value to store
     */
    fun add(element: ReaderDevice) {
        timedDevices[element] = SystemClock.uptimeMillis()
        runner.start()
    }

    /**
     * Removes the given element to the buffer if present.
     * @param element the value to remove
     */
    fun remove(element: ReaderDevice) {
        timedDevices.remove(element)
        if (connectingDevice.value == element.address) {
            connectingDevice.value = null
        }
    }


    /**
     * Marks the given device as connecting. Useful to avoid
     * connecting to the same device multiple times.
     * @param deviceAddress the address of the device to mark as connecting
     */
    fun markAsConnecting(deviceAddress: String) {
        connectingDevice.value = deviceAddress
    }

    /**
     * Indicates if the given device is currently connecting.
     * @param device the device to check
     * @return true if the device is connecting, false otherwise
     */
    fun isConnecting(device: ReaderDevice) = device.address == connectingDevice.value

    /**
     * Stops the timer. Useful to economise resources while the buffer
     * is no longer used.
     */
    fun stop() {
        runner.stop()
    }

    /**
     * Walk through all the elements to remove all element older than
     * the timeout.
     */
    private fun flush() {
        val now = SystemClock.uptimeMillis()
        val toRemove = timedDevices.entries.filter {
            !it.key.isConnected &&
            it.key.address != connectingDevice.value &&
            now - it.value > timeout
        }
        toRemove.forEach {
            timedDevices.remove(it.key)
        }
    }
}