package org.koin.androidx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import kotlin.reflect.KClass

class KoinViewModelLazy<VM : ViewModel> (
    private val viewModelClass: KClass<VM>,
    private val viewModelKey: String? = null,
    private val storeProducer: () -> ViewModelStore,
    private val factoryProducer: () -> ViewModelProvider.Factory,
) : Lazy<VM> {
    private var cached: VM? = null

    override val value: VM
        get() {
            val viewModel = cached
            return if (viewModel == null) {
                val factory = factoryProducer()
                val store = storeProducer()
                if (viewModelKey != null) {
                    ViewModelProvider(store, factory)[viewModelKey, viewModelClass.java].also {
                        cached = it
                    }
                } else {
                    ViewModelProvider(store, factory)[viewModelClass.java].also {
                        cached = it
                    }
                }
            } else {
                viewModel
            }
        }

    override fun isInitialized(): Boolean = cached != null
}