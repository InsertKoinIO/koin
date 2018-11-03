package org.koin.core.module

import org.koin.core.Koin
import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.Definition
import org.koin.core.bean.Options
import org.koin.core.error.AlreadyExistingDefinition

class Module(internal val isCreatedAtStart: Boolean, internal val override: Boolean) {
    internal val definitions = hashSetOf<BeanDefinition<*>>()
    lateinit var koin: Koin

    inline fun <reified T> single(
        name: String? = null,
        isCreatedAtStart: Boolean = false,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = BeanDefinition.createSingle(name, definition)
        declareDefinition(beanDefinition, Options(isCreatedAtStart, override))
        return beanDefinition
    }

    inline fun <reified T> factory(
        name: String? = null,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = BeanDefinition.createFactory(name, definition)
        declareDefinition(beanDefinition, Options(override = override))
        return beanDefinition
    }

    fun <T> declareDefinition(definition: BeanDefinition<T>, options: Options) {
        definition.updateOptions(options)

        val added = definitions.add(definition)
        if (!added) {
            throw AlreadyExistingDefinition("Already existing definition $definition")
        }
    }

    private fun BeanDefinition<*>.updateOptions(options: Options) {
        this.options.isCreatedAtStart = options.isCreatedAtStart || isCreatedAtStart
        this.options.override = options.override || override
    }

    inline fun <reified T> get(): T {
        return koin.get()
    }
}