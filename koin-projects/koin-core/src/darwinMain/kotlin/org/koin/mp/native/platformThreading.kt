package org.koin.mp.native

import platform.Foundation.NSThread

actual val isMainThread: Boolean
    get() = NSThread.isMainThread