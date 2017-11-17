package org.koin.log

/**
 * Logger that print on system.out
 */
class PrintLogger : Logger{
    override fun log(msg: String) {
        println("(KOIN) :: $msg")
    }
}