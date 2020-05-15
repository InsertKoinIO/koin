package org.koin.androidx.fragment.android

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import org.koin.core.KoinComponent
import org.koin.core.scope.Scope

/**
 * FragmentFactory for Koin
 */
class KoinFragmentFactory(val scope: Scope? = null) : FragmentFactory(), KoinComponent {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val clazz = Class.forName(className).kotlin
        val instance = if (scope != null) {
            scope.getOrNull<Fragment>(clazz)
        } else {
            getKoin().getOrNull<Fragment>(clazz)
        }
        return instance ?: super.instantiate(classLoader, className)
    }

}