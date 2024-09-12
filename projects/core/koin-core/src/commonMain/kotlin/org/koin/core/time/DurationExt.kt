package org.koin.core.time

import kotlin.time.Duration

val Duration.inMs : Double get() = (inWholeMicroseconds / 1000.0)