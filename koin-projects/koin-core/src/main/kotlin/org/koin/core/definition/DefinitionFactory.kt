package org.koin.core.definition

import org.koin.core.qualifier.Qualifier

object DefinitionFactory {

    inline fun <reified T> createSingle(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        return createDefinition(qualifier, definition)
    }

    inline fun <reified T> createScoped(
        qualifier: Qualifier? = null,
        scopeName: Qualifier? = null,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = createDefinition(qualifier, definition, Kind.Scope)
        beanDefinition.scopeName = scopeName
        return beanDefinition
    }

    inline fun <reified T> createFactory(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        return createDefinition(qualifier, definition, Kind.Factory)
    }

    inline fun <reified T> createDefinition(
        qualifier: Qualifier?,
        noinline definition: Definition<T>,
        kind: Kind = Kind.Single
    ): BeanDefinition<T> {
        val beanDefinition = BeanDefinition<T>(qualifier, T::class)
        beanDefinition.definition = definition
        beanDefinition.kind = kind
        return beanDefinition
    }
}