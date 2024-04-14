package org.koin.decompose.androidx.activity

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.CompositionLocalProvider
import com.arkivanov.decompose.defaultComponentContext
import org.koin.decompose.context.LocalComponentContext

/**
 * Set the content of the [ComponentActivity] with the [CompositionContext] provided by the [ComponentActivity]
 *
 * @param parent the parent [CompositionContext] to be used
 * @param content the content of the [ComponentActivity]
 *
 * @see ComponentActivity
 * @see CompositionContext
 * @see setContent
 * @see LocalComponentContext
 *
 * @author Antonio Vicente
 */
fun ComponentActivity.setContentComponentContext(
    parent: CompositionContext? = null,
    content: @Composable () -> Unit
) {
    val activityComponentContext = defaultComponentContext()
    setContent(
        parent = parent,
        content = {
            CompositionLocalProvider(
                LocalComponentContext provides activityComponentContext
            ) {
                content()
            }
        }
    )
}