package org.koin.compose.subcoponent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import org.koin.compose.LocalKoinScope
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

/**
 * Defines a dependency injection component that holds a scope that will load all the dependencies
 * for the given composable content. The scope will be created with the given [ScopeKey] and will be
 * linked to the parent scope (the scope of the immediate [KoinSubComponent] caller). When the
 * component is disposed, the [Scope] will be closed.
 *
 * @param ScopeKey - the key to define the scope
 * @param subComponentModule - the module to load to the sub component dependencies
 * @param content - @Composable function
 *
 * @author Antonio Vicente
 */
@Composable
inline fun <reified ScopeKey : Any> KoinSubComponent(
    subComponentModule : Module = module {  },
    crossinline content: @Composable () -> Unit
) {
    val parentScope : Scope = LocalKoinScope.current
    val id = remember { KoinPlatformTools.generateId() }
    val subScope : Scope = parentScope.getKoin()
        .getOrCreateScope<ScopeKey>(id)
        .apply {
            linkTo(parentScope)
            loadModule(subComponentModule)
        }

    DisposableEffect(Unit) {
        onDispose {
            subScope.unloadModule(subComponentModule)
            subScope.close()
        }
    }

    CompositionLocalProvider(
        LocalKoinScope provides subScope
    ) {
        content()
    }
}

fun Scope.loadModule(module: Module) {
    getKoin().loadModules(listOf(module))
}

fun Scope.unloadModule(module: Module) {
    getKoin().unloadModules(listOf(module))
}