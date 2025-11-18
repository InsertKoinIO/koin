package org.koin.compose.scope

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import org.koin.compose.ComposeContextWrapper
import org.koin.compose.LocalKoinScopeContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.scope.Scope
import org.koin.mp.KoinPlatform

/**
 * Creates and provides a Koin scope tied to a navigation back stack entry lifecycle.
 *
 * This composable creates a Koin scope that is automatically bound to the lifecycle of the provided
 * [NavBackStackEntry]. The scope will be created when the composable is first composed and will be
 * closed when the composition is abandoned.
 *
 * @param backStackEntry The navigation back stack entry to bind the Koin scope to.
 * The scope's ID will be derived from the back stack entry's ID.
 * @param content The composable content that will have access to the navigation scope
 * through [LocalKoinScopeContext].
 *
 * @see rememberKoinNavigationScope
 * @see NavBackStackEntry
 */
@OptIn(KoinInternalApi::class)
@KoinExperimentalAPI
@Composable
fun KoinNavigationScope(
    backStackEntry: NavBackStackEntry,
    content: @Composable () -> Unit
) {
    val scope = remember(backStackEntry) {
        KoinPlatform.getKoin().getOrCreateScope<NavBackStackEntry>(backStackEntry.id)
    }
    rememberKoinNavigationScope(scope)
    CompositionLocalProvider(
        LocalKoinScopeContext provides ComposeContextWrapper(scope),
    ) {
        content()
    }
}

/**
 * Remembers a Koin navigation scope and manages its lifecycle within a composition.
 *
 * This composable function creates a [CompositionKoinNavigationScopeLoader] that observes
 * the composition lifecycle. The scope will be automatically closed when the composition
 * is abandoned (e.g., when the user navigates away from the screen).
 *
 * @param scope The Koin scope to remember and manage.
 * @return The same scope that was passed in, now tracked by the composition lifecycle.
 *
 * @see CompositionKoinNavigationScopeLoader
 */
@OptIn(KoinInternalApi::class)
@KoinExperimentalAPI
@Composable
internal fun rememberKoinNavigationScope(scope: Scope): Scope {
    val wrapper = remember(scope) {
        CompositionKoinNavigationScopeLoader(scope)
    }
    return wrapper.scope
}

/**
 * A [RememberObserver] that manages the lifecycle of a Koin navigation scope within a composition.
 *
 * This class tracks when a composition is abandoned and ensures the associated Koin scope
 * is properly closed to prevent memory leaks. The scope is only closed when [onAbandoned]
 * is called, which occurs when the composition is permanently discarded (e.g., when navigating
 * away from a screen).
 *
 * @property scope The Koin scope to manage. This scope will be closed when the composition
 * is abandoned, unless it is a root scope or already closed.
 *
 * @see RememberObserver
 * @see Scope
 */
@KoinExperimentalAPI
@KoinInternalApi
internal class CompositionKoinNavigationScopeLoader(
    val scope: Scope
) : RememberObserver {

    override fun onRemembered() {
        // Nothing to do
    }

    override fun onForgotten() {
        // Nothing to do
    }

    override fun onAbandoned() {
        // Drop Scope only if onAbandoned
        close()
    }

    private fun close() {
        if (!scope.isRoot && !scope.closed){
            scope.logger.debug("CompositionKoinScopeLoader close scope: '${scope.id}'")
            scope.close()
        }
    }
}
