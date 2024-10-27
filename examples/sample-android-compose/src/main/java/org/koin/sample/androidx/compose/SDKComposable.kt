package org.koin.sample.androidx.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.koin.compose.KoinIsolatedContext
import org.koin.compose.koinInject
import org.koin.sample.androidx.compose.data.sdk.SDKData
import org.koin.sample.androidx.compose.di.IsolatedContextSDK


@Composable
fun IsolatedSDKComposable(
    parentStatus: String = "- status -",
) {
    KoinIsolatedContext(IsolatedContextSDK.koinApp) {
        SDKComposable(parentStatus)
    }
}

@Composable
private fun SDKComposable(
    parentStatus: String = "- status -",
    sdkData: SDKData = koinInject()
) {
    var created by remember { mutableStateOf(false) }

    if (created) {
        clickComponent("sdkData", sdkData.id, parentStatus) {
            ButtonForCreate("-X- sdkData") { created = !created }
        }
    } else {
        ButtonForCreate("(+) sdkData") { created = !created }
    }
}