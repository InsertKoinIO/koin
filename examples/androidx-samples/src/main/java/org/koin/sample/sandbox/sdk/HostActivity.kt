package org.koin.sample.sandbox.sdk

import android.os.Bundle
import android.widget.Button
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ScopeActivity
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.GlobalContext
import org.koin.sample.sandbox.R
import org.koin.sample.sandbox.components.main.SimpleService
import org.koin.sample.sandbox.components.scope.Session
import org.koin.sample.sandbox.components.sdk.CustomKoinComponent
import org.koin.sample.sandbox.components.sdk.CustomSDK
import org.koin.sample.sandbox.components.sdk.CustomService
import org.koin.sample.sandbox.components.sdk.SDKActivity
import org.koin.sample.sandbox.utils.navigateTo

// AppCompatActivity(), CustomKoinComponent, KoinScopeComponent
class HostActivity : ScopeActivity(), CustomKoinComponent {

    // Inject by Interface - default definition
    val sdkService: SimpleService by inject()
    val sdkSession: Session by inject()

    @OptIn(KoinInternalApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Host Activity with SDK"
        setContentView(R.layout.host_activity)

        findViewById<Button>(R.id.host_button).setOnClickListener {
            navigateTo<SDKActivity>(isRoot = true)
        }

        // ----
        val koinSDK = CustomSDK.koinApp.koin
        val koinSDKRootScope = koinSDK.scopeRegistry.rootScope
        val defaultKoin = GlobalContext.get()

        assert(scope != koinSDKRootScope)
        val sdkSession2 = scope.get<Session>()
        assert(sdkSession == sdkSession2)

        val globalService =
            defaultKoin.get<SimpleService>()//CustomSDK.koinApp.koin.get<SimpleService>()
        assert(sdkService != globalService)
        assert(CustomService().service == sdkService)
    }
}