package org.koin.Logger

import org.koin.core.KoinApplication
import org.koin.core.logger.Level

fun KoinApplication.slf4jLogger(level: Level = Level.INFO) {
    KoinApplication.logger = SLF4JLogger()
    KoinApplication.logger.level = level
}