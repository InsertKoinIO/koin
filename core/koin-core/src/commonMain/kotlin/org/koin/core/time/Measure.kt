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

import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.measureTimedValue


/**
 * Measure functions
 *
 * @author Arnaud Giuliani
 */

@OptIn(ExperimentalTime::class)
fun measureDuration(code: () -> Unit): Double {
    return measureTime(code).toDouble(DurationUnit.MILLISECONDS)
}

/**
 * Measure code execution and get result
 */
@OptIn(ExperimentalTime::class)
fun <T> measureDurationForResult(code: () -> T): Pair<T, Double> {
    val result = measureTimedValue(code)
    return Pair(result.value, result.duration.toDouble(DurationUnit.MILLISECONDS))
}