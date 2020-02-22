package org.koin.core.state

import platform.Foundation.NSThread
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_sync
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

actual val isMainThread: Boolean
    get() = NSThread.isMainThread