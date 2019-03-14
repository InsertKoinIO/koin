package org.koin.sample.androidx.components.sdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.junit.Assert.assertNotNull
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.sample.android.R

class SDKActivity : AppCompatActivity(), CustomKoinComponent {

    // Overload to use SDK Koin instance
    // could also overload directly
//    override fun getKoin() = CustomSDK.koinApp.koin

    val sdkViewModel: SDKVIewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assertNotNull(sdkViewModel)
        title = "SDK ViewModel Activity"

        setContentView(R.layout.sdk_activity)
    }
}