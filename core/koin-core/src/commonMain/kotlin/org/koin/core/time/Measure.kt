/*
 * Copyright 2017-2021 the original author or authors.
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

import kotlin.time.measureTime
import kotlin.time.measureTimedValue

/**
 * Executes the [code] and returns the duration of execution in milliseconds.
 *
 * TODO: Use [measureTime] when it becomes stable.
 */
internal fun measureDuration(code: () -> Unit): Double {
    val timeSource = getTimeSource()

    val start = timeSource.mark()
    code()
    val end = timeSource.mark()

    return (end - start) / 1_000_000.0
}

/**
 * Executes the [code] and returns a pair of values - the result of the function execution
 * and the duration of execution in milliseconds.
 *
 * TODO: Use [measureTimedValue] when it becomes stable.
 */
internal fun <T> measureDurationForResult(code: () -> T): Pair<T, Double> {
    val timeSource = getTimeSource()

    val start = timeSource.mark()
    val result = code()
    val end = timeSource.mark()

    val duration = (end - start) / 1_000_000.0

    return Pair(result, duration)
}