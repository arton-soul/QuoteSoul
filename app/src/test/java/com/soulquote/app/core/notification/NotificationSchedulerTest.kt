package com.soulquote.app.core.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class NotificationSchedulerTest {

    @Test
    fun calculateNextTriggerMillis_schedulesToday_whenTargetInFuture() {
        val baseCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val currentMillis = baseCalendar.timeInMillis

        val triggerMillis = NotificationScheduler.calculateNextTriggerMillis(
            hour = 7,
            minute = 0,
            currentMillis = currentMillis
        )

        val triggerCalendar = Calendar.getInstance().apply { timeInMillis = triggerMillis }

        assertEquals(baseCalendar.get(Calendar.DAY_OF_YEAR), triggerCalendar.get(Calendar.DAY_OF_YEAR))
        assertEquals(7, triggerCalendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, triggerCalendar.get(Calendar.MINUTE))
        assertTrue(triggerMillis > currentMillis)
    }

    @Test
    fun calculateNextTriggerMillis_schedulesTomorrow_whenTargetPassed() {
        val baseCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val currentMillis = baseCalendar.timeInMillis

        val triggerMillis = NotificationScheduler.calculateNextTriggerMillis(
            hour = 7,
            minute = 0,
            currentMillis = currentMillis
        )

        val triggerCalendar = Calendar.getInstance().apply { timeInMillis = triggerMillis }

        assertTrue(triggerMillis > currentMillis)
        val expectedDay = (baseCalendar.get(Calendar.DAY_OF_YEAR) % 365) + 1
        assertEquals(expectedDay, triggerCalendar.get(Calendar.DAY_OF_YEAR))
        assertEquals(7, triggerCalendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, triggerCalendar.get(Calendar.MINUTE))
    }
}
