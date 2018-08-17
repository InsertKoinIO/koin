package org.koin.core.time

/**
 * Simple class to measure durations
 */
class Duration {

    private var start = 0L
    private var end = 0L

    fun start(){
        start = System.nanoTime()
    }

    fun stop(){
        end = System.nanoTime()
    }

    fun durationInMs() = (end - start) / 1000000

}