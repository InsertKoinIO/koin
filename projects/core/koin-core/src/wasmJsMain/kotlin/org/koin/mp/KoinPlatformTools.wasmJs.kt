package org.koin.mp

import com.benasher44.uuid.uuid4

actual fun KoinPlatformTools.generateId(): String = uuid4().toString()