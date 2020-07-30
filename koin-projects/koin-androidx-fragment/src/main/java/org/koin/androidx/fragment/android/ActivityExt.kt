package org.koin.androidx.fragment.android

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import org.koin.android.ext.android.get
import org.koin.core.scope.Scope

/**
 * Set supportFragmentManager.fragmentFactory to KoinFragmentFactory
 */
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
 * @param args
 * @param tag
 */
inline fun <reified F : Fragment> FragmentTransaction.replace(
    @LayoutRes containerViewId: Int,
    args: Bundle? = null,
    tag: String? = null
): FragmentTransaction {
    return replace(containerViewId, F::class.java, args, tag)
}