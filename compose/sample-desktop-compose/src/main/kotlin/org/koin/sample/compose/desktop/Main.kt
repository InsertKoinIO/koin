package org.koin.sample.compose.desktop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.logger.Level
import org.koin.dsl.module
import java.util.UUID

class Myfactory {
    val id = UUID.randomUUID().toString()
}

val mod = module {
    factory { Myfactory() }
}

@Composable
@Preview
fun App() {
    KoinApplication(
        application = {
            modules(mod)
            printLogger(Level.DEBUG)
        }
    ) {
        var text by remember { mutableStateOf("Hello, World!") }
        val factory = koinInject<Myfactory>()
        MaterialTheme {
            Button(onClick = {
                text = "Hello, Koin! ${factory.id}"
            }) {
                Text(text)
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}