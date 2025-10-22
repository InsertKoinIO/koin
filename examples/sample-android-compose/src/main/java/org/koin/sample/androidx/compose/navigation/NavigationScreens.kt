package org.koin.sample.androidx.compose.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.koin.compose.viewmodel.sharedKoinViewModel
import org.koin.sample.androidx.compose.ButtonForCreate
import org.koin.sample.androidx.compose.viewmodel.SharedViewModel

fun NavGraphBuilder.navigationTypeSafeGraph(navController: NavHostController) {
    navigation<NavigationGraphRoute>(startDestination = FirstScreenRoute) {
        composable<FirstScreenRoute> { backStackEntry ->
            val sharedViewModel = backStackEntry.sharedKoinViewModel<SharedViewModel>(navController)

            NavigationFirstScreen(
                viewModel = sharedViewModel,
                onNavigateToSecond = {
                    navController.navigate(SecondScreenRoute)
                }
            )
        }

        composable<SecondScreenRoute> { backStackEntry ->

            val sharedViewModel = backStackEntry.sharedKoinViewModel<SharedViewModel>(navController)

            NavigationSecondScreen(
                viewModel = sharedViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun NavigationFirstScreen(
    viewModel: SharedViewModel,
    onNavigateToSecond: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "First Screen",
            style = MaterialTheme.typography.h5
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("Last Updated: ${viewModel.lastUpdatedTime ?: "Not set"}")

        Spacer(modifier = Modifier.height(16.dp))

        ButtonForCreate("Record Time") { viewModel.recordTime() }

        Spacer(modifier = Modifier.height(8.dp))

        ButtonForCreate(
            "Go to Second Screen",
            onNavigateToSecond
        )
    }
}

@Composable
fun NavigationSecondScreen(
    viewModel: SharedViewModel,
    onNavigateBack: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Second Screen",
            style = MaterialTheme.typography.h5
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("Last Updated: ${viewModel.lastUpdatedTime ?: "Not set"}")
        Spacer(modifier = Modifier.height(8.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.recordTime() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Record Time")
        }

        Spacer(modifier = Modifier.height(8.dp))

        ButtonForCreate(
            "Back to First Screen",
            onNavigateBack
        )
    }
}
