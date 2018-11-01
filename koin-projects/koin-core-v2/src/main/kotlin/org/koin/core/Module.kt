package org.koin.core

class Module {
    val definitions = arrayListOf<Definition<*>>()

    fun <T> single(definition: Definition<T>) {
        definitions.add(definition)
    }

    fun <T> factory(definition: Definition<T>) {
        definitions.add(definition)
    }

    fun <T> get() : T {
        error("Not yet implemented")
    }
}

typealias Definition<T> = () -> T