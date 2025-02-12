package fr.uge.structsure.scanPage.domain

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class TimedBufferTests {
    @Test
    fun valuesRemovesAfterDelay() {
        val result = AtomicInteger(0)
        val buffer = TimedBuffer { buf, s ->
            if (s == "abc" && buf.contains("def")) result.getAndSet(1)
            else result.getAndSet(2)
        }

        /* Put values with delay */
        buffer.add("abc")
        Thread.sleep(500)
        buffer.add("def")
        Thread.sleep(750)
        assertEquals(1, result.get())
        assertFalse(buffer.contains("abc"))
        assertFalse(buffer.contains("def"))
    }

    @Test
    fun valuesRemovesAfterDelayLate() {
        val result = AtomicInteger(0)
        val buffer = TimedBuffer { buf, s ->
            if (s == "A" && buf.contains("B")) result.incrementAndGet()
            else if (s == "B" && buf.contains("A")) result.incrementAndGet()
            else result.getAndSet(3)
        }

        /* Put values with delay */
        buffer.add("A")
        buffer.add("B")
        Thread.sleep(1500)
        assertEquals(1, result.get())
        // 0: Flush never happen
        // 1: Ok!
        // 2: Sensor read twice (for tag A and tag B)
        // 3: Not all values got removed as expected
        assertFalse(buffer.contains("A"))
        assertFalse(buffer.contains("B"))
    }

    @Test
    fun canStop() {
        val result = AtomicInteger(0)
        val buffer = TimedBuffer<String> { _, _ ->
            result.getAndSet(1)
        }

        /* Put values with delay */
        buffer.add("A")
        buffer.add("B")
        buffer.stop()
        Thread.sleep(1500)
        assertEquals(0, result.get())
    }

    @Test
    fun canResume() {
        val result = AtomicInteger(0)
        val buffer = TimedBuffer { buf, s ->
            if (s == "A" && buf.contains("B")) result.incrementAndGet()
            else if (s == "B" && buf.contains("A")) result.incrementAndGet()
            else result.set(3)
        }

        /* Put values with delay */
        buffer.add("A")
        buffer.stop()
        Thread.sleep(1500)
        buffer.add("B")
        assertEquals(0, result.get())
        Thread.sleep(1500)
        assertEquals(1, result.get())
    }
}
