package org.koin.spark

import org.koin.log.Logger
import org.slf4j.LoggerFactory

/**
 * SLF4J Koin Logger
 */
class Log4JLogger : Logger {

    private val logger: org.slf4j.Logger = LoggerFactory.getLogger("Koin")

    override fun debug(msg: String) {
        logger.debug(msg)
    }

    override fun err(msg: String) {
        logger.error(msg)
    }

    override fun log(msg: String) {
        logger.info(msg)
    }
}