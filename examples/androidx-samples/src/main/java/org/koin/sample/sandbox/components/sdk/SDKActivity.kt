package org.koin.sample.sandbox.components.sdk

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.GlobalContext
import org.koin.sample.sandbox.R
import org.koin.sample.sandbox.navigation.NavActivity
import org.koin.sample.sandbox.utils.navigateTo

class SDKActivity : AppCompatActivity(), CustomKoinComponent {

    // Here we use Koin instance from CustomKoinComponent
    val sdkViewModel: SDKVIewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sdkViewModel
        title = "SDK ViewModel Activity"

        setContentView(R.layout.sdk_activity)

        assert(getKoin().get<SDKService>() == sdkViewModel.sdkService)

        val globalKoin = GlobalContext.get()
        assert(globalKoin.getOrNull<SDKVIewModel>() == null)
        assert(globalKoin.getOrNull<SDKService>() == null)

        findViewById<Button>(R.id.next_button).setOnClickListener {
            navigateTo<NavActivity>(isRoot = true)
        }

    }
}