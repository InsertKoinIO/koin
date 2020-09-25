package org.koin.androidx.fragment.android

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import org.koin.android.ext.android.get
import org.koin.core.KoinExperimentalAPI
import org.koin.core.scope.Scope

/**
 * Set supportFragmentManager.fragmentFactory to KoinFragmentFactory
 */
@KoinExperimentalAPI
fun FragmentActivity.setupKoinFragmentFactory(scope: Scope? = null) {
    if (scope == null) {
        supportFragmentManager.fragmentFactory = get()
    } else {
        supportFragmentManager.fragmentFactory = KoinFragmentFactory(scope)
    }
}

/**
 * Replace container
 *
 * @param containerViewId
 * @param args
 * @param tag
 */
@KoinExperimentalAPI
inline fun <reified F : Fragment> FragmentTransaction.replace(
    @IdRes containerViewId: Int,
    args: Bundle? = null,
    tag: String? = null
): FragmentTransaction {
    return replace(containerViewId, F::class.java, args, tag)
}