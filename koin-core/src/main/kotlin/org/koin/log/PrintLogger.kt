package org.koin.log

/**
 * Logger that print on system.out
 */
class PrintLogger : Logger {
    override fun debug(msg: String) {
        System.err.println("(KOIN) :: [DEBUG] :: $msg")
    }

    override fun log(msg: String) {
        println("(KOIN) :: $msg")
    }

    override fun err(msg: String) {
        System.err.println("(KOIN) :: [ERROR] :: $msg")
    }
}