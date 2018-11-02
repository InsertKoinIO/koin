package org.koin.core.module

class Module {
    val definitions = arrayListOf<Definition<*>>()

    fun <T> single(definition: Definition<T>) {
        declareDefinition(definition)
    }

    fun <T> factory(definition: Definition<T>) {
        declareDefinition(definition)
    }

    private fun <T> declareDefinition(definition: Definition<T>) {
        definitions.add(definition)
    }

    fun <T> get(): T {
        error("Not yet implemented")
    }
}

typealias Definition<T> = () -> T