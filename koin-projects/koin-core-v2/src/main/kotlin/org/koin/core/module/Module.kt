package org.koin.core.module

import org.koin.core.Koin
import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.Definition
import org.koin.core.bean.Options
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.Scope

class Module(internal val isCreatedAtStart: Boolean, internal val override: Boolean) {
    internal val definitions = arrayListOf<BeanDefinition<*>>()
    lateinit var koin: Koin

    fun <T> declareDefinition(definition: BeanDefinition<T>, options: Options) {
        definition.updateOptions(options)
        definitions.add(definition)
    }

    inline fun <reified T> single(
        name: String? = null,
        createdAtStart: Boolean = false,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = BeanDefinition.createSingle(name, definition)
        declareDefinition(beanDefinition, Options(createdAtStart, override))
        return beanDefinition
    }

    inline fun <reified T> scope(
        scopeId: String,
        name: String? = null,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = BeanDefinition.createScope(name, scopeId, definition)
        declareDefinition(beanDefinition, Options(override = override))
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

    private fun BeanDefinition<*>.updateOptions(options: Options) {
        this.options.isCreatedAtStart = options.isCreatedAtStart || isCreatedAtStart
        this.options.override = options.override || override
    }

    inline fun <reified T> get(
        name: String? = null,
        scope: Scope? = null,
        noinline parameters: ParametersDefinition? = null
    ): T {
        return koin.get(name, scope, parameters)
    }
}