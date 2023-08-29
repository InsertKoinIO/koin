package org.koin.core

import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker

fun <R> runBackground(block: () -> R): R {
    val result = worker.execute(TransferMode.SAFE, {
        block
    }) {
        it()
    }.result

    return result
}

private val worker = Worker.start()
