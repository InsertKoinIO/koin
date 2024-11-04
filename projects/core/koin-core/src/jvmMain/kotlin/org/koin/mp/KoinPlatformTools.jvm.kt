package org.koin.mp

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
actual fun KoinPlatformTools.generateId(): String = Uuid.random().toString()