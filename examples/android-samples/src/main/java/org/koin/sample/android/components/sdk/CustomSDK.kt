package org.koin.sample.android.components.sdk

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.Koin
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.sample.android.components.main.Service
import org.koin.sample.android.components.main.ServiceImpl

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
@OptIn(KoinApiExtension::class)
interface CustomKoinComponent : KoinComponent {
    override fun getKoin(): Koin = CustomSDK.koinApp.koin
}

class CustomService : CustomKoinComponent {
    val service: Service by inject()
}