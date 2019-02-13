package org.koin.core.definition

import org.koin.core.scope.setScopeName

object DefinitionFactory {

    inline fun <reified T> createSingle(
            name: String? = null,
            noinline definition: Definition<T>
    ): BeanDefinition<T> {
        return createDefinition(name, definition)
    }

    inline fun <reified T> createScope(
            name: String? = null,
            scopeName: String? = null,
            noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = createDefinition(name, definition, Kind.Scope)
        scopeName?.let { beanDefinition.setScopeName(scopeName) }
        return beanDefinition
    }

    inline fun <reified T> createFactory(
            name: String? = null,
            noinline definition: Definition<T>
    ): BeanDefinition<T> {
        return createDefinition(name, definition, Kind.Factory)
    }

    inline fun <reified T> createDefinition(
            name: String?,
            noinline definition: Definition<T>,
            kind: Kind = Kind.Single
    ): BeanDefinition<T> {
        val beanDefinition = BeanDefinition<T>(name, T::class)
        beanDefinition.definition = definition
        beanDefinition.kind = kind
        return beanDefinition
    }
}