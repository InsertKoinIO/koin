package org.koin.mp

import co.touchlab.stately.concurrency.ThreadLocalRef

actual typealias Lockable = Any

actual typealias ThreadLocal<T> = ThreadLocalRef<T>