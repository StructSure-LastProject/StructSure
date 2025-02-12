package fr.uge.structsure.startScan.domain

import android.os.SystemClock
import android.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

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
                Log.d("TimedBuffer", "Executing task on value ${it.key}")
                task(this, it.key)
                entries.remove(it.key)
            } else {
                Log.d("TimedBuffer", "Value " + it.key + " skipped (deleted)")
            }
        }
    }

    /**
     * Executor that will run a given task in loop with a fixed delay
     * @param delay the time in milliseconds between two run
     * @param task the action to run each time
     */
    class TaskLoopRunner(private val delay: Long, private val task: () -> Unit) {

        private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        private var runningTask: ScheduledFuture<*>? = null
        private companion object {
            const val LOG_TAG_TEST_SCAN = "TaskLoopRunner"
        }

        /**
         * Starts the loop (no effect if the runner is already started)
         */
        fun start() {
            if (runningTask != null) return
            Log.d(LOG_TAG_TEST_SCAN, "Starting TaskLoopRunner")
            runningTask = executor.scheduleWithFixedDelay(task, delay, delay, TimeUnit.MILLISECONDS)
        }

        /**
         * Interrupt the loop if the runner has been started before
         */
        fun stop() {
            if (runningTask == null) return
            // executor.shutdown()
            try {
                runningTask?.let {
                    it.cancel(true)
                } ?: Log.w("TimedBuffer", "No running task to cancel")
            } catch (e: InterruptedException) {
                Log.e("TimedBuffer", "Interrupted while stopping task: ${e.message}")
            }
            runningTask = null
        }
    }
}