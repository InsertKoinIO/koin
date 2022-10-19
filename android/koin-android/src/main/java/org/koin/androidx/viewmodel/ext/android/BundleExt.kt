package org.koin.androidx.viewmodel.ext.android

import android.os.Bundle
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import org.koin.core.annotation.KoinInternalApi

@KoinInternalApi
@PublishedApi
internal fun Bundle.toExtras(): CreationExtras? {
    return if (keySet().isEmpty()) null
    else {
        runCatching {
            MutableCreationExtras().also { extras ->
                extras[DEFAULT_ARGS_KEY] = this
            }
        }.getOrNull()
    }
}