/*
 * Copyright 2017-Present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.koin.core.time

import org.koin.mp.KoinPlatformTimeTools

/**
 * Time Measure functions
 *
 * @author Arnaud Giuliani
 */

/**
 * Measure time in milliseconds for given code
 * @param code - code to execute
 * @return Time in milliseconds
 */
inline fun measureDuration(code: () -> Unit): TimeInMillis {
    return measureTimedValue(code).second
}

/**
 * Measure time in milliseconds and get result
 * @param code - code to execute
 * @return Pair Value & Time in milliseconds
 */
inline fun <T> measureDurationForResult(code: () -> T): Pair<T, TimeInMillis> {
    val (value, duration) = measureTimedValue(code)
    return Pair(value, duration)
}

inline fun <T> measureTimedValue(code: () -> T): Pair<T, TimeInMillis> {
    val start = KoinPlatformTimeTools.getTimeInNanoSeconds()
    val value = code()
    val stop = KoinPlatformTimeTools.getTimeInNanoSeconds()
    return Pair(value, (stop - start) / Timer.NANO_TO_MILLI)
}

typealias TimeInMillis = Double
