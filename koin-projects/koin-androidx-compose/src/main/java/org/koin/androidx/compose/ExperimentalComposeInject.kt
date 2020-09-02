package org.koin.androidx.compose

/**
 * @author Henrique Horbovyi
 * */

/**
 * Annotation that marks [inject] composable as experimental
 * */
@RequiresOptIn(message = "Inject composable it's still an experimental feature. It may change in the future.")
annotation class ExperimentalComposeInject
