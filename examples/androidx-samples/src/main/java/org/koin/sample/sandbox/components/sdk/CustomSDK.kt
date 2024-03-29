package org.koin.sample.sandbox.components.sdk

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.sample.sandbox.components.main.SimpleService
import org.koin.sample.sandbox.components.main.SimpleServiceImpl
import org.koin.sample.sandbox.components.scope.Session
import org.koin.sample.sandbox.sdk.HostActivity

// Custom Koin instance Holder
object CustomSDK {
    val koinApp = koinApplication {
        printLogger(Level.DEBUG)
        modules(sdkModule)
    }
}

val sdkModule = module {
    single<SimpleService> { SimpleServiceImpl() }
    single { SDKService() }
    viewModel { SDKVIewModel(get()) }

    scope<HostActivity>{
        scoped { Session() }
    }
}

// Custom KoinComponent
interface CustomKoinComponent : KoinComponent {
    override fun getKoin(): Koin = CustomSDK.koinApp.koin
}

class CustomService : CustomKoinComponent {
    val service: SimpleService by inject()
}