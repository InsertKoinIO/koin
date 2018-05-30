package org.koin.log

/**
 * Logger that print on system.out
 * @author - Arnaud GIULIANI
 * @author - Laurent BARESSE
 */
class PrintLogger : Logger {
    override fun debug(msg: String) {
        println("(KOIN) :: [DEBUG] :: $msg")
    }

    override fun log(msg: String) {
        println("(KOIN) :: [INFO] :: $msg")
    }

    override fun err(msg: String) {
        System.err.println("(KOIN) :: [ERROR] :: $msg")
    }
}