package org.koin.decompose

import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration

/**
 * Defines a KoinApplicationComponent that binds AppContext to the graph
 *
 * @param application the [KoinAppDeclaration] to be used
 * @param content the content of the application
 *
 * @author Antonio Vicente
 */
@Composable
fun KoinApplicationComponent(
    application: KoinAppDeclaration,
    content: @Composable () -> Unit
) {
    KoinApplication(
        application = application,
    ) {
        AppContextBinder()
        content()
    }
}