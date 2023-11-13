package org.koin.benchmark

import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class TestPerfRunner {

    /**
     * Avg start time: 13.06 ms
     * Avg execution time: 0.158 ms
     */
//    @Test
    fun main() = runBlocking {
        val limits = PerfLimit(17.milliseconds, 0.175.milliseconds)

        println("Perf Tolerance: $limits")

        val results = PerfRunner.runAll(this)
        results.applyLimits(limits)

        assertTrue("Should start under ${results.worstMaxStartTime}", results.isStartOk)
        assertTrue("Should exec under ${results.worstExecTime}", results.isExecOk)
    }

}