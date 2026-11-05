package org.koin.sample

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

class UserRepository {
    fun findName() = "Alice"
}

class UserService(private val repository: UserRepository) {
    fun greet() = "Hello, ${repository.findName()}!"
}

val appModule = module {
    single<UserRepository>()
    single<UserService>()
    viewModel<UserViewModel>()
}

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { UserScreen() }
    }
}
