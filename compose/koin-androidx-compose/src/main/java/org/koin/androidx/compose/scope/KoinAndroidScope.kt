package org.koin.androidx.compose.scope

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import org.koin.android.scope.AndroidScopeComponent
import org.koin.compose.LocalKoinScope


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