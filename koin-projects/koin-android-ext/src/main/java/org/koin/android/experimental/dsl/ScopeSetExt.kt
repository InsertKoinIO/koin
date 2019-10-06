package org.koin.android.experimental.dsl

import android.arch.lifecycle.ViewModel
import org.koin.android.viewmodel.dsl.setIsViewModel
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Options
import org.koin.core.definition.createFactory
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.DefaultScope
import org.koin.core.scope.RootScope
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeSet
import org.koin.experimental.builder.create
import kotlin.reflect.KClass

/**
 * ViewModel DSL Extension
 *
 * @author Arnaud Giuliani
 * @author Andreas Schattney
 **/

/**
 * Allow to declare a ViewModel - be later inject into Activity/Fragment with dedicated injector
 * @param qualifier - definition qualifier
 * @param override - allow definition override
 **/
inline fun <reified T : ViewModel> ScopeSet<*>.viewModel(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<*, T> {
    return createViewModelDefinition(name, override)
}

/**
 * Allow to declare a ViewModel - be later inject into Activity/Fragment with dedicated injector
 * @param qualifier - definition qualifier
 * @param override - allow definition override
 */
inline fun <S: Scope, reified T : ViewModel> ScopeSet<S>.createViewModelDefinition(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<S, T> {
    val beanDefinition = this.createFactory(name, qualifier) { create<T>() }
    declareDefinition(beanDefinition, Options(false, override))
    beanDefinition.setIsViewModel()
    if (!definitions.contains(beanDefinition)) {
        definitions.add(beanDefinition)
    } else {
        throw DefinitionOverrideException("Can't add definition $beanDefinition for scope ${this.qualifier} as it already exists")
    }
    return beanDefinition
}