package org.koin.sample

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

// What the Navigation 3 page leaves out: the screens, and the start of Koin.
class HomeViewModel : ViewModel()

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    Text("Home")
}

@Composable
fun DetailScreen(viewModel: DetailViewModel) {
    Text("Detail ${viewModel.itemId}")
}

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Robolectric creates a new Application for each test, in the same process.
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(this@MainApplication)
                modules(homeModule, detailModule)
            }
        }
    }
}

class MainActivity : ComponentActivity()
