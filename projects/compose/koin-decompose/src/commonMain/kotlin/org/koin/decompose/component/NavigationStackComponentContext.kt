package org.koin.decompose.component

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import org.koin.compose.LocalKoinScope
import org.koin.core.module.Module
import org.koin.core.qualifier.TypeQualifier
import org.koin.compose.subcoponent.KoinSubComponent
import org.koin.decompose.context.LocalComponentContext
import org.koin.decompose.navigation.Child
import org.koin.decompose.navigation.defaultAnimation
import org.koin.decompose.navigation.navigationStack
import org.koin.dsl.module

/**
 * Instantiates, renders and declares a navigation stack. The stack is declared in the current Koin
 * scope. We assume that each stack is a singleton within this its scope (Scoped) so it can be
 * injected by declaring a module like this:
 *
 * val DependencyInScopeModule = module {
 * 	    scope<ScopeKey> {
 * 		    factory() { DependencyInScope(stackNavigator = get()) }
 * 	    }
 * }
 *
 * @param Config the configuration type of the stack
 * @param ScopeKey the key of the scope where the stack will be declared
 *
 * @param subComponentModule the module to load to the sub component dependencies
 * @param initialConfiguration the initial configuration of the stack
 * @param animation the animator to be used for the stack
 * @param childProvider a lambda that converts a configuration to a [Child]
 *
 * @author Antonio Vicente
 */
@Composable
inline fun <reified Config : Any, reified ScopeKey : Any> NavigationStackComponentContext(
    subComponentModule : Module = module {  },
    initialConfiguration: Config,
    animation: StackAnimation<Config, Child>? = stackAnimation(defaultAnimation),
    noinline childProvider : Config.(StackNavigation<Config>) -> Child
) = KoinSubComponent<ScopeKey>(
    subComponentModule = subComponentModule
) {
    val navigationStack = LocalComponentContext.current.navigationStack(
        initialConfiguration = initialConfiguration,
        toChildRenderable = childProvider,
        animation = animation
    )
    LocalKoinScope.current.declare(
        instance = navigationStack.navigation,
        qualifier = TypeQualifier(Config::class)
    )
    navigationStack.render()
}