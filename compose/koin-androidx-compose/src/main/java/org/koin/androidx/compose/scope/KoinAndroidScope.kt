package org.koin.androidx.compose.scope

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import org.koin.android.scope.AndroidScopeComponent
import org.koin.compose.LocalKoinScope
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.scope.Scope


@Composable
fun KoinActivityScope(
    content: @Composable () -> Unit
) {
    val scope = (LocalContext.current as? AndroidScopeComponent)?.scope
        ?: error("Current context ${LocalContext.current} must implement AndroidScopeComponent interface.")
    CompositionLocalProvider(
        LocalKoinScope provides scope,
    ) {
        content()
    }
}

@Composable
fun KoinFragmentScope(
    content: @Composable () -> Unit
) {
    val scope = (LocalContext.current as? AndroidScopeComponent)?.scope
        ?: error("Current context ${LocalContext.current} must implement AndroidScopeComponent interface.")
    CompositionLocalProvider(
        LocalKoinScope provides scope,
    ) {
        content()
    }
}

val koinAndroidScope: Scope
    @Composable
    get() {
        val context = LocalContext.current as? AndroidScopeComponent
            ?: error("LocalContext.current ${LocalContext.current} is not a KoinScopeComponent. Please use KoinScopeComponent interface on your Activity or Fragment.")
        return context.scope
    }
