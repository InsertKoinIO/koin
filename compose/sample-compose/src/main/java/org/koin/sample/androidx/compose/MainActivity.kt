package org.koin.sample.androidx.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import org.koin.sample.androidx.compose.MainActivity.Companion.logger
import org.koin.sample.androidx.compose.data.MyFactory
import org.koin.sample.androidx.compose.data.MySingle
import org.koin.sample.androidx.compose.data.User
import org.koin.sample.androidx.compose.viewmodel.SSHViewModel
import org.koin.sample.androidx.compose.viewmodel.UserViewModel
import java.time.Instant
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

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

@Composable
fun App(userViewModel: UserViewModel = koinViewModel()) {
    var created by remember { mutableStateOf(true) }

    if (created) {
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            item {
                clickComponent("Main", "", "") { updatedTime ->
                    singleComposable(parentStatus = updatedTime)
                    factoryComposable(parentStatus = updatedTime)
                    viewModelComposable(parentStatus = updatedTime)
                }
            }

            item {
                ButtonForCreate("-X- Main") { created = !created }
            }
        }
    } else {
        Surface(modifier = Modifier.padding(8.dp)) {
            ButtonForCreate("(+) Main") { created = !created }
        }
    }

}

fun now(): String = Instant.now().toString()

@Composable
fun viewModelComposable(parentStatus: String, modifier: Modifier = Modifier, myViewModel: SSHViewModel = koinViewModel(parameters = { parametersOf(parentStatus) })) {
    var created by remember { mutableStateOf(false) }

    if (created) {
        clickComponent("ViewModel", myViewModel.id, parentStatus) {
            ButtonForCreate("-X- ViewModel") { created = !created }
        }
        Text(text = "ViewModel - lastIds: "+myViewModel.lastIds)
        myViewModel.saveId()
    } else {
        ButtonForCreate("(+) ViewModel") { created = !created }
    }
}

@Composable
fun singleComposable(parentStatus: String, modifier: Modifier = Modifier, mySingle: MySingle = get()) {
    var created by remember { mutableStateOf(false) }

    if (created) {
        clickComponent("Single", mySingle.id, parentStatus) {
            ButtonForCreate("-X- Single") { created = !created }
        }
    } else {
        ButtonForCreate("(+) Single") { created = !created }
    }
}

@Composable
private fun ButtonForCreate(label: String, onClick: () -> Unit) {
    Button(onClick = {
        onClick()
    }
    ) {
        Text(text = label)
    }
}

@Composable
fun factoryComposable(
    parentStatus: String,
    modifier: Modifier = Modifier,
    myFactory: MyFactory = remember { GlobalContext.get().get { parametersOf(parentStatus) } }
) {
    var created by remember { mutableStateOf(false) }

    if (created) {
        clickComponent("Factory", myFactory.id, parentStatus) {
            innerFactoryComposable(parentStatus)
            ButtonForCreate("-X- Factory") { created = !created }
        }
    } else {
        ButtonForCreate("(+) Factory") { created = !created }
    }
}

@Composable
//TODO Hold instance until recreate Composable
fun innerFactoryComposable(parentStatus: String, modifier: Modifier = Modifier, myFactory: MyFactory = get { parametersOf(parentStatus) }) {
    var created by remember { mutableStateOf(false) }
    if (created) {
        Column {
            clickComponent("InnerFactory", myFactory.id, parentStatus) {}
            ButtonForCreate("-X- InnerFactory") { created = !created }
        }

    } else {
        ButtonForCreate("(+) InnerFactory") { created = !created }
    }
}

@Composable
fun clickComponent(title: String, id: String, parentStatus: String, modifier: Modifier = Modifier, content: @Composable (String) -> Unit) {
    logger.info(" -- clickComponent '$title : $id' -- ")

    var clicked by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf(now()) }
    Column(modifier = modifier.padding(8.dp)) {
        logger.info(" -- clickComponent '$title : $id' - Column -- ")
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