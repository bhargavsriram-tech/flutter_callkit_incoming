package com.hiennv.flutter_callkit_incoming

import android.telecom.CallAudioState
import org.junit.Assert.assertEquals
import org.junit.Test

class SpeakerRouteTest {
    @Test
    fun speakerOffReturnsToBluetoothWhenConnected() {
        val mask = CallAudioState.ROUTE_EARPIECE or CallAudioState.ROUTE_SPEAKER or CallAudioState.ROUTE_BLUETOOTH
        assertEquals(CallAudioState.ROUTE_BLUETOOTH, speakerOffRoute(mask))
    }

    @Test
    fun speakerOffOtherwiseLetsTelecomPickWiredOrEarpiece() {
        val mask = CallAudioState.ROUTE_EARPIECE or CallAudioState.ROUTE_SPEAKER
        assertEquals(CallAudioState.ROUTE_WIRED_OR_EARPIECE, speakerOffRoute(mask))
        assertEquals(CallAudioState.ROUTE_WIRED_OR_EARPIECE, speakerOffRoute(0))
    }
}
