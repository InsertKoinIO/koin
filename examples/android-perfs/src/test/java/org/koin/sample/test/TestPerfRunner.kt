package org.koin.sample.test

import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.sample.android.main.PerfLimit
import org.koin.sample.android.main.PerfRunner

class TestPerfRunner {

    @Test
    fun runTestScenario() = runBlocking {
        val limits = PerfLimit(17.0, 0.175)

        println("Perf Tolerance: $limits")

        val results = PerfRunner.runAll(this)
        results.applyLimits(limits)

        assertTrue("Should start under ${results.worstMaxStartTime} ms", results.isStartOk)
        assertTrue("Should exec under ${results.worstExecTime} ms", results.isExecOk)
    }

}