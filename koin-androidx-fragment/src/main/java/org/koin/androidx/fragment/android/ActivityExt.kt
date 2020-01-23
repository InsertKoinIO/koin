package org.koin.androidx.fragment.android

import androidx.fragment.app.FragmentActivity
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