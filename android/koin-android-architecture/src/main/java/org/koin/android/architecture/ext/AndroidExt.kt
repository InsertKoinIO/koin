package org.koin.android.architecture.ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.standalone.KoinComponent
import kotlin.reflect.KClass

/**
 * Get a ViewModel for given Fragment
 */
inline fun <reified T : ViewModel> Fragment.getViewModel(): T {
    return getViewModel(T::class)
}

/**
 * Get a ViewModel for given Fragment
 * version that avoid inline parameter and take KClass to instantiate
 */
fun <T : ViewModel> Fragment.getViewModel(clazz: KClass<T>): T {
    val vm = ViewModelProvider(ViewModelStores.of(this), KoinFactory)
    return vm.get(clazz.java)
}

/**
 * Lazy get view model
 * @param fromActivity - reuse ViewModel from parent Activity or create new one
 */
inline fun <reified T : ViewModel> Fragment.viewModel(fromActivity: Boolean = true): Lazy<T> = viewModel(T::class, fromActivity)

/**
 * Lazy get view model
 * version that avoid inline parameter and take KClass to instantiate
 *
 * @param fromActivity - reuse ViewModel from parent Activity or create new one
 */
fun <T : ViewModel> Fragment.viewModel(clazz: KClass<T>, fromActivity: Boolean = true): Lazy<T> = lazy {
    val currentActivity = activity
    if (fromActivity && currentActivity != null) currentActivity.getViewModel(clazz) else getViewModel(clazz)
}


/**
 * Get a ViewModel for given Activity
 */
inline fun <reified T : ViewModel> FragmentActivity.getViewModel(): T {
    return getViewModel(T::class)
}

/**
 * Get a ViewModel for given Activity - from given class
 * version that avoid inline parameter and take KClass to instantiate
 */
fun <T : ViewModel> FragmentActivity.getViewModel(clazz: KClass<T>): T {
    val vm = ViewModelProvider(ViewModelStores.of(this), KoinFactory)
    return vm.get(clazz.java)
}

/**
 * Lazy get a ViewModel - for Activity
 */
inline fun <reified T : ViewModel> FragmentActivity.viewModel(): Lazy<T> = lazy { getViewModel<T>() }

/**
 * Lazy get a ViewModel - for Activity
 * version that avoid inline parameter and take KClass to instantiate
 */
fun <T : ViewModel> FragmentActivity.viewModel(clazz: KClass<T>): Lazy<T> = lazy { getViewModel(clazz) }


/**
 * Koin ViewModel factory
 */
object KoinFactory : ViewModelProvider.Factory, KoinComponent {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return get(modelClass)
    }
}
