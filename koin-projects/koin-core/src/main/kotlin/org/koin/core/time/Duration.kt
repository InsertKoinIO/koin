package org.koin.core.time

import kotlin.system.measureNanoTime


fun measureDuration(code : () -> Unit) = measureNanoTime(code) / 1000000