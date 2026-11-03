package org.koin.sample

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.module.dsl.navigationScope
import org.koin.plugin.module.dsl.scoped

class ScreenStateHolder

val appModule = module {
    navigationScope {
        scoped<ScreenStateHolder>()
    }
}

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (GlobalContext.getOrNull() == null) startKoin { modules(appModule) }
    }
}

@Serializable data object ListRoute
@Serializable data object DetailRoute

// The test drives the navigation and reads the scope of the entry through these.
object Probe {
    lateinit var navController: NavHostController
    var detailEntryId: String? = null
    var holder: ScreenStateHolder? = null
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            Probe.navController = navController
            NavHost(navController, startDestination = ListRoute) {
                composable<ListRoute> { Text("List") }
                composable<DetailRoute> { entry ->
                    ClosingKoinNavigationScope(entry) {
                        Probe.detailEntryId = entry.id
                        Probe.holder = koinInject<ScreenStateHolder>()
                        Text("Detail")
                    }
                }
            }
        }
    }
}
