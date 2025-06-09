package org.koin.sample.androidx.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.compose.scope.KoinActivityScope
import org.koin.compose.KoinApplicationPreview
import org.koin.compose.koinInject
import org.koin.compose.module.rememberKoinModules
import org.koin.compose.scope.KoinScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf
import org.koin.sample.androidx.compose.data.MyFactory
import org.koin.sample.androidx.compose.data.MyInnerFactory
import org.koin.sample.androidx.compose.data.MyScoped
import org.koin.sample.androidx.compose.data.MySingle
import org.koin.sample.androidx.compose.di.appModule
import org.koin.sample.androidx.compose.di.secondModule
import org.koin.sample.androidx.compose.viewmodel.SSHViewModel
import org.koin.sample.androidx.compose.viewmodel.UserViewModel
import java.util.UUID


@Composable
fun App(userViewModel: UserViewModel = koinViewModel()) {
    var created by remember { mutableStateOf(true) }

    val users = userViewModel.getUsers()
    println("Loaded users:${users.count()}")

    if (created) {
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            item {
                clickComponent("Main", "", "") { updatedTime ->
                    SingleComposable(parentStatus = updatedTime)
                    FactoryComposable(parentStatus = updatedTime)
                    ViewModelComposable(parentStatus = updatedTime)
                    IsolatedSDKComposable(parentStatus = updatedTime)
                }
            }

            item {
                ButtonForCreate("-X- Main") { created = !created }
            }
            item {
                MyScreen()
            }
        }
    } else {
        Surface(modifier = Modifier.padding(8.dp)) {
            ButtonForCreate("(+) Main") { created = !created }
        }
    }

}

@Composable
fun MyScreen() {

    var someValue by remember { mutableStateOf("initial") }
    val myDependency = koinInject<MyFactory> { parametersOf(someValue) }

    SideEffect {
        println("MyScreen 1")
    }

    Column {
        Text(text = myDependency.id)
        TextField(someValue, onValueChange = { someValue = it })
        Button(onClick = {
            if (someValue == "") someValue = "${UUID.randomUUID()}"
        }) {
            Text("Update")
        }
    }
}
@OptIn(KoinExperimentalAPI::class)
@Preview(name = "1 - Pixel 2 XL", device = Devices.PIXEL_2_XL, locale = "en")
@Preview(name = "2 - Pixel 5", device = Devices.PIXEL_5, locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "3 - Pixel 7 ", device = Devices.PIXEL_7, locale = "ru", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun previewVMComposable(){
    KoinApplicationPreview(application = { modules(appModule) }) {
        ViewModelComposable()
    }
}

@OptIn(KoinExperimentalAPI::class)
@Preview(name = "3 - Pixel 7 ", device = Devices.PIXEL_7, locale = "ru", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "4 - Pixel 7 Pro", device = Devices.PIXEL_7_PRO, locale = "fr")
@Composable
fun previewSingleComposable(){
    KoinApplicationPreview(application = { modules(appModule) }) {
        SingleComposable()
    }
}

@Composable
fun ViewModelComposable(
    parentStatus: String = "- status -",
    myViewModel: SSHViewModel = koinViewModel(parameters = { parametersOf(parentStatus) })
) {
    var created by remember { mutableStateOf(false) }

    if (created) {
        clickComponent("ViewModel", myViewModel.id, parentStatus) {
            ButtonForCreate("-X- ViewModel") { created = !created }
        }
//        Text(text = "ViewModel - lastIds: " + myViewModel.lastIds)
//        myViewModel.saveId()
    } else {
        ButtonForCreate("(+) ViewModel") { created = !created }
    }
}


@Composable
fun SingleComposable(
    parentStatus: String = "- status -",
    mySingle: MySingle = koinInject()
) {
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
fun FactoryComposable(
    parentStatus: String = "- status -",
    myFactory: MyFactory = koinInject(parametersHolder = parametersOf("stable_status"))
) {
    var created by remember { mutableStateOf(false) }
    rememberKoinModules(modules = { listOf(secondModule) })

    if (created) {
        clickComponent("Factory", myFactory.id, parentStatus) {

            KoinScope<MyFactory>("factory_"+myFactory.id) {
                InnerFactoryComposable(parentStatus)
                ScopeComposable("SC_1", parentStatus)
                ScopeComposable("SC_2", parentStatus)
            }
            KoinActivityScope {
                ScopeComposable("SC_3", parentStatus)
            }
            ButtonForCreate("-X- Factory") { created = !created }
        }
    } else {
        ButtonForCreate("(+) Factory") { created = !created }
    }
}

@Composable
//TODO Hold instance until recreate Composable
fun InnerFactoryComposable(
    parentStatus: String,
    myFactory: MyInnerFactory = koinInject { parametersOf("_stable_") }
) {
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
fun ScopeComposable(
    label: String = "- status -",
    parentStatus: String = "- parent status -",
    scopedInstance: MyScoped = koinInject()
) {
    var created by remember { mutableStateOf(false) }
    if (created) {
        Column {
            clickComponent("Scoped[$label]", scopedInstance.id, parentStatus) {}
            ButtonForCreate("-X- Scoped[$label]") { created = !created }
        }

    } else {
        ButtonForCreate("(+) Scoped[$label]") { created = !created }
    }
}