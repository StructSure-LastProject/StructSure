package fr.uge.structsure.scanPage.domain

import android.os.SystemClock
import android.util.Log
import fr.uge.structsure.components.TaskLoopRunner

/**
 * Buffer that stores element for a specified amount of time before
 * flushing to old items and running a callback on them.
 * Can be useful run code on a lot of item a fixed delay.
 *
 * The goal of this class is to convert RFID tags to Sensors by
 * associating tags with each other while letting some time for the
 * scanner to scan all tags.
 */
class TimedBuffer<T>(private val task: (buffer: TimedBuffer<T>, element: T) -> Unit) {

    companion object {
        private const val LOG_TAG = "TimedBuffer"
    }

    /** The delay between two flushes */
    private val timeout: Long = 1000

    /** List of values with their associated entrance time */
    private val runner = TaskLoopRunner(timeout / 2) { collect() }

    /** List of values with their associated entrance time */
    private val entries = mutableMapOf<T, Long>()

    /**
     * Adds the given element to the buffer if not already present.
     * If the element was not present, the delayed task will be
     * scheduled.
     * @param element the value to store
     */
    fun add(element: T) {
        entries.putIfAbsent(element, SystemClock.uptimeMillis())
        runner.start()
    }

    /**
     * Checks if an element currently exists in the buffer and removes
     * it if so.
     * @param element the value to search and remove
     * @return true is the element was removed, false otherwise
     */
    fun contains(element: T): Boolean = entries.remove(element) != null

    /**
     * Stops the timer. Useful to economise resources while the buffer
     * is no longer used.
     */
    fun stop() {
        runner.stop()
    }

    /**
     * Walk through all the elements, remove all element older than
     * the timeout and run the task on each of them.
     */
    private fun collect() {
        val now = SystemClock.uptimeMillis()
        val toRemove = entries.entries.filter { now - it.value > timeout }
        toRemove.forEach {
            if (entries.contains(it.key)) {
                if (!runner.isRunning()) return // stop here
                Log.d(LOG_TAG, "Executing task on value ${it.key}")
                task(this, it.key)
                entries.remove(it.key)
            } else {
                Log.d(LOG_TAG, "Value " + it.key + " skipped (deleted)")
            }
        }
    }
}