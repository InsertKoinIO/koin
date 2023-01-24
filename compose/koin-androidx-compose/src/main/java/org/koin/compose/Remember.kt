@file:OptIn(KoinInternalApi::class)

package org.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.getScopeId
import org.koin.core.component.getScopeName
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID

@Composable
inline fun rememberKoinModules(
    crossinline modules: @DisallowComposableCalls () -> List<Module> = { emptyList() }
) {
    //TODO Need Composer here?
//    val commposer = currentComposer
    val koin = getKoin()
    val wrapper = remember {
        CompositionKoinModuleLoader(modules(), koin)
    }
}

@Composable
inline fun <reified T : KoinScopeComponent> rememberKoinScopeComponent(
    context: T = LocalContext.current as T
): Scope {
    //TODO Need Composer here?
//    val commposer = currentComposer
    val koin = getKoin()
    val wrapper = remember {
        CompositionKoinScopeLoader(context.scope, koin)
    }
    return wrapper.scope
}

@Composable
inline fun <T : Any> rememberKoinScope(
    context: T = LocalContext.current as T
): Scope {
    //TODO Need Composer here?
//    val commposer = currentComposer
    val koin = getKoin()
    val wrapper = remember {
        CompositionKoinScopeLoader(koin.createScope(context.getScopeId(), context.getScopeName()), koin)
    }
    return wrapper.scope
}

@Composable
inline fun rememberKoinScope(
    scopeID: ScopeID,
    scopeName: Qualifier
): Scope {
    //TODO Need Composer here?
//    val commposer = currentComposer
    val koin = getKoin()
    val wrapper = remember {
        CompositionKoinScopeLoader(koin.createScope(scopeID, scopeName),koin)
    }
    return wrapper.scope
}

/*

fun createScope(scopeId: ScopeID, qualifier: Qualifier, source: Any? = null): Scope {
        return scopeRegistry.createScope(scopeId, qualifier, source)
    }

    /**
     * Create a Scope instance
     * @param scopeId
     */
    inline fun <reified T : Any> createScope(scopeId: ScopeID, source: Any? = null): Scope {
        val qualifier = TypeQualifier(T::class)
        return scopeRegistry.createScope(scopeId, qualifier, source)
    }

    /**
     * Create a Scope instance
     * @param scopeDefinitionName
     */
    inline fun <reified T : Any> createScope(scopeId: ScopeID = KoinPlatformTools.generateId()): Scope {
        val qualifier = TypeQualifier(T::class)
        return scopeRegistry.createScope(scopeId, qualifier, null)
    }

    /**
     * Create a Scope instance
     * @param scopeDefinitionName
     */
    fun <T : KoinScopeComponent> createScope(t: T): Scope {
        val scopeId = t.getScopeId()
        val qualifier = t.getScopeName()
        return scopeRegistry.createScope(scopeId, qualifier, null)
    }

 */