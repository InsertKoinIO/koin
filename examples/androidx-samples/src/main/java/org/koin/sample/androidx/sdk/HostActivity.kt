package org.koin.sample.androidx.sdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.host_activity.*
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.scope.activityScope
import org.koin.core.annotation.KoinInternalApi
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

// AppCompatActivity(), CustomKoinComponent, KoinScopeComponent
class HostActivity : ScopeActivity(), CustomKoinComponent {

    // Inject by Interface - default definition
    val sdkService: SimpleService by inject()
    val sdkSession : Session by inject()

    @OptIn(KoinInternalApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Host Activity with SDK"
        setContentView(R.layout.host_activity)

        host_button.setOnClickListener {
            navigateTo<SDKActivity>(isRoot = true)
        }

        // ----
        val koinSDK = CustomSDK.koinApp.koin
        val koinSDKRootScope = koinSDK.scopeRegistry.rootScope
        val defaultKoin = GlobalContext.get()

        assert(scope != koinSDKRootScope)
        val sdkSession2 = scope.get<Session>()
        assert(sdkSession == sdkSession2)

        val globalService = defaultKoin.get<SimpleService>()//CustomSDK.koinApp.koin.get<SimpleService>()
        assert(sdkService != globalService)
        assert(CustomService().service == sdkService)
    }
}