package org.koin.core.state

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlatformThreadingTest {
    @Test
    fun checkPlatformThreading(){
        assertFalse(platformThreading.multithreadingCapable)
        assertTrue(platformThreading.isMainThread)
    }
}