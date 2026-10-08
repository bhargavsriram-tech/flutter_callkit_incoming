package com.hiennv.flutter_callkit_incoming

import android.os.Bundle
import android.os.SystemClock
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The full-screen ring must close when the call ends elsewhere - the
 * caller hung up, or the app ended it. VSP 1.2.7 tester report
 * IVg4J-FAmQnY (2026-10-08): the caller cancelled, the plugin reported the
 * timeout, and the ring screen stayed up until the user tapped it.
 */
class RingScreenClosesInstrumentedTest {
    @Test
    fun endedBroadcastClosesTheRingScreen() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val data = Bundle().apply {
            putString(CallkitConstants.EXTRA_CALLKIT_ID, "ring-closes")
            putLong(CallkitConstants.EXTRA_CALLKIT_DURATION, 600000L)
            putString(CallkitConstants.EXTRA_CALLKIT_NAME_CALLER, "Test caller")
        }
        val monitor = instrumentation.addMonitor(CallkitIncomingActivity::class.java.name, null, false)
        context.startActivity(CallkitIncomingActivity.getIntent(context, data))
        val activity = instrumentation.waitForMonitorWithTimeout(monitor, 5000)
        instrumentation.removeMonitor(monitor)
        assertNotNull("Incoming activity must open", activity)
        // Not waitForIdleSync: the ring animation keeps the main thread busy,
        // and that wait outlasted the ring and let its own timeout close it.
        SystemClock.sleep(1000)
        val opened = SystemClock.uptimeMillis()

        context.sendBroadcast(CallkitIncomingActivity.getIntentEnded(context, false))

        // Well inside the 10 min ring duration, so only the broadcast can close it.
        val deadline = SystemClock.uptimeMillis() + 5000
        while (!activity.isFinishing && !activity.isDestroyed &&
            SystemClock.uptimeMillis() < deadline) {
            SystemClock.sleep(100)
        }
        assertTrue("Ring screen must close on the ended broadcast",
            activity.isFinishing || activity.isDestroyed)
        assertTrue("Closed by the broadcast, not by its own timeout",
            SystemClock.uptimeMillis() - opened < 10000)
    }
}
