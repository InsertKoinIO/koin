@file:OptIn(KoinInternalApi::class)

package org.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.Module

@Composable
inline fun rememberKoinModules(
    crossinline modules: @DisallowComposableCalls () -> List<Module> = { emptyList() }
) {
    val koin = getKoin()
    remember {
        CompositionKoinModuleLoader(modules(), koin)
    }
}
