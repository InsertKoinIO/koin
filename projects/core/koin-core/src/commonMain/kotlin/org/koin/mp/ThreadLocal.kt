package org.koin.mp

expect open class Lockable()

expect open class ThreadLocal<T>() {
    fun get(): T?
    fun set(value: T?)
    fun remove()
}