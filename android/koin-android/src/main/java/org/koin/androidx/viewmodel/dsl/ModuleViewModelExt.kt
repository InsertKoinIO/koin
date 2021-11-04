package org.koin.androidx.viewmodel.dsl

import androidx.lifecycle.ViewModel
import org.koin.core.module.Module
import org.koin.core.scope.new

/**
 * Declare a [Module.viewModel] definition by resolving a constructor reference for the dependency.
 * The resolution is done at compile time by leveraging inline functions, no reflection is required.
 *
 * Example:
 * ```
 * class ViewModel
 *
 * val myModule = module {
 *   viewModel(::ViewModel)
 * }
 * ```
 *
 * @author Marcello Galhardo
 *
 * @see new
 */
inline fun <reified R : ViewModel> Module.viewModel(
    crossinline constructor: () -> R,
) {
    viewModel(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R : ViewModel, reified T1> Module.viewModel(
    crossinline constructor: (T1) -> R,
) {
    viewModel(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R : ViewModel, reified T1, reified T2> Module.viewModel(
    crossinline constructor: (T1, T2) -> R,
) {
    viewModel(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3> Module.viewModel(
    crossinline constructor: (T1, T2, T3) -> R,
) {
    viewModel(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4> Module.viewModel(
    crossinline constructor: (T1, T2, T3, T4) -> R,
) {
    viewModel(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5> Module.viewModel(
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
) {
    viewModel(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> Module.viewModel(
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
) {
    viewModel(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> Module.viewModel(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
) {
    viewModel(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> Module.viewModel(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
) {
    viewModel(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> Module.viewModel(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
) {
    viewModel(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> Module.viewModel(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
) {
    viewModel(definition = { new(constructor) })
}
