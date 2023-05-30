package org.koin.sample.sandbox.workmanager

import kotlinx.coroutines.channels.Channel

/**
 * @author : Fabio de Matos
 * @since : 16/02/2020
 **/
class SimpleWorkerService {

    private val channel = Channel<Int>(Channel.BUFFERED)

    /**
     * Returns the next answer
     */
    suspend fun popAnswer(): Int {
        return channel.receive()
    }

    suspend fun addAnswer(answer: Int) {
        channel.send(answer)
    }

    fun isEmpty(): Boolean = channel.isEmpty
}