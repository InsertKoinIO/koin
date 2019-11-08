/*
 * Copyright 2017-2018 the original author or authors.
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
 */package org.koin.core.time

import kotlin.time.ExperimentalTime
import kotlin.time.MonoClock

/**
 * Measure functions
 *
 * @author Arnaud Giuliani
 */

/**
 * Measure code execution
 */
@UseExperimental(ExperimentalTime::class)
fun measureDuration(code: () -> Unit): Double {
    val clock = MonoClock
    val mark = clock.markNow()
    code()
    return mark.elapsedNow().inMilliseconds
}

@UseExperimental(ExperimentalTime::class)
fun measureDuration(message: String, code: () -> Unit) {
    val time = measureDuration(code)
    println("$message - $time ms")
}

/**
 * Measure code execution and get result
 */
@UseExperimental(ExperimentalTime::class)
fun <T> measureDurationForResult(code: () -> T): Pair<T, Double> {
    val clock = MonoClock
    val mark = clock.markNow()
    val result = code()
    return Pair(result, mark.elapsedNow().inMilliseconds)
}

@UseExperimental(ExperimentalTime::class)
fun <T> measureDurationForResult(message: String, code: () -> T): T {
    val (result, time) = measureDurationForResult(code)
    println("$message - $time ms")
    return result
}