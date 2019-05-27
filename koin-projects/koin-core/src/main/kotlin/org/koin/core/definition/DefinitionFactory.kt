package org.koin.core.definition

import org.koin.core.qualifier.Qualifier

object DefinitionFactory {

    inline fun <reified T> createSingle(
            qualifier: Qualifier? = null,
            scopeName: Qualifier? = null,
            noinline definition: Definition<T>
    ): BeanDefinition<T> {
        return createDefinition(qualifier, definition, scopeName = scopeName)
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
            kind: Kind = Kind.Single,
            scopeName: Qualifier? = null
    ): BeanDefinition<T> {
        val beanDefinition = BeanDefinition<T>(qualifier, scopeName, T::class)
        beanDefinition.definition = definition
        beanDefinition.kind = kind
        return beanDefinition
    }
}