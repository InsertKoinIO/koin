package org.koin.log

class PrintLogger : Logger{
    override fun log(msg: String) {
        println("(KOIN):: $msg")
    }
}