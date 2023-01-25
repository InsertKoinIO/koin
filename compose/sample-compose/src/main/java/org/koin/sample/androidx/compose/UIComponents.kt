package org.koin.sample.androidx.compose

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.sample.androidx.compose.data.User
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
fun now(): String = Instant.now().toString()

@Composable
fun ButtonForCreate(label: String, onClick: () -> Unit) {
    Button(onClick = {
        onClick()
    }
    ) {
        Text(text = label)
    }
}

@Composable
fun clickComponent(title: String, id: String, parentStatus: String, modifier: Modifier = Modifier, content: @Composable (String) -> Unit) {
    MainActivity.logger.info(" -- clickComponent '$title : $id' -- ")

    var clicked by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf(now()) }

    Column(modifier = modifier.padding(8.dp)) {
        MainActivity.logger.info(" -- clickComponent '$title : $id' - Column -- ")
        Text(text = "$title - $date")
        if (parentStatus != "") {
            Text(text = "id : $id")
        }
        Button(onClick = {
            clicked = !clicked
            date = now()
        }
        ) {
            Text(text = "-> $title")
        }
        content(date)
    }
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