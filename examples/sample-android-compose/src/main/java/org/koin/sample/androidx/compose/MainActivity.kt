package org.koin.sample.androidx.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.android.ext.android.getKoin
import org.koin.androidx.scope.ScopeActivity
import org.koin.sample.androidx.compose.data.sdk.SDKData
import org.koin.sample.androidx.compose.navigation.NavigationGraphRoute
import org.koin.sample.androidx.compose.navigation.navigationStringGraph
import org.koin.sample.androidx.compose.navigation.navigationTypeSafeGraph
import java.util.logging.Logger

class MainActivity : ScopeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ensure SDK is not accessible in main context
        assert(getKoin().getOrNull<SDKData>() == null)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    composable("main") {
                        App(
                            onTypeSafeNavigateToNavigation = {
                                navController.navigate(NavigationGraphRoute)
                            },
                            onStringNavigateToNavigation = {
                                navController.navigate("navigation_graph")
                            }
                        )
                    }

                    // Type-Safe Navigation graph
                    navigationTypeSafeGraph(navController)

                    // String-based Navigation graph
                    navigationStringGraph(navController)
                }
            }
        }
    }

    companion object {
        val logger = Logger.getLogger("MainActivity")
    }
}