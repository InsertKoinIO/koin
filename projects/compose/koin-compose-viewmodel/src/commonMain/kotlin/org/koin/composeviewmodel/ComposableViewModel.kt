package org.koin.composeviewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import org.koin.compose.getKoin
import org.koin.core.qualifier.Qualifier

/**
 * A composable function that provides a convenient way to instantiate and manage a [ComposeViewModel]
 * within the composable hierarchy. The [ComposeViewModel] instance created by this function is scoped
 * to the current composition, and its lifetime is bound to the composable's lifecycle.
 *
 * Usage of this function ensures that the [ComposeViewModel] and its associated resources are cleared
 * and disposed of properly when the composable is removed from the hierarchy.
 *
 * @param T The type of the [ComposeViewModel] being managed.
 * @param qualifier An optional qualifier to associate with the [ComposeViewModel] instance. Providing
 * a qualifier can be useful to distinguish between multiple instances of the same ViewModel type.
 * @param key1 An optional key to associate with the [ComposeViewModel] instance. Providing a key can
 * be useful to control the identity and therefore the lifecycle of the ViewModel, especially across
 * recompositions.
 * @param content A [Composable] lambda expression that will be invoked with the created [ComposeViewModel]
 * instance as its argument. This lambda is the consumer of the ViewModel, and is where the ViewModel
 * should be used to obtain data and interact with the rest of the composable hierarchy.
 */
@Composable
inline fun <reified T : ComposeViewModel> ComposableViewModel(
    qualifier: Qualifier? = null,
    key1: Any? = null,
    content: @Composable (T) -> Unit,
) {
    val koin = getKoin()
    val viewModel: T = remember(key1) { koin.get<T>(qualifier)}

    /*
     * Manages the disposal process of the [ComposeViewModel] and its associated resources. When the
     * composable associated with this ViewModel is removed from the hierarchy, or when there is a
     * change in the provided key, this effect will trigger the [ComposeViewModel.clearViewModel] method
     * to clean up resources.
     *
     * @see DisposableEffect
     */
    DisposableEffect(key1 = true) {
        onDispose {
            viewModel.clearViewModel()
        }
    }

    // Invokes the consumer [content] lambda with the created [ComposeViewModel] instance.
    content(viewModel)
}
