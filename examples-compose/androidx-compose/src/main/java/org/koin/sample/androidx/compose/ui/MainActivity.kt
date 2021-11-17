package org.koin.sample.androidx.compose.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import org.koin.sample.androidx.compose.data.User
import org.koin.sample.androidx.compose.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme { App() }
        }
    }
}

@Composable
fun App(userViewModel: UserViewModel = getViewModel()) {
    UsersView(userViewModel.getUsers())
}

@Composable
fun UsersView(users: List<User>) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        users.forEach { user ->
            Text(
                text = user.name,
                color = Color.Blue
            )
        }
    }
}