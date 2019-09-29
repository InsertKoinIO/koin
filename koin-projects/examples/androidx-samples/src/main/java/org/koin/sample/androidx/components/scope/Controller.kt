package org.koin.sample.androidx.components.scope

import androidx.lifecycle.LifecycleOwner

data class Controller(var owner: LifecycleOwner)