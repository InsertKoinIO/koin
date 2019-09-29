package org.koin.androidx.experimental.dsl

import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.dsl.setIsViewModel
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Options
import org.koin.core.definition.createFactory
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeSet
import org.koin.experimental.builder.create

/**
 * ViewModel DSL Extension
 *
 * @author Arnaud Giuliani
 *
 */

/**
 * Allow to declare a ViewModel - be later inject into Activity/Fragment with dedicated injector
 * @param qualifier - definition qualifier
 * @param override - allow definition override
 */
inline fun <reified T : ViewModel> ScopeSet<out Scope>.autoViewModel(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<out Scope, T> {
    return genericAutoViewModel(name, override)
}

inline fun <S: Scope, reified T : ViewModel> ScopeSet<S>.genericAutoViewModel(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<S, T> {
    val beanDefinition = this.factory.createFactory<S, T>(name, qualifier) { create() }
    declareDefinition(beanDefinition, Options(false, override))
    beanDefinition.setIsViewModel()
    if (!definitions.contains(beanDefinition)) {
        definitions.add(beanDefinition)
    } else {
        throw DefinitionOverrideException("Can't add definition $beanDefinition for scope ${this.qualifier} as it already exists")
    }
    return beanDefinition
}