/*
 * Copyright 2017-2020 the original author or authors.
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


/**
 * Measure functions
 *
 * @author Arnaud Giuliani
 */

/**
 * Measure code execution
 */
fun measureDuration(code: () -> Unit): Double {
    val startTime = System.nanoTime().toDouble()
    code()
    val endTime = System.nanoTime().toDouble()
    return (endTime - startTime).inMilliseconds()
}

fun measureDuration(message: String, code: () -> Unit) {
    val time = measureDuration(code)
    println("$message - $time ms")
}

/**
 * Measure code execution and get result
 */
fun <T> measureDurationForResult(code: () -> T): Pair<T, Double> {
    val startTime = System.nanoTime().toDouble()
    val result = code()
    val endTime = System.nanoTime().toDouble()
    return Pair(result, (endTime - startTime).inMilliseconds())
}

fun <T> measureDurationForResult(message: String, code: () -> T): T {
    val (result, time) = measureDurationForResult(code)
    println("$message - $time ms")
    return result
}

private fun Double.inMilliseconds(): Double = (this / 1000000)
