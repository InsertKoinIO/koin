package org.koin.composeviewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

/**
 * A base view model designed to wrap [Composable] functions with associated business logic.
 *
 * This class provides core utilities and structures to manage coroutine lifecycles within the
 * scope of a ViewModel. It ensures that the coroutines are automatically cancelled and cleaned up
 * when the ViewModel is no longer in use.
 */
abstract class ComposeViewModel : KoinComponent {

    /**
     * Represents a [SupervisorJob] that oversees all coroutine jobs initiated by this ViewModel.
     * Utilizing a supervisor allows child jobs to fail independently without affecting others.
     */
    protected val viewModelSupervisor = SupervisorJob()

    /**
     * A coroutine scope specifically for this ViewModel, determined by [viewModelSupervisor].
     * All coroutines launched within this ViewModel should use this scope to ensure they are
     * tied to the ViewModel's lifecycle.
     */
    protected val viewModelScope = CoroutineScope(viewModelSupervisor)


    /**
     * Abstract function that should be overridden by subclasses to clear or release resources
     * and jobs when the ViewModel is about to be disposed of.
     *
     * This function gets called when [clearViewModel] is executed, before the supervisor gets cancelled.
     */
    abstract suspend fun onClear()

    /**
     * Handles the process of cleaning up and releasing all resources tied to the ViewModel.
     * It first triggers the [onClear] method and, upon completion, cancels the [viewModelSupervisor]
     * which in turn cancels all active jobs under its jurisdiction.
     */
    fun clearViewModel() {
        viewModelScope.launch {
            onClear()
        }.invokeOnCompletion {
            viewModelSupervisor.cancel("Compose View Model Job Cancelled")
        }
    }
}
