package org.koin.viewmodel

import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavBackStackEntry
import org.koin.core.annotation.KoinInternalApi

fun defaultNavExtras(viewModelStoreOwner: ViewModelStoreOwner): CreationExtras = when {
    viewModelStoreOwner is HasDefaultViewModelProviderFactory -> viewModelStoreOwner.defaultViewModelCreationExtras
    else -> CreationExtras.Empty
}