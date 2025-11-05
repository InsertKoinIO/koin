package org.koin.mp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise
import org.koin.core.annotation.KoinInternalApi
import kotlin.coroutines.CoroutineContext

actual object KoinPlatformCoroutinesTools {
    actual fun defaultCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @ExperimentalWasmJsInterop
    @OptIn(KoinInternalApi::class, DelicateCoroutinesApi::class)
    actual fun <T> runBlocking(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> T
    ): T {
        KoinPlatform.getKoinOrNull()?.logger?.warn("[Warning] - KoinPlatformCoroutinesTools.runBlocking is experimental coroutines on JS")
        return GlobalScope.promise(block = block).get()
    }
}
