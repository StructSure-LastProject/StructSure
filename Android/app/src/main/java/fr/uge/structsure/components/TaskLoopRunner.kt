package fr.uge.structsure.components

import android.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Executor that will run a given task in loop with a fixed delay
 * @param delay the time in milliseconds between two run
 * @param task the action to run each time
 */
class TaskLoopRunner(private val delay: Long, private val task: () -> Unit) {

    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var runningTask: ScheduledFuture<*>? = null
    private companion object {
        const val LOG_TAG = "TaskLoopRunner"
    }

    /**
     * Starts the loop (no effect if the runner is already started)
     */
    fun start() {
        if (runningTask != null) return
        Log.d(LOG_TAG, "Starting TaskLoopRunner")
        runningTask = executor.scheduleWithFixedDelay(task, delay, delay, TimeUnit.MILLISECONDS)
    }

    /**
     * Interrupt the loop if the runner has been started before
     */
    fun stop() {
        if (runningTask == null) return
        // executor.shutdown()
        try {
            runningTask?.cancel(true) ?: Log.w(LOG_TAG, "No running task to cancel")
        } catch (e: InterruptedException) {
            Log.e(LOG_TAG, "Interrupted while stopping task: ${e.message}")
        }
        runningTask = null
    }

    /**
     * Checks if this task is currently running or not
     * @return true if running, false if not or cancelled
     */
    fun isRunning() = runningTask?.isCancelled == false
}