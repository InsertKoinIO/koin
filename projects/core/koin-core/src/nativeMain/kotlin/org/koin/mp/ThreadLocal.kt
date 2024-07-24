package org.koin.mp

import co.touchlab.stately.concurrency.Lock
import co.touchlab.stately.concurrency.ThreadLocalRef

actual open class Lockable {
    internal val lock = Lock()
}

actual typealias ThreadLocal<T> = ThreadLocalRef<T>