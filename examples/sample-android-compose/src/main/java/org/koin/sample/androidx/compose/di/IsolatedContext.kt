package org.koin.sample.androidx.compose.di

import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.sample.androidx.compose.data.sdk.SDKData

val sdkModule = module {
    single { SDKData() }
}

object IsolatedContextSDK {

    val koinApp = koinApplication {
        modules(sdkModule)
    }

}