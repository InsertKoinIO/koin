package org.koin.benchmark

import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test

class TestPerfRunner {

    /**
     * Avg start time: 13.06
     * Avg execution time: 0.158
     */
    @Test
    fun main() = runBlocking {
        val limits = PerfLimit(17.0, 0.175)

        println("Perf Tolerance: $limits")

        val results = PerfRunner.runAll(this)
        results.applyLimits(limits)

        assertTrue("Should start under ${results.worstMaxStartTime} ms", results.isStartOk)
        assertTrue("Should exec under ${results.worstExecTime} ms", results.isExecOk)
    }

}