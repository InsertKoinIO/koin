package org.koin.sample.android.components.sdk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.junit.Assert.assertNotNull
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.sample.android.R

class SDKActivity : AppCompatActivity(), CustomKoinComponent {

    // Here we use Koin instance from CustomKoinComponent
    val sdkViewModel: SDKVIewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assertNotNull(sdkViewModel)
        title = "SDK ViewModel Activity"

        setContentView(R.layout.sdk_activity)
    }
}