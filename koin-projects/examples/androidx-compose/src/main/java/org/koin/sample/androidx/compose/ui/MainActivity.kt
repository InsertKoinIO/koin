package org.koin.sample.androidx.compose.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.setContent
import androidx.ui.foundation.AdapterList
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.tooling.preview.Preview
import org.koin.androidx.compose.ExperimentalComposeInject
import org.koin.androidx.compose.inject
import org.koin.sample.androidx.compose.data.UserRepository

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme { App() }
        }
    }

    @Composable
    @Preview(showDecoration = true)
    fun Preview() {
        App()
    }
}

@OptIn(ExperimentalComposeInject::class)
@Composable
fun App() {
    val userRepository: UserRepository by inject()
    UsersView(userRepository)
}

@Composable
fun UsersView(userRepository: UserRepository) {
    AdapterList(userRepository.getUsers()) { user ->
        ListItem(
            text = user.name,
            secondaryText = user.age.toString(),
            onClick = {}
        )
    }
}

