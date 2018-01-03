package org.koin.sample.util

import org.koin.log.Logger
import org.slf4j.LoggerFactory

class Log4JLogger : Logger {

    val logger: org.slf4j.Logger = LoggerFactory.getLogger("Koin")

    override fun err(msg: String) {
        logger.error(msg)
    }

    override fun log(msg: String) {
        logger.info(msg)
    }
}