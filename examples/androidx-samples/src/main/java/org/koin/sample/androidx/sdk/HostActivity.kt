package org.koin.sample.androidx.sdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.host_activity.*
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.activityScope
import org.koin.core.component.KoinScopeComponent
import org.koin.core.context.GlobalContext
import org.koin.core.scope.Scope
import org.koin.sample.android.R
import org.koin.sample.androidx.components.main.SimpleService
import org.koin.sample.androidx.components.scope.Session
import org.koin.sample.androidx.components.sdk.CustomKoinComponent
import org.koin.sample.androidx.components.sdk.CustomSDK
import org.koin.sample.androidx.components.sdk.CustomService
import org.koin.sample.androidx.components.sdk.SDKActivity
import org.koin.sample.androidx.utils.navigateTo

class HostActivity : AppCompatActivity(), CustomKoinComponent, KoinScopeComponent {

    override val scope : Scope by activityScope()

    // Inject by Interface - default definition
    val sdkService: SimpleService by inject()
    val sdkSession : Session by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val koinSDK = CustomSDK.koinApp.koin

        val globalService = GlobalContext.get().get<SimpleService>()//CustomSDK.koinApp.koin.get<SimpleService>()
        assert(sdkService != globalService)
        assert(CustomService().service == sdkService)
        assert(sdkSession != null)

        title = "Host Activity with SDK"
        setContentView(R.layout.host_activity)

        host_button.setOnClickListener {
            navigateTo<SDKActivity>(isRoot = true)
        }
    }
}