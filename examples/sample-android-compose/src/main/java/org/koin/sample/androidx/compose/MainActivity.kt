package org.koin.sample.androidx.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.scope.ScopeActivity
import org.koin.compose.KoinApplication
import org.koin.compose.KoinContext
import org.koin.sample.androidx.compose.data.sdk.SDKData
import org.koin.sample.androidx.compose.di.appModule
import java.util.logging.Logger

class MainActivity : ScopeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ensure SDK is not accessible in main context
        assert(getKoin().getOrNull<SDKData>() == null)

        setContent {
            MaterialTheme {
                App()
            }
        }
    }

    companion object {
        val logger = Logger.getLogger("MainActivity")
    }
}