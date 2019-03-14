package org.koin.sample.androidx.components.sdk

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.Koin
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.sample.androidx.components.main.Service
import org.koin.sample.androidx.components.main.ServiceImpl

// Custom Koin instance Holder
object CustomSDK {
    val koinApp = koinApplication {
        printLogger(Level.DEBUG)
        modules(sdkModule)
    }
}

val sdkModule = module {
    single<Service> { ServiceImpl() }
    single { SDKService() }
    viewModel { SDKVIewModel(get()) }
}

// Custom KoinComponent
interface CustomKoinComponent : KoinComponent {
    override fun getKoin(): Koin = CustomSDK.koinApp.koin
}

class CustomService : CustomKoinComponent {
    val service: Service by inject()
}