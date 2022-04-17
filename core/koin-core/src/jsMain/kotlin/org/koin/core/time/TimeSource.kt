package org.koin.core.time

import kotlin.js.Date
import kotlin.math.roundToLong

internal actual fun getTimeSource(): TimeSource {
    val isNode = js(
        "typeof process !== 'undefined' " +
                "&& process.versions " +
                "&& !!process.versions.node"
    ) as Boolean

    return if (isNode) {
        NodeJsHrTimeSource()
    } else {
        val isPerformanceNowSupported = js("self.performance && !!self.performance.now") as Boolean
        if (isPerformanceNowSupported) {
            PerformanceNowTimeSource()
        } else {
            DateNowTimeSource()
        }
    }
}

// https://nodejs.org/api/process.html#processhrtimetime
private class NodeJsHrTimeSource : TimeSource() {
    override fun mark(): Long {
        val (seconds, nanos) = js("process.hrtime()") as Array<Double>
        return (seconds * 1_000_000_000 + nanos).roundToLong()
    }
}

// https://developer.mozilla.org/en-US/docs/Web/API/Performance/now
private class PerformanceNowTimeSource : TimeSource() {
    override fun mark(): Long {
        return (js("self.performance.now()") as Double * 1_000_000).roundToLong()
    }
}

// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date/now
private class DateNowTimeSource : TimeSource() {
    override fun mark(): Long {
        return (Date.now() * 1_000_000).roundToLong()
    }
}