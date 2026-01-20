package org.koin.compose.activity

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.android.scope.AndroidScopeComponent
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

/**
 * Resolves a Koin dependency from the current Activity's scope within a Composable context.
 *
 * This function retrieves dependencies that are scoped to the current [ComponentActivity].
 * The Activity must implement [AndroidScopeComponent] to provide its Koin scope.
 *
 * The resolved instance is remembered across recompositions as long as the qualifier,
 * scope, and parameters remain the same.
 *
 * @param T The type of the dependency to resolve.
 * @param qualifier Optional [Qualifier] to distinguish between multiple definitions of the same type.
 * @param scope The Koin [Scope] to resolve from. Defaults to the current Activity's scope.
 *              The Activity must implement [AndroidScopeComponent], otherwise an error is thrown.
 * @param parameters Optional [ParametersDefinition] to pass dynamic parameters to the dependency.
 * @return The resolved instance of type [T].
 * @throws IllegalStateException if the current Activity does not implement [AndroidScopeComponent].
 *
 * @sample
 * ```
 * @Composable
 * fun MyScreen() {
 *     val stateHolder: StateHolder = koinActivityInject()
 *     // Use state holder...
 * }
 * ```
 *
 * @see AndroidScopeComponent
 * @see Scope
 */
@OptIn(KoinInternalApi::class)
@Composable
inline fun <reified T> koinActivityInject(
    qualifier: Qualifier? = null,
    scope: Scope = (LocalActivity.current as? AndroidScopeComponent)?.scope ?: error("Activity is not an AndroidScopeComponent. Make your activity implement AndroidScopeComponent to use koinActivityInject"),
    noinline parameters: ParametersDefinition? = null,
): T {
    val p = parameters?.invoke()
    return remember(qualifier, scope, p) {
        scope.getWithParameters(T::class, qualifier, p)
    }
}