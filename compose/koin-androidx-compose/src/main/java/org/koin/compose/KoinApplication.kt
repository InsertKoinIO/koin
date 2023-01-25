package org.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

val LocalKoinApplication = compositionLocalOf { GlobalContext.get() }

@Composable
fun KoinApplication(
    koinApp: KoinAppDeclaration,
    content: @Composable () -> Unit
) {
    val koinApplication = startKoin(koinApp)
    CompositionLocalProvider(
        LocalKoinApplication provides koinApplication.koin,
    ) {
        content()
    }
}