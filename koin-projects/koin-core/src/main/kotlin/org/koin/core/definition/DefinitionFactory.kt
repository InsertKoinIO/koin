package org.koin.core.definition

import org.koin.core.qualifier.Qualifier

object DefinitionFactory {

    inline fun <reified T> createSingle(
            qualifier: Qualifier? = null,
            scopeName: Qualifier? = null,
            noinline definition: Definition<T>
    ): BeanDefinition<T> {
        return createDefinition(qualifier, definition, Kind.Single, scopeName)
    }

    inline fun <reified T> createScoped(
            qualifier: Qualifier? = null,
            scopeName: Qualifier? = null,
            noinline definition: Definition<T>
    ): BeanDefinition<T> {
        return createDefinition(qualifier, definition, Kind.Scoped, scopeName)
    }

    inline fun <reified T> createFactory(
            qualifier: Qualifier? = null,
            scopeName: Qualifier? = null,
            noinline definition: Definition<T>
    ): BeanDefinition<T> {
        return createDefinition(qualifier, definition, Kind.Factory, scopeName)
    }

    inline fun <reified T> createDefinition(
            qualifier: Qualifier?,
            noinline definition: Definition<T>,
            kind: Kind,
            scopeName: Qualifier?
    ): BeanDefinition<T> {
        val beanDefinition = BeanDefinition<T>(qualifier, scopeName, T::class)
        beanDefinition.definition = definition
        beanDefinition.kind = kind
        return beanDefinition
    }
}