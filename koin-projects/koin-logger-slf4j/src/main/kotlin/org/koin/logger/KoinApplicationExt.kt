package org.koin.logger

import org.koin.core.KoinApplication
import org.koin.core.logger.Level

fun KoinApplication.slf4jLogger(level: Level = Level.INFO) {
    logger(SLF4JLogger(level))
}