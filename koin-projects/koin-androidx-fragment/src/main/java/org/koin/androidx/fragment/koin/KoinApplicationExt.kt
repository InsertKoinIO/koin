package org.koin.androidx.fragment.koin

import androidx.fragment.app.FragmentFactory
import org.koin.androidx.fragment.android.KoinFragmentFactory
import org.koin.core.KoinApplication

/**
 * Setup the KoinFragmentFactory instance
 */
fun KoinApplication.fragmentFactory() {
    koin.declare(KoinFragmentFactory(), secondaryTypes = listOf(FragmentFactory::class))
}