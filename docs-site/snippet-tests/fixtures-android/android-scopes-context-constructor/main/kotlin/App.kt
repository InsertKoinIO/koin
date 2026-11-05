package org.koin.sample

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.scope.activityRetainedScope
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.androidx.scope.dsl.activityScope
import org.koin.androidx.scope.dsl.fragmentScope
import org.koin.androidx.scope.fragmentScope
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.scopedOf
import org.koin.core.scope.Scope
import org.koin.dsl.module

// Records the Context that each scope gives to a definition, for "Context in an Activity scope".
class InActivityScope(val context: Context)
class InRetainedScope(val context: Context)
class InFragmentScope(val context: Context)

val contextModule = module {
    activityScope { scopedOf(::InActivityScope) }
    activityRetainedScope { scopedOf(::InRetainedScope) }
    fragmentScope { scopedOf(::InFragmentScope) }
}

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(this@MainApplication)
                modules(contextModule)
            }
        }
    }
}

class MainActivity : ScopeActivity() {
    val value: InActivityScope by inject()
}

class RetainedActivity : AppCompatActivity(), AndroidScopeComponent {
    override val scope: Scope by activityRetainedScope()
    val value: InRetainedScope by inject()
}

class PlainActivity : AppCompatActivity()

class ScopedFragment : Fragment(), AndroidScopeComponent {
    override val scope: Scope by fragmentScope()
    val value: InFragmentScope by inject()
    val fromActivityScope: InActivityScope by inject()
}
