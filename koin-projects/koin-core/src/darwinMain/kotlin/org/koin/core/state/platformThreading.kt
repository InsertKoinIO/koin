package org.koin.core.state

import platform.Foundation.NSThread

actual val isMainThread: Boolean
    get() = NSThread.isMainThread