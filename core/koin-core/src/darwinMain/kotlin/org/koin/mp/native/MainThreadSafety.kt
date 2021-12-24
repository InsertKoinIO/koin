package org.koin.mp.native

import platform.Foundation.NSThread

internal actual val isMainThread: Boolean = NSThread.isMainThread