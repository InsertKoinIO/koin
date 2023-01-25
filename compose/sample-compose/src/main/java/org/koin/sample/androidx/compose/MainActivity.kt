package org.koin.sample.androidx.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import org.koin.androidx.scope.ScopeActivity
import java.util.logging.Logger

class MainActivity : ScopeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme { App() }
        }
    }

    companion object {
        val logger = Logger.getLogger("MainActivity")
    }
}