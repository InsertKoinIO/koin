package org.koin.core

import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze

@OptIn(ExperimentalStdlibApi::class)
fun <R> runBackground(block: () -> R):R {
    val result = worker.execute(TransferMode.SAFE, {
        if(!isExperimentalMM()) {
            block.freeze()
        }
        block
    }){
        it()
    }.result

    return result
}

private val worker = Worker.start()