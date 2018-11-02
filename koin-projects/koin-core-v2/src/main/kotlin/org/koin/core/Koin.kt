package org.koin.core

import org.koin.core.registry.BeanRegistry

class Koin {

    internal val beanRegistry = BeanRegistry()

    inline fun <reified T> get(): T {
        error("")
    }
}